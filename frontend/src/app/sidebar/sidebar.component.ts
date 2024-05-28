import { Component } from '@angular/core';
import { UserService } from '../service/user.servce';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {

  role;

  constructor(private userService: UserService) {

    this.role = userService.getRole();

  }

}
