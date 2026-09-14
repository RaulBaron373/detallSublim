import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';

import NavbarComponent from './navbar.component';

describe('Navbar Component', () => {
  let comp: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [NavbarComponent, TranslateModule.forRoot()],
    })
      .overrideTemplate(NavbarComponent, '')
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    comp = fixture.componentInstance;
  });

  it('should start collapsed', () => {
    expect(comp.isNavbarCollapsed()).toBe(true);
  });

  it('should toggle navbar', () => {
    comp.toggleNavbar();

    expect(comp.isNavbarCollapsed()).toBe(false);

    comp.toggleNavbar();

    expect(comp.isNavbarCollapsed()).toBe(true);
  });

  it('should collapse navbar', () => {
    comp.isNavbarCollapsed.set(false);

    comp.collapseNavbar();

    expect(comp.isNavbarCollapsed()).toBe(true);
  });
});
