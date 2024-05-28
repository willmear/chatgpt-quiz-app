import { Component, OnInit } from '@angular/core';
import { ClassroomService } from '../service/classroom.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../service/user.servce';
import { CreateQuizService } from '../service/create-quiz.service';
import { Assignment, AssignmentCompletion, Classroom, Member } from '../model/class.model';
import { User } from '../model/user.model';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-assignment-report-students',
  templateUrl: './assignment-report-students.component.html',
  styleUrl: './assignment-report-students.component.css'
})
export class AssignmentReportStudentsComponent implements OnInit{
  
  searchValue: string ='';
  assignmentId: number;
  assignment: Assignment | undefined;
  members: Member[] = [];
  assignmentCompletions: AssignmentCompletion[] = [];
  classroom: Classroom | undefined;
  students: User[] = [];
  viewStudent: boolean = false;
  selectedStudent: number = 0;
  selectedCompletion: any;
  pieChart: any = [];

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
        this.getStudents();
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getStudents() {
    this.userService.getUserByIds(this.assignmentCompletions.map(obj => obj.member.memberId)).subscribe({
      next:data => {
        this.students = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getCorrectAnswersPercentage(completion: AssignmentCompletion): number {
    const correctAnswersCount = completion.answer.filter(item => item === true).length;
    const totalQuestions = this.assignment?.questions.length ?? 1;
    const accuracy = correctAnswersCount / totalQuestions;
    // Round the accuracy to two decimal places
    return Math.round(accuracy*100);
  }

  getCorrectAnswers(completion: AssignmentCompletion): number {
    return completion.answer.filter(item => item === true).length;
  }

  toggleViewStudent(index: number, completion: AssignmentCompletion) {
    this.viewStudent = !this.viewStudent;
    this.selectedStudent = index;
    this.selectedCompletion = completion;
    setTimeout(() => {
      this.createPieChart(completion);
    }, 100);
  }
  closeViewStudent() {
    this.viewStudent = false;
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

}
