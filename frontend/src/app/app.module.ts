import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { CreateQuizComponent } from './create-quiz/create-quiz.component';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AuthInterceptor } from './_guards/auth.interceptor';
import { QuestionBankComponent } from './question-bank/question-bank.component';
import { CustomDatePipe } from './service/custom-date.pipe';
import { QuizDesignComponent } from './quiz-design/quiz-design.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { SidebarComponent } from './sidebar/sidebar.component';
import { NavbarComponent } from './navbar/navbar.component';
import { LibraryComponent } from './library/library.component';
import { JoinGameComponent } from './join-game/join-game.component';
import { HostQuizComponent } from './host-lobby/host-quiz.component';
import { HostGameComponent } from './host-game/host-game.component';
import { PlayerGameComponent } from './player-game/player-game.component';
import { ClassroomComponent } from './classroom/classroom.component';
import { OpenClassroomComponent } from './open-classroom/open-classroom.component';
import { CreateAssignmentComponent } from './create-assignment/create-assignment.component';
import { StudentClassroomComponent } from './student-classroom/student-classroom.component';
import { StudentOpenClassComponent } from './student-open-class/student-open-class.component';
import { StudentQuizComponent } from './student-quiz/student-quiz.component';
import { StudentDashboardComponent } from './student-dashboard/student-dashboard.component';
import { TeacherDashboardComponent } from './teacher-dashboard/teacher-dashboard.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { AssignmentInfoComponent } from './assignment-info/assignment-info.component';
import { AssignmentReportStudentsComponent } from './assignment-report-students/assignment-report-students.component';
import { AssignmentReportQuestionsComponent } from './assignment-report-questions/assignment-report-questions.component';
import { ReportBannerComponent } from './report-banner/report-banner.component';
import { StudentReportComponent } from './student-report/student-report.component';
import { ManageClassComponent } from './manage-class/manage-class.component';



@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    CreateQuizComponent,
    LoginComponent,
    RegisterComponent,
    QuestionBankComponent,
    CustomDatePipe,
    QuizDesignComponent,
    SidebarComponent,
    NavbarComponent,
    LibraryComponent,
    JoinGameComponent,
    HostQuizComponent,
    HostGameComponent,
    PlayerGameComponent,
    ClassroomComponent,
    OpenClassroomComponent,
    CreateAssignmentComponent,
    StudentClassroomComponent,
    StudentOpenClassComponent,
    StudentQuizComponent,
    StudentDashboardComponent,
    TeacherDashboardComponent,
    PageNotFoundComponent,
    AssignmentInfoComponent,
    AssignmentReportStudentsComponent,
    AssignmentReportQuestionsComponent,
    ReportBannerComponent,
    StudentReportComponent,
    ManageClassComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    DragDropModule,
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
