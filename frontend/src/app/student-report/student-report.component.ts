import { Component, OnInit } from '@angular/core';
import { ClassroomService } from '../service/classroom.service';
import { AssignmentCompletion } from '../model/class.model';
import { User } from '../model/user.model';
import { UserService } from '../service/user.servce';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-student-report',
  templateUrl: './student-report.component.html',
  styleUrl: './student-report.component.css'
})
export class StudentReportComponent implements OnInit {

  searchValue: string ='';
  viewReport: boolean = false;
  selectedReport: number = 0;
  selectedCompletion: any;
  assignmentCompletions: AssignmentCompletion[] = [];
  user: User | undefined;
  pieChart: any = [];


  constructor(private classroomService: ClassroomService, private userService: UserService) {}

  ngOnInit(): void {
    this.getCompletions();
    this.getUser();
  }


  getCompletions() {
    this.classroomService.getAllCompletionsByMember().subscribe({
      next:data => {
        this.assignmentCompletions = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getUser() {
    this.userService.getUser().subscribe({
      next:data => {
        this.user = data.body ?? undefined;
      },
      error: error => {
        console.log(error);
      }
    })
  }

  toggleViewReport(index: number, completion: AssignmentCompletion) {
    this.viewReport = !this.viewReport;
    this.selectedReport = index;
    this.selectedCompletion = completion;
    setTimeout(() => {
      this.createPieChart(completion);
    }, 100);
    
  }
  closeViewReport() {
    this.viewReport = false;
  }

  createPieChart(completion: AssignmentCompletion) {

    const labels = ["Correct", "Incorrect"];
    let data = Array(2).fill(0);
    data[0] += completion.answer.filter((obj) => obj===true).length;
    data[1] += completion.answer.filter((obj) => obj===false).length;


    this.pieChart = new Chart("pieChart", {
      type: 'pie',
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: [
            'rgb(34 197 94)',
            'rgb(239 68 68)'
        ],
        hoverOffset: 4
        }],
      },
      options: {
        
        aspectRatio: 2,
        
        
      }
    });

  }


  getAnswer(answer: number[]): string {

    let answerStr = "";

    answer.forEach((ans: number) => {
      answerStr += this.selectedCompletion.assignment.questions.choices[ans];
    });

    return answerStr

  }

  answerExists(index: number): boolean {
    console.log(isNaN(this.selectedCompletion.answer[index]));
    return isNaN(this.selectedCompletion.answer[index]);
  }

  getAnswers(choices: string[], answer: number[]): string {
    let strAnswer = '';

    for (let i=0; i<answer.length;i++) {
      strAnswer += choices[answer[i]] + ' ';
    }

    return strAnswer;
  }

  getCorrectAnswersPercentage(completion: AssignmentCompletion): number {
    const correctAnswersCount = completion.answer.filter(item => item === true).length;
    const totalQuestions = completion.assignment.questions.length ?? 1;
    const accuracy = correctAnswersCount / totalQuestions;
    // Round the accuracy to two decimal places
    return Math.round(accuracy*100);
  }

  getCorrectAnswers(completion: AssignmentCompletion): number {
    return completion.answer.filter(item => item === true).length;
  }

}
