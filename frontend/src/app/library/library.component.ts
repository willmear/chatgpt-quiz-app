import { Component, OnInit } from '@angular/core';
import { Quiz } from '../model/quiz.model';
import { CreateQuizService } from '../service/create-quiz.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrl: './library.component.css'
})
export class LibraryComponent implements OnInit {

  rotationDegrees: number = 270;
  quiz: Quiz[] = [];
  draft: Quiz[] = [];
  recent: boolean = true;
  drafts: boolean = false;

  constructor(private createQuizService: CreateQuizService, private router: Router) {}

  ngOnInit(): void {
    this.getQuiz();
    this.getDraft();
  }

  getQuiz(): void {
    this.createQuizService.getAllQuiz().subscribe({
      next: data => {
        this.quiz = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getDraft(): void {
    this.createQuizService.getAllDraft().subscribe({
      next: data => {
        this.draft = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    });
  }

  openFolder(): void {
    const element = document.querySelector('.rotated-element');
    element?.classList.toggle('rotate-90');
  }

  showModal = false;
  toggleModal(){
    this.showModal = !this.showModal;
  }

  hostQuiz(id: number):void {
    this.router.navigate(['host', id]);
  }

  editQuiz(id: number) {
    this.router.navigate(['design', id]);
  }

  deleteQuiz(id: number) {
    this.createQuizService.deleteQuiz(id).subscribe({
      next: data => {
        if (this.recent) {
          this.getQuiz();
        } else {
          this.getDraft();
        }
      },
      error: error => {
        console.log(error);
      }
    })
  }

  selectTab(tab: string) {
    if (tab === 'recent') {
      this.recent=true;
      this.drafts=false;
    } else if (tab === 'draft') {
      this.drafts=true;
      this.recent=false;
    }
  }

  createAssignment(id: number): void {
    console.log(id);
    this.router.navigate(['homework'], { queryParams: { quizId: id } });
  }

  convertToDate(date: Date): string {

    const date2 = new Date(date);
    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long', // full name of the day of the week
      year: 'numeric', // full numeric representation of the year
      month: 'long', // full name of the month
      day: 'numeric' // numeric day of the month
    };
    
    const writtenDate = date2.toLocaleDateString('en-GB', options);
    
    return writtenDate;
  }

}
