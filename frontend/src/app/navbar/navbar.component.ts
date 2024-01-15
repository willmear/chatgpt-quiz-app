import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CreateQuizService } from '../service/create-quiz.service';
import { Question } from '../model/question.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  isDropdownOpen: boolean = false;
  options: string[] = ["Blank Quiz", "Create From Question Bank"]

  constructor(private router: Router, private createQuizService: CreateQuizService) {}

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }
  closeDropdown(): void {
    this.isDropdownOpen = false;
  }


  navigate(option: string): void {

    // Make the post request once inside quiz design not before

    if (option===this.options[0]) {
      const questions: Question[] = [];
      this.createQuizService.createQuiz(questions).subscribe({
        next: data => {
          const quizId = data.body.id;
          this.router.navigate(['/design-quiz', quizId]);
        },
        error: error => {
          console.log(error);
        }
      });
    } else if (option===this.options[1]) {
      this.router.navigate(['/question-bank']);
    }

    

    
  }
}
