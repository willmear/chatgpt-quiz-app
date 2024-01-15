import { Component, OnInit } from '@angular/core';
import { UserService } from '../service/user.servce';
import { NewUser } from '../model/user.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit{
  confirmationMessage: string = '';


  constructor(private userService: UserService, private router: Router) {  }

  ngOnInit(): void {
  }

  register(user: NewUser): void {
    console.log(user);
    if (user.password.length < 8) {
      this.confirmationMessage = 'Password must be between greater than 8 characters';
      return;
    }
    if (user.password != user.confirmationPassword) {
      this.confirmationMessage = 'Passwords do not match';
      return;
    }
    if (user.firstname.length < 1 || user.lastname.length < 1) {
      this.confirmationMessage = 'Enter a first and last name';
      return;
    }
    if (!user.role) {
      this.confirmationMessage = 'Pick an account type'
    }
    user.school = "UoB";
    user.confirmationPassword = undefined;
    this.userService.register(user).subscribe({
      next: data => {
        console.log(data);
        this.router.navigate([""]);
        this.confirmationMessage = 'User Registered';
      },
      error: error => {
        console.log(error);
        if (error.status === 409) {
          this.confirmationMessage = 'User Already Exists';
        } else {
          this.confirmationMessage = 'Error Registering User. Try Again.';
        }
      }
    });
  }

}
