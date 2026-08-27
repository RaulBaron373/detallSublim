import { AfterViewInit, Directive, ElementRef, Input, OnDestroy, Renderer2 } from '@angular/core';

export type ScrollRevealAnimation = 'fade-up' | 'fade-left' | 'fade-right' | 'scale-up';

@Directive({
  selector: '[dsScrollReveal]',
  standalone: true,
})
export class ScrollRevealDirective implements AfterViewInit, OnDestroy {
  @Input() reveal: ScrollRevealAnimation = 'fade-up';

  @Input() revealDelay = 0;

  private observer?: IntersectionObserver;

  constructor(
    private readonly elementRef: ElementRef<HTMLElement>,
    private readonly renderer: Renderer2,
  ) {}

  ngAfterViewInit(): void {
    const element = this.elementRef.nativeElement;

    this.renderer.addClass(element, 'ds-reveal');

    this.renderer.addClass(element, `ds-reveal--${this.reveal}`);

    this.renderer.setStyle(element, '--ds-reveal-delay', `${Math.max(0, this.revealDelay)}ms`);

    /*
     * Si el navegador no soporta IntersectionObserver
     * mostramos directamente el contenido.
     */
    if (typeof window === 'undefined' || !('IntersectionObserver' in window)) {
      this.revealElement();
      return;
    }

    /*
     * Respetamos la configuración de accesibilidad
     * del usuario.
     */
    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    if (reducedMotion) {
      this.revealElement();
      return;
    }

    this.observer = new IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (!entry.isIntersecting) {
            return;
          }

          this.revealElement();

          /*
           * Solo animamos una vez.
           */
          this.observer?.unobserve(entry.target);
        });
      },
      {
        threshold: 0.16,

        /*
         * La animación comienza un poco antes
         * de que el elemento entre completamente.
         */
        rootMargin: '0px 0px -8% 0px',
      },
    );

    this.observer.observe(element);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }

  private revealElement(): void {
    this.renderer.addClass(this.elementRef.nativeElement, 'ds-reveal--visible');
  }
}
