import { Component, OnInit } from '@angular/core';
import { UserService } from '../service/user.servce';
import { Router, RouterStateSnapshot } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  
  constructor(private userService: UserService, private router: Router) {  }

  ngOnInit(): void { }

  classes() {
    this.router.navigate(['/login'], { queryParams: { returnUrl: 'classroom' }});
  }

  isLoggedIn(): boolean {
    return this.userService.isLoggedIn() != null;
  }

  logout() {
    this.userService.clear();
  }

}
