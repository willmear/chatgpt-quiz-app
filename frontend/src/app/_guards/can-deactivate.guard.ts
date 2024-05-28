import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { StudentQuizComponent } from '../student-quiz/student-quiz.component';
import { Classroom } from '../model/class.model';
import { ClassroomService } from '../service/classroom.service';

type CanDeactivateType = Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree;

export interface CanComponentDeactivate {
  canDeactivate: () => CanDeactivateType;
}

@Injectable({
  providedIn: 'root'
})
export class ExitAssignmentGuard {

    constructor(private classroomService: ClassroomService) {}

    canDeactivate(
      component: StudentQuizComponent
    ): Observable<boolean> | boolean {
      if (!component.submitted) {
        const confirmExit = confirm('Are you sure you want to exit the assignment? Your progress will be submitted incomplete.');

        if (confirmExit) {
          component.quit = true;
          component.endAssignment();
        }
          
          return confirmExit;
      }
      return true;
    }
  }