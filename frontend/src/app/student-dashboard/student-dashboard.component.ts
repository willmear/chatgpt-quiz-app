import { Component, OnInit } from '@angular/core';
import { ClassroomService } from '../service/classroom.service';
import { Assignment, AssignmentCompletion, Classroom } from '../model/class.model';
import { Router } from '@angular/router';
import { UserService } from '../service/user.servce';
import { User } from '../model/user.model';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-student-dashboard',
  templateUrl: './student-dashboard.component.html',
  styleUrl: './student-dashboard.component.css'
})
export class StudentDashboardComponent implements OnInit{

  classes: Classroom[] = [];
  assignments: Assignment[] = [];
  todoAssignments: Assignment[] = [];
  upcomingAssignments: Assignment[] = [];
  pastAssignments: Assignment[] = [];
  recentCompletions: AssignmentCompletion[][] = [];
  recentCompletionsChart: any[] = [];
  allCompletions: AssignmentCompletion[] = [];
  pieChart: any = [];
  user: any;

  constructor(private classService: ClassroomService, private router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.getAssignments();
    this.getUser();
    this.getClasses();
    this.getRecentCompletions();
    this.getAllCompletionsByMember();
  }

  getRecentCompletions() {
    this.classService.getMostRecentCompletionsForEachClassroom().subscribe({
      next:data => {
        this.recentCompletions = data.body ?? [];
        console.log(this.recentCompletions);
        this.createPerformanceChart();
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getAllCompletionsByMember() {
    this.classService.getAllCompletionsByMember().subscribe({
      next:data => {
        this.allCompletions = data.body ?? [];
        this.createQuestionsChart();
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getUser() {
    this.userService.getUser().subscribe({
      next:data => {
        this.user = data.body;
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getClasses() {
    this.classService.getClassesMember().subscribe({
      next: data => {
        this.classes = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getAssignments(): void {
    this.classService.getAssignments().subscribe({
      next: data => {
        this.assignments = data.body ?? [];
        this.assignments.forEach((assignment: Assignment) => {
          if (new Date(assignment.deadline) < new Date()) {
            this.pastAssignments.push(assignment);
          } else if (new Date(assignment.releaseTime) < new Date() && new Date(assignment.deadline) > new Date()) {
            this.todoAssignments.push(assignment);
          } else if (new Date(assignment.releaseTime) > new Date()) {
            this.upcomingAssignments.push(assignment);
          }
        });
      },
      error: error => {
        console.log(error);
      }
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
    } else if (hoursDifference >= 1) {
      // If less than a day, display in hours
      return `${hoursDifference} hours left`;
  } else {
      return `${hoursDifference*60} minutes left`
  }
    }

    if (hoursDifference >= 24) {
        // If more than a day, display in days
        const daysDifference = Math.floor(hoursDifference / 24);
        return `${daysDifference} days ago`;
    } else if (hoursDifference >= 1) {
      // If less than a day, display in hours
      return `${hoursDifference} hours ago`;
  } else {
      return `${hoursDifference*60} minutes ago`
  }
  }

  navToClass(id: number): void {
    this.router.navigate(['student-class', id]);
  }

  timeOfDay(): string {
    const currentTime = new Date();
    const currentHour = currentTime.getHours();

    if (currentHour >= 0 && currentHour < 12) {
        return "Morning";
    } else if (currentHour >= 12 && currentHour < 17) {
        return "Afternoon";
    } else {
        return "Evening";
    }
  }

  createPerformanceChart() {
    setTimeout(() => {
      this.recentCompletions.forEach((_, index) => {
        if (this.recentCompletions[index].length > 0) {
          this.createRecentPerformanceChart(index);
        }
      });
    }, 100);
  }

  createRecentPerformanceChart(index: number): any {

    const scoresPercentage: number[] = this.recentCompletions[index].map(obj => Math.round((obj.answer.filter((item) => item === true).length / obj.answer.length)*100));
    const labels: string[] = this.recentCompletions[index].map(obj => obj.assignment.name);

    return new Chart(`progressChart${index}`, {
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
        aspectRatio: 3,
        
      }
    });

  }

  createQuestionsChart() {

    const labels = ["Correct", "Incorrect"];
    let data = Array(2).fill(0);
    console.log(this.allCompletions)
    this.allCompletions.forEach((completion: AssignmentCompletion) => {
      
      data[0] += completion.answer.filter((obj) => obj===true).length;
      data[1] += completion.answer.filter((obj) => obj===false).length;
    });

    this.pieChart = new Chart("pieChart", {
      type: 'pie',
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: [
            'rgb(255, 205, 86)',
            'rgb(54, 162, 235)'
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
