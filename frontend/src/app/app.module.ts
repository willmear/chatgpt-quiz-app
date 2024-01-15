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
import { AuthInterceptor } from './_auth/auth.interceptor';
import { QuestionBankComponent } from './question-bank/question-bank.component';
import { CustomDatePipe } from './service/custom-date.pipe';
import { QuizDesignComponent } from './quiz-design/quiz-design.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { SidebarComponent } from './sidebar/sidebar.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NavbarComponent } from './navbar/navbar.component';
import { LibraryComponent } from './library/library.component';
import { JoinGameComponent } from './join-game/join-game.component';
import { HostQuizComponent } from './host-lobby/host-quiz.component';
import { HostGameComponent } from './host-game/host-game.component';
import { PlayerGameComponent } from './player-game/player-game.component';



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
    DashboardComponent,
    NavbarComponent,
    LibraryComponent,
    JoinGameComponent,
    HostQuizComponent,
    HostGameComponent,
    PlayerGameComponent,
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
