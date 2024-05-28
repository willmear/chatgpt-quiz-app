import { Component, OnInit } from '@angular/core';
import { OpenaiService } from '../service/openai.service';
import { Question } from '../model/question.model';
import { Router } from '@angular/router';
import { CreateQuizService } from '../service/create-quiz.service';


@Component({
  selector: 'app-question-bank',
  templateUrl: './question-bank.component.html',
  styleUrl: './question-bank.component.css'
})
export class QuestionBankComponent implements OnInit {

  questions: Question[] = [];
  selectedQuestions: Question[] = [];
  filteredQuestionsList: Question[] = [];
  searchValue: string ='';
  category = "Question";
  categories = ["Question", "Type", "Topic"]
  isDropdownOpen: boolean = false;


  constructor(private openaiService: OpenaiService, private router: Router, private createQuizService: CreateQuizService) {

    this.openaiService.getAllQuestions().subscribe({
      next:data => {
        console.log(data);
        this.questions = data.body ?? [];
        this.filteredQuestionsList = this.questions;
      },
      error: error => {
        console.log(error);
      }
    });
  }

  ngOnInit(): void {
    
  }

  updateSelection(question: Question, event: any): void {
    if (event.target.checked) {
      // Checkbox is checked, add to the selected items list
      this.selectedQuestions.push(question);
    } else {
      // Checkbox is unchecked, remove from the selected items list
      const index = this.selectedQuestions.indexOf(question);
      if (index !== -1) {
        this.selectedQuestions.splice(index, 1);
      }
    }
  }

  filterResults() {
    if (!this.searchValue) {
      this.filteredQuestionsList = this.questions;
      return;
    }
    
    if (this.category == "Question") {
      this.filteredQuestionsList = this.questions.filter(
        (question:any) => question?.question.toLowerCase().includes(this.searchValue.toLowerCase())
      );
    } else if (this.category == "Type") {
      this.filteredQuestionsList = this.questions.filter(
        (question:any) => question?.questionType.toLowerCase().includes(this.searchValue.toLowerCase())
      );
    } else {
      this.filteredQuestionsList = this.questions.filter(
        (question:any) => question.topics.join(',').toLowerCase().includes(this.searchValue.toLowerCase())
      );
    }
    

  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  closeDropdown() {
    this.isDropdownOpen = false;
  }

  updateSelectedOption(option: string) {
    this.category=option;
    this.toggleDropdown();
  }

  createQuiz() {
    this.createQuizService.createQuiz(this.selectedQuestions).subscribe({
      next:data => {
        console.log(data);
        const quizId = data.body.id;
        this.router.navigate(['/design', quizId]);
      },
      error: error => {
        console.log(error);
      }
    });
  }

  difficulty(percent: number | undefined): string {
    if (percent) {
      if (percent === 0) {
        return "N/A";
      } else if (percent >= 0.7) {
        return "Easy";
      } else if (percent >= 0.5) {
        return "Medium";
      } else {
        return "Hard";
      }
    } else {
      return "N/A";
    }
    

  }

  deleteQuestions() {
    // TODO
  }
}
