import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import TranslateDirective from './language/translate.directive';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import HasAnyAuthorityDirective from './auth/has-any-authority.directive';
import { ScrollRevealDirective } from './directives/scroll-reveal.directive';

/**
 * Application wide Module
 */
@NgModule({
  imports: [AlertComponent, AlertErrorComponent, TranslateDirective, HasAnyAuthorityDirective, ScrollRevealDirective],
  exports: [
    CommonModule,
    NgbModule,
    FontAwesomeModule,
    AlertComponent,
    AlertErrorComponent,
    TranslateModule,
    TranslateDirective,
    HasAnyAuthorityDirective,
    ScrollRevealDirective,
  ],
})
export default class SharedModule {}
