package com.detallsublim.app.config;

import com.detallsublim.app.domain.User;
import com.detallsublim.app.repository.UserRepository;
import com.detallsublim.app.security.AuthoritiesConstants;
import com.detallsublim.app.service.UserService;
import com.detallsublim.app.service.dto.AdminUserDTO;
import java.util.Locale;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProductionAdminInitializer implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ProductionAdminInitializer.class);

    private static final int INITIAL_PASSWORD_MIN_LENGTH = 12;

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${DETALL_SUBLIM_INITIAL_ADMIN_LOGIN:}")
    private String adminLogin;

    @Value("${DETALL_SUBLIM_INITIAL_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${DETALL_SUBLIM_INITIAL_ADMIN_PASSWORD:}")
    private String adminPassword;

    public ProductionAdminInitializer(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String normalizedLogin = adminLogin == null ? "" : adminLogin.trim().toLowerCase(Locale.ROOT);

        /*
         * El login debe estar configurado siempre
         * en producción para identificar de forma
         * inequívoca al administrador inicial.
         */
        if (normalizedLogin.isBlank()) {
            throw new IllegalStateException("DETALL_SUBLIM_INITIAL_ADMIN_LOGIN no está configurado.");
        }

        /*
         * Si el administrador ya existe,
         * no volvemos a crearlo ni necesitamos
         * conservar la contraseña inicial.
         */
        var existingUser = userRepository.findOneWithAuthoritiesByLogin(normalizedLogin);

        if (existingUser.isPresent()) {
            User user = existingUser.orElseThrow();

            boolean isAdmin = user.getAuthorities().stream().anyMatch(authority -> AuthoritiesConstants.ADMIN.equals(authority.getName()));

            if (!isAdmin) {
                throw new IllegalStateException("El usuario configurado como administrador inicial existe, " + "pero no tiene ROLE_ADMIN.");
            }

            LOG.info("Production administrator '{}' is already configured", normalizedLogin);

            return;
        }

        /*
         * Estos valores solo son necesarios
         * durante la creación inicial.
         */
        if (adminEmail == null || adminEmail.isBlank()) {
            throw new IllegalStateException("DETALL_SUBLIM_INITIAL_ADMIN_EMAIL no está configurado.");
        }

        if (adminPassword == null || adminPassword.length() < INITIAL_PASSWORD_MIN_LENGTH) {
            throw new IllegalStateException(
                "DETALL_SUBLIM_INITIAL_ADMIN_PASSWORD debe tener " + "al menos " + INITIAL_PASSWORD_MIN_LENGTH + " caracteres."
            );
        }

        String normalizedEmail = adminEmail.trim().toLowerCase(Locale.ROOT);

        if (userRepository.findOneByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalStateException("El email configurado para el administrador " + "ya pertenece a otro usuario.");
        }

        AdminUserDTO admin = new AdminUserDTO();

        admin.setLogin(normalizedLogin);

        admin.setEmail(normalizedEmail);

        admin.setFirstName("Administrador");

        admin.setLastName("Detall Sublim");

        admin.setLangKey("es");

        admin.setActivated(true);

        /*
         * Conservamos ROLE_USER además de ROLE_ADMIN
         * para mantener compatibilidad con todas las
         * comprobaciones actuales del panel.
         */
        admin.setAuthorities(Set.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER));

        User newAdmin = userService.createUser(admin);

        /*
         * createUser genera una contraseña aleatoria
         * porque normalmente los usuarios reciben
         * un correo para establecerla.
         *
         * Para el administrador inicial sustituimos
         * esa contraseña por la proporcionada mediante
         * el secret del servidor.
         */
        newAdmin.setPassword(passwordEncoder.encode(adminPassword));

        /*
         * No necesitamos dejar una resetKey válida
         * para esta cuenta inicial.
         */
        newAdmin.setResetKey(null);
        newAdmin.setResetDate(null);

        userRepository.save(newAdmin);

        LOG.info("Initial production administrator '{}' created successfully", normalizedLogin);
    }
}
