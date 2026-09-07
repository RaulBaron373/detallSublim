import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';

import { WhatsappButtonComponent } from './whatsapp-button.component';

@Component({
  template: '',
})
class DummyComponent {}

describe('WhatsappButtonComponent', () => {
  let fixture: ComponentFixture<WhatsappButtonComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WhatsappButtonComponent],
      providers: [
        provideRouter([
          {
            path: '**',
            component: DummyComponent,
          },
        ]),
      ],
    }).compileComponents();

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(WhatsappButtonComponent);
    fixture.detectChanges();
  });

  it('should display the WhatsApp button on public routes', async () => {
    await router.navigateByUrl('/catalogo');
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('.whatsapp-button');

    expect(button).not.toBeNull();
  });

  it('should hide the WhatsApp button on private routes', async () => {
    await router.navigateByUrl('/panel');
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('.whatsapp-button');

    expect(button).toBeNull();
  });

  it('should hide the WhatsApp button on nested private routes', async () => {
    await router.navigateByUrl('/producto/15/edit');
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('.whatsapp-button');

    expect(button).toBeNull();
  });

  it('should contain the correct WhatsApp destination and security attributes', async () => {
    await router.navigateByUrl('/contacto');
    fixture.detectChanges();

    const button: HTMLAnchorElement = fixture.nativeElement.querySelector('.whatsapp-button');

    expect(button.href).toContain('wa.me/34618006543');
    expect(button.target).toBe('_blank');
    expect(button.rel).toContain('noopener');
    expect(button.rel).toContain('noreferrer');
    expect(button.getAttribute('aria-label')).toBe('Contactar con Detall Sublim por WhatsApp');
  });
});
