import { Injectable, inject } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';

@Injectable()
export class AppPageTitleStrategy extends TitleStrategy {
  private readonly titleService = inject(Title);

  override updateTitle(routerState: RouterStateSnapshot): void {
    const pageTitle = this.buildTitle(routerState);

    if (pageTitle) {
      this.titleService.setTitle(`Detall Sublim - ${pageTitle}`);
    } else {
      this.titleService.setTitle('Detall Sublim');
    }
  }
}
