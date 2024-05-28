import { Component, OnInit } from '@angular/core';
import { ClassroomService } from '../service/classroom.service';
import { Classroom, NewClassroom } from '../model/class.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-classroom',
  templateUrl: './classroom.component.html',
  styleUrl: './classroom.component.css'
})
export class ClassroomComponent implements OnInit {

  openCreateClass: boolean = false;
  className: string = '';
  classes: Classroom[] = [];
  openEditDropdown: boolean = false;
  editClass: boolean = false;
  options: string[] = ['Edit', 'Delete'];
  dropdownStates: boolean[] = [];
  updateIndex!: number;

  constructor(private classroomService: ClassroomService, private router: Router) {  }

  ngOnInit(): void {
    this.getClassesOwned();
  }

  getClassesOwned() {
    this.classroomService.getClassesOwned().subscribe({
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

  toggleCreateClass() {
    
    if (this.editClass == true) {
      this.editClass = false;
    } else {
      this.openCreateClass = !this.openCreateClass;
    }
    
  }

  createClass() {
    let classroom: NewClassroom = {name: this.className}
    this.classroomService.createClass(classroom).subscribe({
      next: data => {
        console.log(data);
        this.toggleCreateClass();
        this.className = '';
        this.getClassesOwned();
      },
      error: error => {
        console.log(error);
      }
    });
  }

  toggleEditDropdown(index: number) {
    this.dropdownStates[index] = !this.dropdownStates[index];
  }

  updateClass() {
    const updateClass = this.classes[this.updateIndex];
    updateClass.name = this.className;
    this.toggleCreateClass()
    this.className='';
    this.classroomService.updateClass(updateClass).subscribe({
      next: data => {
        this.getClassesOwned();
        this.editClass = false;
      },
      error: error => {
        console.log(error);
      }
    });
  }

  deleteClass(id: number) {
    this.classroomService.deleteClass(id).subscribe({
      next: data => {
        this.getClassesOwned();
      },
      error: error => {
        console.log(error);
      }
    });

  }

  onOptionSelect(option: string, index: number) {
    if (option === this.options[0]) {
      this.updateIndex = index;
      this.editClass = true;
    } else if (option === this.options[1]) {
      this.deleteClass(this.classes[index].id);
    }
  }

  navClass(classroom: Classroom) {
    this.router.navigate(['class', classroom.id]);
  }

}
