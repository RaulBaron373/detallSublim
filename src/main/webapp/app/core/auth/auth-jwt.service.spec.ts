import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';
import { StateStorageService } from './state-storage.service';

describe('Auth JWT', () => {
  let service: AuthServerProvider;
  let httpMock: HttpTestingController;
  let mockStorageService: StateStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    mockStorageService = TestBed.inject(StateStorageService);
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(AuthServerProvider);
  });

  describe('Get Token', () => {
    it('should return empty token if not found in session storage', () => {
      const result = service.getToken();
      expect(result).toEqual('');
    });

    it('should return token from session storage', () => {
      sessionStorage.setItem('jhi-authenticationToken', JSON.stringify('sessionStorageToken'));
      const result = service.getToken();
      expect(result).toEqual('sessionStorageToken');
    });
  });

  describe('Login', () => {
    it('should save the authentication token in session storage', () => {
      // GIVEN
      mockStorageService.storeAuthenticationToken = jest.fn();

      // WHEN
      service.login({ username: 'John', password: '123' }).subscribe();
      httpMock.expectOne('api/authenticate').flush({ id_token: '1' });

      // THEN
      httpMock.verify();
      expect(mockStorageService.storeAuthenticationToken).toHaveBeenCalledWith('1');
    });
  });

  describe('Logout', () => {
    it('should clear storage', () => {
      // GIVEN
      mockStorageService.clearAuthenticationToken = jest.fn();

      // WHEN
      service.logout().subscribe();

      // THEN
      expect(mockStorageService.clearAuthenticationToken).toHaveBeenCalled();
    });
  });
});
