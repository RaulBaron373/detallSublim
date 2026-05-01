import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';
import SharedModule from 'app/shared/shared.module';

import { User } from '../user-management.model';
import { NgClass } from '@angular/common';

@Component({
  selector: 'jhi-user-mgmt-detail',
  templateUrl: './user-management-detail.component.html',
  imports: [RouterModule, SharedModule, NgClass],
})
export default class UserManagementDetailComponent {
  user = input<User | null>(null);
}
