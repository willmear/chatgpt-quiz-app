import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClassroomService } from '../service/classroom.service';
import { AssignmentCompletion, Classroom, Member } from '../model/class.model';
import { UserService } from '../service/user.servce';
import { User } from '../model/user.model';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-manage-class',
  templateUrl: './manage-class.component.html',
  styleUrl: './manage-class.component.css'
})
export class ManageClassComponent implements OnInit {

  classId: number;
  classroom: Classroom | undefined;
  completions: AssignmentCompletion[] = [];
  memberCompletions: AssignmentCompletion[] = [];
  students: User[] = [];
  viewStudent: boolean = false;
  selectedStudent: number = 0;
  progressChart: any = [];
  questionTypeChart: any = [];
  memberId: number;

  constructor(private route: ActivatedRoute, private classroomService: ClassroomService, private userService: UserService) {
    this.classId = this.route.snapshot.params['id'];
    this.memberId = +this.route.snapshot.queryParams['memberId'];
  }

  ngOnInit(): void {
    this.getClassroom();
    this.getCompletions();
    
  }

  getClassroom() {
    this.classroomService.getClass(this.classId).subscribe({
      next: data => {
        this.classroom = data.body ?? undefined;
        this.getStudents();
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getCompletions() {
    this.classroomService.getAllCompletionsInClass(this.classId).subscribe({
      next:data => {
        this.completions = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getStudents() {
    this.userService.getUserByIds(this.classroom!.members.map(obj => obj.memberId)).subscribe({
      next:data => {
        this.students = data.body ?? [];
        if(this.memberId) {
          this.toggleViewStudentsParams();
        }
      },
      error: error => {
        console.log(error);
      }
    });
  }

  removeStudent(student: User) {
    const index = this.classroom!.members.map(obj => obj.memberId).indexOf(student.id);
    this.classroomService.removeStudent(this.classroom!.members[index].id, this.classId).subscribe({
      next:data => {
        this.getClassroom();
      },
      error: error => {
        console.log(error);
        
      }
    });
  }

  toggleViewStudent(index: number) {
    this.selectedStudent = index;
    this.viewStudent = !this.viewStudent;
    this.getStudentCompletions(this.students[index].id);
  }

  toggleViewStudentsParams() {
    this.viewStudent = !this.viewStudent;
    this.getStudentCompletions(this.memberId);
  }

  closeViewStudent() {
    this.viewStudent = false;
  }

  getStudentCompletions(memberId: number) {
    this.classroomService.getAllCompletionsMember(this.classId, memberId).subscribe({
      next:data => {
        this.memberCompletions = data.body ?? [];
        this.createProgressChart();
        this.createQuestionTypeChart();
      },
      error: error => {
        console.log(error);
      }
    });
  }

  createProgressChart() {

    const scoresPercentage: number[] = this.memberCompletions.map(obj => Math.round((obj.answer.filter((item) => item === true).length / obj.answer.length)*100));
    const labels: string[] = this.memberCompletions.map(obj => obj.assignment.name);

    this.progressChart = new Chart("progressChart", {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          data: scoresPercentage,
          fill: false,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1
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
              callback: function(value, index, values) {
                return value + '%';
              },
              precision: 0,
              font: {
                size: 16,
              }
            },
          },
          x: {
            ticks: {
              font: {
                size: 16,
              }
            },
          }
        },
        aspectRatio: 4,
        
      }
    });
  }

  createQuestionTypeChart() {
    const labels = ["True/False", "Multiple Answers", "Multiple Choice", "Ordering", "Drag and Drop", "Fill The Blanks"];
    let data = labels.map(type => ({ type, correct: 0, completed: 0 }));

    this.memberCompletions.forEach((completion: AssignmentCompletion) => {
      completion.assignment.questions.forEach((question, i) => {
        const typeIndex = labels.indexOf(String(question.questionType));
        if (typeIndex !== -1) {
          data[typeIndex].completed++;
          if (completion.answer[i]) {
            data[typeIndex].correct++;
          }
        }
      });
    });

    this.questionTypeChart = new Chart("questionTypeChart", {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          data: data.map(obj => Math.round((obj.correct/obj.completed)*100)),
          backgroundColor: [
            'rgba(54, 162, 235, 0.2)'
          ],
          borderColor: [
            'rgb(54, 162, 235)'
          ],
          borderWidth: 1,
          minBarLength: 5,
        }]
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
              callback: function(value, index, values) {
                return value + '%';
              },
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
            ticks: {
              font: {
                size: 16,
              }
            },
            grid: {
              drawOnChartArea: false
            }
          }
        },
        aspectRatio: 4
      }
    });

  }
}
