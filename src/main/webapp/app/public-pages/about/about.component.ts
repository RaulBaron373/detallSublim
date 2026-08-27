import { Component } from '@angular/core';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-about',
  imports: [SharedModule],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss',
})
export class AboutComponent {}
