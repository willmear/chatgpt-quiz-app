import { Component, OnInit } from '@angular/core';
import { UserService } from '../service/user.servce';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  
  constructor(private userService: UserService, private router: Router) {  }

  ngOnInit(): void { }

  logout() {
    this.userService.clear();
  }

}
