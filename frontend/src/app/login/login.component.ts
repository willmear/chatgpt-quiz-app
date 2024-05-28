import { Component, OnInit } from '@angular/core';
import { UserService } from '../service/user.servce';
import { AuthUser } from '../model/user.model';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{

  confirmationMessage:string = "";
  returnUrl: string;

  constructor(private userService: UserService, protected router: Router, private route: ActivatedRoute) {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  ngOnInit(): void {
  }

  login(user: AuthUser): void {
    if (user.password.length < 8 || user.password.length > 16) {
      this.confirmationMessage = 'Password must be between 8 and 16 characters';
      return;
    }
    this.userService.authenticate(user).subscribe({
      next: data => {
        console.log(data);
        const accessToken = data.body?.access_token;
        const role = data.body?.role;
        if (role) {
          this.userService.setRole(role);
        }
        if (accessToken) {
          this.userService.setToken(accessToken);
        }
        this.userService.setCurrentlyLoggedIn(user.email);
        if (this.returnUrl !== '/') {
          if (this.returnUrl === 'classroom') {
            if (role === 'TEACHER') {
              this.router.navigate(['class']);
            } else if (role === 'STUDENT') {
              this.router.navigate(['student-class']);
            }
          } else {
            this.router.navigate([this.returnUrl]);
          }
        } else if (role === 'TEACHER') {
          this.router.navigate(['teacher-dashboard']);
        } else if (role === 'STUDENT') {
          this.router.navigate(['student-dashboard']);
        } else {
          this.router.navigate([this.returnUrl]);
        }
      },
      error: error => {
        console.log(error);
        this.confirmationMessage = "Error Logging In. Try Again."
      }
    });
    this.ngOnInit();
  }
  

}
