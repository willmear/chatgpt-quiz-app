import { Component, OnInit } from '@angular/core';
import { Classroom } from '../model/class.model';
import { ClassroomService } from '../service/classroom.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-student-classroom',
  templateUrl: './student-classroom.component.html',
  styleUrl: './student-classroom.component.css'
})
export class StudentClassroomComponent implements OnInit {
  

  openJoinClass: boolean = false;
  className: string = '';
  joinCode: string = '';
  classes: Classroom[] = [];
  openEditDropdown: boolean = false;
  options: string[] = ['Leave'];
  dropdownStates: boolean[] = [];
  updateIndex!: number;

  constructor(private classroomService: ClassroomService, private router: Router) {}


  ngOnInit(): void {
    this.getClassesMember();
  }

  getClassesMember() {
    this.classroomService.getClassesMember().subscribe({
      next: data => {
        if (data.body != null) {
          this.classes = data.body ?? [];
        }
      },
      error: error => {
        console.log(error);
      }
    });
  }

  toggleEditDropdown(index: number) {
    this.dropdownStates[index] = !this.dropdownStates[index];
  }

  onOptionSelect(option: string, index: number) {
    if (option === this.options[0]) {
      this.updateIndex = index;
    }
  }

  navClass(classroom: Classroom) {
    this.router.navigate(['student-class', classroom.id]);
  }

  toggleJoinClass() {
    
    this.openJoinClass = !this.openJoinClass;
    
  }

  joinClass() {
    if (this.joinCode.length === 8) {
      this.classroomService.joinClass(this.joinCode).subscribe({
        next: data => {
          this.toggleJoinClass();
          this.getClassesMember();
        },
        error: error => {
          console.log(error);
        }
      });
    }
  }

}
