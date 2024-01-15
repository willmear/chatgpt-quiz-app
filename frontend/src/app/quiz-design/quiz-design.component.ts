import { Component, OnInit, ElementRef, HostListener } from '@angular/core';
import { CreateQuizService } from '../service/create-quiz.service';
import { Quiz, QuizQuestions } from '../model/quiz.model';
import { ActivatedRoute, Router } from '@angular/router';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Question } from '../model/question.model';
import { OpenaiService } from '../service/openai.service';
import { ChatRequest } from '../model/chat-request.model';

@Component({
  selector: 'app-quiz-design',
  templateUrl: './quiz-design.component.html',
  styleUrl: './quiz-design.component.css'
})
export class QuizDesignComponent implements OnInit {

  quiz: any;
  currentQuestion = 0;
  quizId: string = '';
  timeDropdown: boolean = false;
  pointsDropdown: boolean = false;
  addQuestions: boolean = false;
  blankQuestion: boolean = true;
  questionBank: boolean = false;
  questionGenerator: boolean = false;
  times: number[] = [5, 10, 20, 30, 60, 90, 120, 180];
  points: number[] = [100,200,300,400,500];
  topic: string = '';
  searched: boolean = false;
  // generatedQuestions: String[] = ['test', 'test2', 'test3'];
  generatedQuestions: Question[] = [];
  openChoices: boolean = false;

  questions: Question[] = [];
  selectedQuestions: Question[] = [];
  filteredQuestionsList: Question[] = [];
  searchValue: string ='';
  category = "Question";
  categories = ["Question", "Type"];
  isDropdownOpen: boolean = false;

  constructor(private createQuizService: CreateQuizService, private route: ActivatedRoute, private router: Router, private openaiService: OpenaiService, private elRef: ElementRef) {  }

  ngOnInit(): void {
    this.quizId = this.route.snapshot.params['id'];
    this.fetchQuizData(this.quizId);
  }


  fetchQuizData(quizId: string) {
    
    this.createQuizService.getQuiz(quizId).subscribe({
      next:data => {
        console.log(data);
        this.quiz = data.body;
      },
      error: error => {
        console.log(error);
      }
    });
  }

  drop(event: CdkDragDrop<string[]>) {
    if (this.quiz) {
      moveItemInArray(this.quiz.questions, event.previousIndex, event.currentIndex);
    }
  }

  changeCurrentQuestion(index: number) {
    this.currentQuestion = index;
  }

  onChoiceBlur(event: any, index: number) {

    const editedChoice = event.target.innerText;

    if (this.quiz) {
      this.quiz.questions[this.currentQuestion].choices[index] = editedChoice;
    }

  }

  changeCorrectChoice(index: number) {
    if (this.quiz) {
      if (this.quiz.questions[this.currentQuestion].answer.includes(index) && this.quiz.questions[this.currentQuestion].answer.length > 1) {
        this.quiz.questions[this.currentQuestion].answer.splice(this.quiz.questions[this.currentQuestion].answer.indexOf(index),1);
      } else if (!(this.quiz.questions[this.currentQuestion].answer.includes(index))) {
        this.quiz.questions[this.currentQuestion].answer.push(index);
      }
    }
    
  }

  onQuestionBlur(event: any) {
    const editedQuestion = event.target.innerText;
    if (this.quiz) {
      this.quiz.questions[this.currentQuestion].question = editedQuestion;

    }
  }

  displayAnswer(index: number): boolean {

    if (this.quiz) {
      if (this.quiz.questions[this.currentQuestion].answer.includes(index)) {
        return true;
      }
    }

    return false;
  }

  onTitleBlur(event: any) {
    const editedTitle = event.target.innerText;
    if (this.quiz) {
      this.quiz.title = editedTitle;
      console.log(this.quiz);
    }
  }

  publishQuiz() {
    this.createQuizService.publishQuiz(this.quiz, this.quizId).subscribe({
      next: data => {
        console.log(data);
        this.router.navigate(['/library']);
      },
      error: error => {
        console.log(error);
      }
    })
  }

  toggleTimeDropdown() {
    this.timeDropdown = !this.timeDropdown;
  }
  togglePointsDropdown() {
    this.pointsDropdown = !this.pointsDropdown;
  }
  toggleAddQuestions() {
    this.addQuestions = !this.addQuestions;
  }
  updateTime(option: number) {
    this.quiz.questions[this.currentQuestion].timeSeconds = option;
    this.toggleTimeDropdown();
  }
  updatePoints(option: number) {
    this.quiz.questions[this.currentQuestion].points = option;
    this.togglePointsDropdown();
  }

  openQuestionBank() {
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
    if (this.blankQuestion) {
      this.blankQuestion = !this.blankQuestion;
      this.questionBank = !this.questionBank;
    } else if (this.questionGenerator) {
      this.questionGenerator = !this.questionGenerator;
      this.questionBank = !this.questionBank;
    }
  }
  openQuestionGenerator() {
    if (this.questionBank) {
      this.questionBank = !this.questionBank;
      this.questionGenerator = !this.questionGenerator;
    } else if (this.blankQuestion) {
      this.blankQuestion = !this.blankQuestion;
      this.questionGenerator = !this.questionGenerator;
    }
  }
  openBlankQuestion() {
    if (this.questionBank) {
      this.questionBank = !this.questionBank;
      this.blankQuestion = !this.blankQuestion;
    } else if (this.questionGenerator) {
      this.questionGenerator = !this.questionGenerator;
      this.blankQuestion = !this.blankQuestion;
    }
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

  updateSelectedOption(option: string) {
    this.category=option;
    this.toggleDropdown();
  }

  addToQuiz() {
    for (let currQuestion of this.selectedQuestions) {
      let newQuestion: QuizQuestions = {id: undefined, question: currQuestion.question, choices: currQuestion.choices, answer: currQuestion.answer, questionType: currQuestion.questionType, timeSeconds: 20, points: 200 };
      this.quiz.questions.push(newQuestion);
    }
    this.selectedQuestions = [];
    this.toggleAddQuestions();
  }

  deleteQuestion() {
    this.quiz.questions.splice(this.currentQuestion, 1);
  }

  createQuestion(type: string) {
    switch(type) {
      case 'Multiple Choice':
        this.quiz.questions.push({id: undefined, question: 'Question', choices: ['Answer 1', 'Answer 2'], answer: [0], questionType: type, timeSeconds: 20, points: 200});
        break;
      case 'Multiple Answers':
        this.quiz.questions.push({id: undefined, question: 'Question', choices: ['Answer 1', 'Answer 2', 'Answer 3', 'Answer 4'], answer: [0, 1], questionType: type, timeSeconds: 20, points: 200});
        break;
      case 'True/False':
        this.quiz.questions.push({id: undefined, question: 'Question', choices: ['True', 'False'], answer: [0], questionType: type, timeSeconds: 20, points: 200});
        break;
      default:
    }
    this.toggleAddQuestions();
  }

  generateQuestions() {
    if (this.topic.length > 0) {
      const request: ChatRequest = {
        topic: this.topic,
        questionType: 'Multiple Choice',
        questionAmount: '10'
    };
      this.openaiService.createQuestions(request).subscribe({
        next: data => {
          this.generatedQuestions = data.body;
          this.searched = true;
        },
        error: error => {
          console.log(error);
        }
      })
    }
  }

  unhideChoices(index: number) {
    const dropdownId = 'dropdown' + index;
    const dropdownElement = document.getElementById(dropdownId);
    
    if (dropdownElement) {
      dropdownElement.classList.toggle('hidden');
      console.log('done');
    }
  }

  addGeneratedQuestion(index: number) {
    const question = this.generatedQuestions[index];
    let newQuestion: QuizQuestions = {id: undefined, question: question.question, choices: question.choices, answer: question.answer, questionType: question.questionType, timeSeconds: 20, points: 200 };
    this.quiz.questions.push(newQuestion);
  }

  duplicateQuestion() {
    const newQuestion: QuizQuestions = this.quiz.questions[this.currentQuestion];
    newQuestion.id = undefined;
    this.quiz.questions.push(newQuestion);
  }

}
