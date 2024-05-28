import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { ClassroomService } from '../service/classroom.service';
import { Assignment, AssignmentCompletion, Classroom } from '../model/class.model';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-open-classroom',
  templateUrl: './open-classroom.component.html',
  styleUrl: './open-classroom.component.css'
})
export class OpenClassroomComponent implements OnInit{

  classId: number;
  currentClass: Classroom | undefined;
  assignments: Assignment[] = [];
  completions: AssignmentCompletion[] = [];
  totalQuestions: number = 0;
  accuracy: number = 0;
  averageMap: Record<string, number> | undefined;
  progressChart: any = [];
  mapSize: number = 0;

  constructor(private route: ActivatedRoute, private classroomService: ClassroomService, private router: Router) {
    this.classId = this.route.snapshot.params['id'];
  }

  ngOnInit(): void {
    this.getClassroom();
    this.getAssignments();
    this.getCompletions();
    this.getClassAveragePerAssignment();
  }

  getClassroom() {
    this.classroomService.getClass(this.classId).subscribe({
      next: data => {
        if (data.body != null) {
          this.currentClass = data.body;
        }
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
        this.completions.forEach((completion: AssignmentCompletion) => {
          this.totalQuestions += completion.answer.length;
          this.accuracy += completion.answer.filter(obj => obj === true).length;
        });
        this.accuracy = Number((this.accuracy / this.totalQuestions).toFixed(2));
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getAssignments() {
    this.classroomService.getAssignmentsTeacher(this.classId).subscribe({
      next: data => {
        this.assignments = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getClassAveragePerAssignment() {
    this.classroomService.getAverageForAllAssignments(this.classId).subscribe({
      next:data => {
        if (data.body) {
          this.averageMap = data.body
          console.log(this.averageMap);
          this.mapSize = Object.keys(this.averageMap).length;
          this.createProgressChart();
        }
        
      },
      error: error => {
        console.log(error);
      }
    });
  }

  createAssignment() {
    this.router.navigate(['homework'], { queryParams: { classId: this.classId } });
  }

  viewQuiz(id: number) {
    this.router.navigate(['info', id]); 
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

  createProgressChart() {

    if (this.averageMap) {
      let scoresPercentage: number[] = [];
      let labels: string[] = [];

      // const scoresPercentage: number[] = Array.from(this.averageMap.values());
      // const labels: string[] = Array.from(this.averageMap.keys());

      for (const [key, value] of Object.entries(this.averageMap)) {
        labels.push(key);
        scoresPercentage.push(Math.round(value));
      }

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
    } else {
      console.log("average map undefined");
    }

    }
    

    


}
