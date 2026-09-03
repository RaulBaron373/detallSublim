import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

import TranslateDirective from './translate.directive';
import { of } from 'rxjs';

@Component({
  imports: [TranslateDirective],
  template: ` <div jhiTranslate="test"></div> `,
})
class TestTranslateDirectiveComponent {}

describe('TranslateDirective Tests', () => {
  let fixture: ComponentFixture<TestTranslateDirectiveComponent>;
  let translateService: TranslateService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot(), TestTranslateDirectiveComponent],
    });
  }));

  beforeEach(() => {
    translateService = TestBed.inject(TranslateService);
    fixture = TestBed.createComponent(TestTranslateDirectiveComponent);
  });

  it('should change HTML', () => {
    const spy = jest.spyOn(translateService, 'get');

    fixture.detectChanges();

    expect(spy).toHaveBeenCalled();
  });

  it('should sanitize unsafe translated HTML before rendering it', () => {
    jest.spyOn(translateService, 'get').mockReturnValue(of('<img src="x" onerror="alert(1)"><strong>Safe content</strong>'));

    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement.querySelector('div');

    expect(element.innerHTML).not.toContain('onerror');
    expect(element.innerHTML).toContain('<strong>Safe content</strong>');
  });
});
