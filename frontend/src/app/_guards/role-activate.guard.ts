import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { UserService } from '../service/user.servce';

@Injectable({
  providedIn: 'root'
})
export class RoleActivateGuard {

  constructor(protected userService: UserService, protected router: Router) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    let url: string = state.url;
    return this.checkUserRole(route, url);
  }

  checkUserRole(route: ActivatedRouteSnapshot, url: any): boolean {
    if (this.userService.isLoggedIn()) {
      const userRole = this.userService.getRole();
      if (route.data['role'] && route.data['role'].indexOf(userRole) === -1) {
        if (userRole === 'TEACHER') {
          this.router.navigate(['/teacher-dashboard']);
        } else if (userRole === 'STUDENT') {
          this.router.navigate(['/student-dashboard']);
        }
        return false;
      }
      return true;
    } else {
      this.router.navigate(['/login'], { queryParams: { returnUrl: url }});
      return false;
    }
  }

}