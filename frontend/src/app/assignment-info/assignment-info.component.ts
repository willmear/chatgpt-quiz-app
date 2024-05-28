import { Component, OnInit } from '@angular/core';
import { ClassroomService } from '../service/classroom.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CreateQuizService } from '../service/create-quiz.service';
import { Assignment, AssignmentCompletion, Classroom, Member } from '../model/class.model';
import Chart from 'chart.js/auto';
import { Question } from '../model/question.model';
import { UserService } from '../service/user.servce';
import { User } from '../model/user.model';

@Component({
  selector: 'app-assignment-info',
  templateUrl: './assignment-info.component.html',
  styleUrl: './assignment-info.component.css'
})
export class AssignmentInfoComponent implements OnInit{

  assignmentId: number;
  assignment: Assignment | undefined;
  members: Member[] = [];
  assignmentCompletions: AssignmentCompletion[] = [];
  classroom: Classroom | undefined;
  complete: number=0;
  incomplete: number=0;
  unattempted: number=0;
  averageScore: number=0;
  highest: number=0;
  lowest:number=999;
  averageCompletionTime: number=0;
  toughQuestions: Question[] = [];
  dnf: Member[] = [];
  studentsDnf: User[] = [];
  scoreList: number[] = [];
  scoreOverviewChart: any = [];


  constructor(private classroomService: ClassroomService, private router: Router, private quizService: CreateQuizService, private route: ActivatedRoute,
    private createQuizService: CreateQuizService, private userService: UserService) {
    this.assignmentId = +this.route.snapshot.params['assignmentId'];
    this.getCompletions();
    
  }

  ngOnInit(): void {
  }

  getCompletions() {
    this.classroomService.getAssignmentCompletionsAdmin(this.assignmentId).subscribe({
      next:data => {
        this.assignmentCompletions = data.body ?? [];
        this.assignment = this.assignmentCompletions[0].assignment;
        this.classroom = this.assignment.classroom;
        this.members = this.classroom.members;
        this.dnf = [...this.members];
        let score = 0;
        let time = 0;
        this.assignmentCompletions.forEach((completion: AssignmentCompletion) => {
          let correctAnswers = completion.answer.filter((bool) => bool===true).length;
          this.highest = Math.max(this.highest, correctAnswers);
          this.lowest = Math.min(this.lowest, correctAnswers);
          score += correctAnswers;
          this.scoreList.push(Math.round((correctAnswers/completion.assignment.questions.length)*100))
          time += completion.completionTime;
          if (completion.timeOver || completion.exitedBeforeFinished) {
            this.incomplete++;
          } else if (completion.timeOver===false) {
            this.complete++;
          }
          let index = this.dnf.findIndex(member => member.id === completion.member.id);
          if (index > -1) {
            this.dnf.splice(index, 1);
          };
        });
        this.averageScore = Math.round(score/this.assignmentCompletions.length);
        this.unattempted = this.dnf.length;
        if (this.unattempted > 0) {
          this.getStudentsDnf();
        }
        this.averageCompletionTime = Math.round(time/this.assignmentCompletions.length);
        this.difficultQuestions();
        console.log(this.assignmentCompletions);
        this.createScoreChart();
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getStudentsDnf() {
    this.userService.getUserByIds(this.dnf.map(obj => obj.memberId)).subscribe({
      next: data => {
        this.studentsDnf = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    });
  }

  createScoreChart() {

    let dataset = Array(101).fill(0);
    for (let i = 0; i< this.scoreList.length; i++) {
      dataset[this.scoreList[i]]++;
    }

    const labels = Array(101).fill(0).map((n, i) => n+i);

    console.log(dataset)

    this.scoreOverviewChart = new Chart("scoreOverview", {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          data: dataset,
          backgroundColor: [
            'rgba(54, 162, 235, 0.2)'
          ],
          borderColor: [
            'rgb(54, 162, 235)'
          ],
          borderWidth: 1,
          minBarLength: 5,
        }],
      },
      options: {
        plugins: {
          legend: {
            display: false
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              precision: 0,
              font: {
                size: 16,
              }
            },
            grid: {
              drawOnChartArea: false
            }
          },
          x: {
            beginAtZero: true,
            ticks: {
              callback: function(value, index, values) {
                return value + '%';
              },
              maxTicksLimit: 11,
              stepSize: 10,
              maxRotation: 0,
              minRotation: 0,
              font: {
                size: 16,
              }
            },
            grid: {
              drawOnChartArea: false
            }
          }
        },
        aspectRatio: 5,
        onClick: (evt, item) => {
          if (item.length) {
              // A bar is clicked
              const dataIndex = item[0].index;
              // Call your function with the clicked bar's data index
              // this.handleBarClick(dataIndex);
          }
      }
      },
    });
  }

  calculateTimeDifference(deadline: Date): string {
    const currentTime = new Date();
    const deadlineDate = new Date(deadline);
    const timeDifference = Math.abs(currentTime.getTime() - deadlineDate.getTime());

    const hoursDifference = Math.floor(timeDifference / (1000 * 60 * 60));

    if (deadlineDate.getTime() > currentTime.getTime()) {
      if (hoursDifference >= 24) {
        // If more than a day, display in days
        const daysDifference = Math.floor(hoursDifference / 24);
        return `${daysDifference} days left`;
    } else {
        // If less than a day, display in hours
        return `${hoursDifference} hours left`;
    }
    }

    if (hoursDifference >= 24) {
        // If more than a day, display in days
        const daysDifference = Math.floor(hoursDifference / 24);
        return `${daysDifference} days ago`;
    } else {
        // If less than a day, display in hours
        return `${hoursDifference} hours ago`;
    }
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

  getAverageCompletionTime(): string {
    let hours,minutes,seconds = 0;
    hours = Math.floor(this.averageCompletionTime / 3600);
    minutes = Math.floor((this.averageCompletionTime % 3600) / 60);
    seconds = this.averageCompletionTime % 60;

    if (hours > 0) {
      return `${hours}:${this.formatTime(minutes)}:${this.formatTime(seconds)}`;
    } else {
      return `${this.formatTime(minutes)}:${this.formatTime(seconds)}`;
    }
  }

  private formatTime(time: number): string {
    return time < 10 ? `0${time}` : `${time}`;
  }

  difficultQuestions() {
    let answersCorrect: number[] = new Array(this.assignment?.questions.length).fill(0);;
    
    this.assignmentCompletions.forEach((completion: AssignmentCompletion) => {
      for (let i = 0; i< completion.answer.length; i++) {
        if (completion.answer[i]===true) {
          answersCorrect[i] += 1;
        } else if (completion.answer[i]===false) {
          answersCorrect[i] += 0;
        }
      }
    });

    for (let i = 0; i< answersCorrect.length; i++) {
      
      if (answersCorrect[i]/this.assignmentCompletions.length <= 0.35) {
        let {id, question, choices, answer, questionType, timeSeconds, points, difficulty} = this.assignment!.questions[i];
        let questions: Question = {
          id: id!,
          userId: undefined,
          question: question,
          choices: choices,
          answer: answer,
          topics: undefined,
          generatedAt: new Date,
          questionType: questionType,
          difficulty: difficulty,
        };
        this.toughQuestions.push(questions);
      }
    }
  }

  createQuiz() {
    this.createQuizService.createQuiz(this.toughQuestions).subscribe({
      next:data => {
        console.log(data);
        const quizId = data.body.id;
        this.router.navigate(['/design', quizId]);
      },
      error: error => {
        console.log(error);
      }
    })
  }

}
