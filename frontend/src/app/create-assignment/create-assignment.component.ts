import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClassroomService } from '../service/classroom.service';
import { Assignment, Classroom, NewAssignment, NewAssignmentForm } from '../model/class.model';
import { CreateQuizService } from '../service/create-quiz.service';
import { Quiz, QuizQuestions } from '../model/quiz.model';
import { Question } from '../model/question.model';

@Component({
  selector: 'app-create-assignment',
  templateUrl: './create-assignment.component.html',
  styleUrl: './create-assignment.component.css'
})
export class CreateAssignmentComponent implements OnInit {

  classId: number;
  classroom: Classroom | undefined;
  classes: Classroom[] = [];
  quiz: Quiz | undefined;
  quizzes: Quiz[] = [];
  quizId: number;
  errorMessage: string = '';
  
  constructor(private route: ActivatedRoute, private classroomService: ClassroomService, private quizService: CreateQuizService, private router: Router) {
    this.classId = +this.route.snapshot.queryParams['classId'];
    this.quizId = +this.route.snapshot.queryParams['quizId'];
  }
  
  ngOnInit(): void {
    this.getAllQuiz();
    this.getClasses();
    if (!isNaN(this.classId)) {
      this.getClassroom();
    }
    else if (!isNaN(this.quizId)) {
      this.getQuiz();
    }
  }

  getClassroom(): void {
    this.classroomService.getClass(this.classId).subscribe({
      next: data => {
        this.classroom = data.body || undefined;
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getQuiz(): void {
    this.quizService.getQuiz(String(this.quizId)).subscribe({
      next: data => {
        this.quiz = data.body || undefined;
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getClasses(): void {
    this.classroomService.getClassesOwned().subscribe({
      next: data => {
        this.classes = data.body ?? [];
      }
    })
  }

  getAllQuiz(): void {
    this.quizService.getAllQuiz().subscribe({
      next:data => {
        this.quizzes = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    })
  }

  createAssignment(form: NewAssignmentForm) {

    if (form.multipleAttempts != true) {
      form.multipleAttempts = false;
    }

    this.errorMessage = '';

    let releaseTime: Date = new Date(form.releaseTime);
    const deadline: Date = new Date(form.deadline);
    const quiz: QuizQuestions[] = form.questions;

    if (releaseTime >= deadline) {
      this.errorMessage = 'Please extend deadline until after the release date.'
      return;
    } else if (isNaN(releaseTime.getDate()) && deadline.getHours() <= new Date().getHours()) {
      this.errorMessage = 'Select a release time or select a later deadline.'
      return;
    } else if (isNaN(deadline.getTime())) {
      this.errorMessage = 'Please select a deadline.'
      return;
    } else if (quiz === undefined && isNaN(this.quizId)) {
      this.errorMessage = 'Please select a quiz.'
      return;
    }

    let newAssignment: NewAssignment = {
      questions: form.questions,
      classroom: form.classroom,
      releaseTime: releaseTime,
      deadline: deadline,
      name: form.name,
      timer: form.timer,
      multipleAttempts: form.multipleAttempts
    };

    if (newAssignment.classroom === undefined && !isNaN(this.classId)) {
      newAssignment.classroom = this.classroom!;
    }
    if (newAssignment.questions === undefined && !isNaN(this.quizId)) {
      newAssignment.questions = this.quizzes.find(obj => obj.id === this.quizId)?.questions!;
    }
    this.classroomService.createAssignment(newAssignment).subscribe({
      next:data => {
        if (!isNaN(this.classId)) {
          this.router.navigate(['class', this.classId]);
        } else {
          this.router.navigate(['library']);
        }
      },
      error: error => {
        console.log(error);
      }
    });

  }

}
