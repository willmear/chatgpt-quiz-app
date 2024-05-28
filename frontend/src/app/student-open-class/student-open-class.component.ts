import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClassroomService } from '../service/classroom.service';
import { Assignment, AssignmentResponse, Classroom } from '../model/class.model';

@Component({
  selector: 'app-student-open-class',
  templateUrl: './student-open-class.component.html',
  styleUrl: './student-open-class.component.css'
})
export class StudentOpenClassComponent implements OnInit {
  

  classId: number;
  currentClass: Classroom | undefined;
  assignments: AssignmentResponse[] = [];
  todoAssignments: AssignmentResponse[] = [];
  upcomingAssignments: AssignmentResponse[] = [];
  pastAssignments: AssignmentResponse[] = [];

  constructor(private route: ActivatedRoute, private classroomService: ClassroomService, private router: Router) {
    this.classId = this.route.snapshot.params['id'];
  }

  ngOnInit(): void {
    this.getClassroom();
    this.getAssignments();
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
    })
  }

  getAssignments() {
    this.classroomService.getAssignmentsMember(this.classId).subscribe({
      next: data => {
        this.assignments = data.body ?? [];
        this.assignments.forEach((assignmentResponse: AssignmentResponse) => {
          if (new Date(assignmentResponse.assignment.deadline) < new Date()) {
            this.pastAssignments.push(assignmentResponse);
          } else if (new Date(assignmentResponse.assignment.releaseTime) < new Date() && new Date(assignmentResponse.assignment.deadline) > new Date()) {
            this.todoAssignments.push(assignmentResponse);
          } else if (new Date(assignmentResponse.assignment.releaseTime) > new Date()) {
            this.upcomingAssignments.push(assignmentResponse);
          }
        });
      },
      error: error => {
        console.log(error);
      }
    })
  }
  
  takeQuiz(id: number, index: number) {
    if (this.todoAssignments[index].assignment.multipleAttempts || this.todoAssignments[index].completions.length === 0) {
      this.router.navigate(['assignment', this.classId, id]);
    }
        
  }

  calculateReleaseTime(releaseTime: Date): string {
    const currentTime = new Date();
    const deadlineDate = new Date(releaseTime);
    const timeDifference = Math.abs(currentTime.getTime() - deadlineDate.getTime());

    const hoursDifference = Math.floor(timeDifference / (1000 * 60 * 60));


    if (hoursDifference >= 24) {
        // If more than a day, display in days
      const daysDifference = Math.floor(hoursDifference / 24);
      return `Opens in ${daysDifference} days`;
    } else {
        // If less than a day, display in hours
      return `Opens in ${hoursDifference} hours`;
    }
    
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
        return `Ends in ${daysDifference} days`;
    } else {
        // If less than a day, display in hours
        return `Ends in ${hoursDifference} hours`;
    }
    }

    if (hoursDifference >= 24) {
        // If more than a day, display in days
        const daysDifference = Math.floor(hoursDifference / 24);
        return `Ended ${daysDifference} days ago`;
    } else {
        // If less than a day, display in hours
        return `Ended ${hoursDifference} hours ago`;
    }
  }

}
