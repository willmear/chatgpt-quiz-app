import { Component, OnInit } from '@angular/core';
import { ClassroomService } from '../service/classroom.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CreateQuizService } from '../service/create-quiz.service';
import { Assignment, AssignmentCompletion, Classroom, Member } from '../model/class.model';

@Component({
  selector: 'app-report-banner',
  templateUrl: './report-banner.component.html',
  styleUrl: './report-banner.component.css'
})
export class ReportBannerComponent implements OnInit{

  assignmentId: number;
  assignment!: Assignment;
  members: Member[] = [];
  assignmentCompletions: AssignmentCompletion[] = [];
  classroom: Classroom | undefined;
  complete: number=0;
  incomplete: number=0;
  unattempted: number=0;

  constructor(private classroomService: ClassroomService, private router: Router, private quizService: CreateQuizService, private route: ActivatedRoute) {
    this.assignmentId = +this.route.snapshot.params['assignmentId'];
    
    
  }

  ngOnInit(): void {
    this.getAssignment();
    this.getCompletions();
  }

  getAssignment() {
    this.classroomService.getAssignment(this.assignmentId).subscribe({
      next:data => {
        if (data.body) {
          this.assignment = data.body;
        }
      },
      error: error => {
        console.log(error);
      }
    })
  }

  getCompletions() {
    this.classroomService.getAssignmentCompletionsAdmin(this.assignmentId).subscribe({
      next:data => {
        this.assignmentCompletions = data.body ?? [];
        this.classroom = this.assignment.classroom;
        this.members = this.classroom.members;
        let members: Member[] = [...this.members];
        this.assignmentCompletions.forEach((completion: AssignmentCompletion) => {
          if (completion.timeOver || completion.exitedBeforeFinished) {
            this.incomplete++;
          } else if (completion.timeOver===false) {
            this.complete++;
          }
          let index = members.findIndex(member => member.id === completion.member.id);
          if (index > -1) {
            members.splice(index, 1);
          };
        });
        this.unattempted = members.length;
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


}