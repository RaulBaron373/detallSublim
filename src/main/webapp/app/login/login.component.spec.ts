jest.mock('app/core/auth/account.service');
jest.mock('app/login/login.service');

import { ElementRef, signal } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';

import { LoginService } from './login.service';
import LoginComponent from './login.component';

describe('LoginComponent', () => {
  let comp: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockRouter: Router;
  let mockAccountService: AccountService;
  let mockLoginService: LoginService;
  let mockStateStorageService: StateStorageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        FormBuilder,
        AccountService,
        {
          provide: LoginService,
          useValue: {
            login: jest.fn(() => of({})),
          },
        },
      ],
    })
      .overrideTemplate(LoginComponent, '')
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    comp = fixture.componentInstance;

    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));

    mockLoginService = TestBed.inject(LoginService);
    mockAccountService = TestBed.inject(AccountService);
    mockStateStorageService = TestBed.inject(StateStorageService);

    jest.spyOn(mockStateStorageService, 'clearUrl');
  });

  describe('ngOnInit', () => {
    it('should call accountService.identity on Init', () => {
      // GIVEN
      mockAccountService.identity = jest.fn(() => of(null));
      mockAccountService.getAuthenticationState = jest.fn(() => of(null));

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(mockAccountService.identity).toHaveBeenCalled();
    });

    it('should call accountService.isAuthenticated on Init', () => {
      // GIVEN
      mockAccountService.identity = jest.fn(() => of(null));

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(mockAccountService.isAuthenticated).toHaveBeenCalled();
    });

    it('should navigate to panel on Init if authenticated=true', () => {
      // GIVEN
      mockAccountService.identity = jest.fn(() => of(null));
      mockAccountService.isAuthenticated = () => true;

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/panel']);
    });
  });

  describe('ngAfterViewInit', () => {
    it('should set focus to username input after the view has been initialized', () => {
      // GIVEN
      const node = {
        focus: jest.fn(),
      };

      comp.username = signal<ElementRef>(new ElementRef(node));

      // WHEN
      comp.ngAfterViewInit();

      // THEN
      expect(node.focus).toHaveBeenCalled();
    });
  });

  describe('login', () => {
    it('should authenticate the user, clear cached url and navigate to panel', () => {
      // GIVEN
      const credentials = {
        username: 'admin',
        password: 'admin',
      };

      comp.loginForm.patchValue({
        username: 'admin',
        password: 'admin',
      });

      // WHEN
      comp.login();

      // THEN
      expect(comp.authenticationError()).toEqual(false);
      expect(mockLoginService.login).toHaveBeenCalledWith(credentials);
      expect(mockStateStorageService.clearUrl).toHaveBeenCalled();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/panel']);
    });

    it('should ignore cached url and always navigate to panel', () => {
      // GIVEN
      mockStateStorageService.storeUrl('/admin/users');

      // WHEN
      comp.login();

      // THEN
      expect(comp.authenticationError()).toEqual(false);
      expect(mockStateStorageService.clearUrl).toHaveBeenCalled();
      expect(mockStateStorageService.getUrl()).toBeNull();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/panel']);
    });

    it('should stay on login form and show error message on login error', () => {
      // GIVEN
      mockLoginService.login = jest.fn(() => throwError(() => new Error('Authentication failed')));

      // WHEN
      comp.login();

      // THEN
      expect(comp.authenticationError()).toEqual(true);
      expect(mockRouter.navigate).not.toHaveBeenCalled();
    });
  });
});
