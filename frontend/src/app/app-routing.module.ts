import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { CreateQuizComponent } from './create-quiz/create-quiz.component';
import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { QuestionBankComponent } from './question-bank/question-bank.component';
import { QuizDesignComponent } from './quiz-design/quiz-design.component';
import { LibraryComponent } from './library/library.component';
import { JoinGameComponent } from './join-game/join-game.component';
import { HostQuizComponent } from './host-lobby/host-quiz.component';
import { PlayerGameComponent } from './player-game/player-game.component';
import { HostGameComponent } from './host-game/host-game.component';
import { ClassroomComponent } from './classroom/classroom.component';
import { OpenClassroomComponent } from './open-classroom/open-classroom.component';
import { CreateAssignmentComponent } from './create-assignment/create-assignment.component';
import { StudentClassroomComponent } from './student-classroom/student-classroom.component';
import { StudentOpenClassComponent } from './student-open-class/student-open-class.component';
import { StudentQuizComponent } from './student-quiz/student-quiz.component';
import { RoleActivateGuard } from './_guards/role-activate.guard';
import { StudentDashboardComponent } from './student-dashboard/student-dashboard.component';
import { TeacherDashboardComponent } from './teacher-dashboard/teacher-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { AssignmentInfoComponent } from './assignment-info/assignment-info.component';
import { AssignmentReportStudentsComponent } from './assignment-report-students/assignment-report-students.component';
import { AssignmentReportQuestionsComponent } from './assignment-report-questions/assignment-report-questions.component';
import { ExitAssignmentGuard } from './_guards/can-deactivate.guard';
import { StudentReportComponent } from './student-report/student-report.component';
import { ManageClassComponent } from './manage-class/manage-class.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'create', component: CreateQuizComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'question-bank', component: QuestionBankComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'design/:id', component: QuizDesignComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'library', component: LibraryComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'join', component: JoinGameComponent },
  { path: 'host/:quizId', component: HostQuizComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'game', component: PlayerGameComponent },
  { path: 'host-game', component: HostGameComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'class', component: ClassroomComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'class/:id', component: OpenClassroomComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'homework', component: CreateAssignmentComponent, canActivate:[RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'student-class', component: StudentClassroomComponent, canActivate:[RoleActivateGuard], data: {  role: 'STUDENT'  } },
  { path: 'student-class/:id', component: StudentOpenClassComponent, canActivate:[RoleActivateGuard], data: {  role: 'STUDENT'  } },
  { path: 'assignment/:classId/:assignmentId', component: StudentQuizComponent, canActivate: [RoleActivateGuard], canDeactivate: [ExitAssignmentGuard], data: {  role: 'STUDENT'  } },
  { path: 'student-dashboard', component: StudentDashboardComponent, canActivate: [RoleActivateGuard], data: {  role: 'STUDENT'  } },
  { path: 'teacher-dashboard', component: TeacherDashboardComponent, canActivate: [RoleActivateGuard], data: {  role: 'TEACHER'  } },
  { path: 'info/:assignmentId', component: AssignmentInfoComponent, canActivate: [RoleActivateGuard], data: { role: 'TEACHER' } },
  { path: 'info/:assignmentId/students', component: AssignmentReportStudentsComponent, canActivate: [RoleActivateGuard], data: { role: 'TEACHER' } },
  { path: 'info/:assignmentId/questions', component: AssignmentReportQuestionsComponent, canActivate: [RoleActivateGuard], data: { role: 'TEACHER' } },
  { path: 'student-report', component: StudentReportComponent, canActivate: [RoleActivateGuard], data: {  role: 'STUDENT'  } },
  { path: 'class/:id/manage', component: ManageClassComponent, canActivate: [RoleActivateGuard], data: { role: 'TEACHER' } },
  { path: '**', pathMatch: 'full', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
