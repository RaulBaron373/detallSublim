import { Directive, ElementRef, OnChanges, OnDestroy, OnInit, SecurityContext, inject, input } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { translationNotFoundMessage } from 'app/config/translation.config';
import { DomSanitizer } from '@angular/platform-browser';

/**
 * A wrapper directive on top of the translation pipe as the inbuilt translation directive from ngx-translate is too verbose and buggy
 */
@Directive({
  selector: '[jhiTranslate]',
})
export default class TranslateDirective implements OnChanges, OnInit, OnDestroy {
  readonly jhiTranslate = input.required<string>();
  readonly translateValues = input<Record<string, unknown>>();

  private readonly directiveDestroyed = new Subject();

  private readonly el = inject(ElementRef);
  private readonly translateService = inject(TranslateService);
  private readonly sanitizer = inject(DomSanitizer);

  ngOnInit(): void {
    this.translateService.onLangChange.pipe(takeUntil(this.directiveDestroyed)).subscribe(() => {
      this.getTranslation();
    });
    this.translateService.onTranslationChange.pipe(takeUntil(this.directiveDestroyed)).subscribe(() => {
      this.getTranslation();
    });
  }

  ngOnChanges(): void {
    this.getTranslation();
  }

  ngOnDestroy(): void {
    this.directiveDestroyed.next(null);
    this.directiveDestroyed.complete();
  }

  private getTranslation(): void {
    this.translateService
      .get(this.jhiTranslate(), this.translateValues())
      .pipe(takeUntil(this.directiveDestroyed))
      .subscribe({
        next: value => {
          this.el.nativeElement.innerHTML = this.sanitizer.sanitize(SecurityContext.HTML, value) ?? '';
        },
        error: () => `${translationNotFoundMessage}[${this.jhiTranslate()}]`,
      });
  }
}
