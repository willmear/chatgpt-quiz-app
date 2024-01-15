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
  filter: string = 'Recent';
  // quiz: Quiz[] = [{id:1, userId:1, questions:[], title:'', isDraft:false, createdAt:Date.now as unknown as Date, lastPlayed:Date.now as unknown as Date}];
  quiz: Quiz[] = [];
  draft: Quiz[] = [];
  selectedTab: string = 'recent';

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

  selectTab(tab: string): void {
    this.selectedTab = tab;
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
        this.getQuiz()
      },
      error: error => {
        console.log(error);
      }
    })
  }

}
