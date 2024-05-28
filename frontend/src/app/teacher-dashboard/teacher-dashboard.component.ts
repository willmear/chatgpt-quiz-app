import { Component, OnInit } from '@angular/core';
import { Assignment, AssignmentCompletion, Classroom, Member } from '../model/class.model';
import { Router } from '@angular/router';
import { ClassroomService } from '../service/classroom.service';
import { UserService } from '../service/user.servce';
import { User } from '../model/user.model';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-teacher-dashboard',
  templateUrl: './teacher-dashboard.component.html',
  styleUrl: './teacher-dashboard.component.css'
})
export class TeacherDashboardComponent implements OnInit {

  classes: Classroom[] = [];
  assignments: Assignment[] = [];
  todoAssignments: Assignment[] = [];
  upcomingAssignments: Assignment[] = [];
  pastAssignments: Assignment[] = [];
  user: any;
  users: User[] = [];
  completions: Assignment[] = [];
  completionStudents: User[] = [];
  allCompletions: AssignmentCompletion[][] = [];

  constructor(private classService: ClassroomService, private router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.getAssignments();
    this.getUser();
    this.getClasses();
    this.getCompletions();
    this.getCompletionsForEachClassroomTeacher();
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

  getUsers() {

    const memberIds = this.classes.flatMap((classroom: Classroom) =>
      classroom.members.map(member => member.memberId)
    );

    this.userService.getUserByIds(memberIds).subscribe({
      next: data => {
        this.users = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getCompletionsForEachClassroomTeacher() {
    this.classService.getCompletionsForEachClassroomTeacher().subscribe({
      next: data => {
        this.allCompletions = data.body ?? [];
        console.log(this.allCompletions)
        this.createPerformanceChart();
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getCompletions() {
    this.classService.getAssignmentsAdmin().subscribe({
      next:data => {
        this.completions = data.body ?? [];
        // let ids: number[] = [];
        // this.completions.slice(0,5).forEach((completion: AssignmentCompletion) => {
        //   ids.push(completion.member.memberId);
        // });
        // this.getStudents(ids);
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getStudents(ids: number[]) {
    this.userService.getUserByIds(ids).subscribe({
      next:data => {
        this.completionStudents = data.body ?? [];
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getClasses() {
    this.classService.getClassesOwned().subscribe({
      next: data => {
        this.classes = data.body ?? [];
        if (this.classes.length > 0) {
          this.getUsers();
        }
      },
      error: error => {
        console.log(error);
      }
    });
  }

  getAssignments(): void {
    this.classService.getAssignmentsAdmin().subscribe({
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
      this.allCompletions.forEach((_, index) => {
        this.createRecentPerformanceChart(index);
      });
    }, 100);
  }

  createRecentPerformanceChart(index: number): any {

    // const scoresPercentage: number[] = this.allCompletions[index].map(obj => Math.round((obj.answer.filter((item) => item === true).length / obj.answer.length)*100));
    // const labels = this.allCompletions[index].map(obj => obj.assignment.name);

    const assignmentAverages = this.calculateAssignmentAverages(index);

    const labels = Array.from(assignmentAverages.keys());
    let data = Array.from(assignmentAverages.values());
    data = data.map(obj => Math.round(obj));


    return new Chart(`progressChart${index}`, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          data: data,
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

  calculateAssignmentAverages(index: number): Map<string, number> {
    const assignmentAverages = new Map<string, number>();


        this.allCompletions[index].forEach(completion => {
            const assignmentName = completion.assignment.name;
            const scorePercentage = (completion.answer.filter(item => item === true).length / completion.answer.length) * 100;

            if (!assignmentAverages.has(assignmentName)) {
                assignmentAverages.set(assignmentName, scorePercentage);
            } else {
                const currentAverage = assignmentAverages.get(assignmentName);
                assignmentAverages.set(assignmentName, currentAverage! + scorePercentage);
            }
        });

    // Calculate the average for each assignment
    assignmentAverages.forEach((totalScore, assignmentName) => {
        const completionsCount = this.allCompletions.reduce((count, completions) => {
            return count + completions.filter(completion => completion.assignment.name === assignmentName).length;
        }, 0);
        assignmentAverages.set(assignmentName, totalScore / completionsCount);
    });

    return assignmentAverages;
  }

  sortMembers(classroom: Classroom): Member[] {
    return classroom.members.sort((a, b) => a.memberId - b.memberId);
  }

  getUserName(memberId: number): string {
    const user = this.users.find(u => u.id === memberId);
    return user ? `${user.firstname} ${user.lastname}` : 'Unknown';
  }

  goToManageStudents(memberId: number, classId: number) {
    this.router.navigate(['class', classId, 'manage'], { queryParams: { memberId: memberId } });
  }

}
