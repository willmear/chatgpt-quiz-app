import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { CreateQuizComponent } from './create-quiz/create-quiz.component';
import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { QuestionBankComponent } from './question-bank/question-bank.component';
import { QuizDesignComponent } from './quiz-design/quiz-design.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LibraryComponent } from './library/library.component';
import { JoinGameComponent } from './join-game/join-game.component';
import { LoginActivateGuard } from './_auth/login-activate.guard';
import { HostQuizComponent } from './host-lobby/host-quiz.component';
import { PlayerGameComponent } from './player-game/player-game.component';
import { HostGameComponent } from './host-game/host-game.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'create', component: CreateQuizComponent, canActivate:[LoginActivateGuard] },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'question-bank', component: QuestionBankComponent, canActivate:[LoginActivateGuard] },
  { path: 'design/:id', component: QuizDesignComponent, canActivate:[LoginActivateGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate:[LoginActivateGuard] },
  { path: 'library', component: LibraryComponent, canActivate:[LoginActivateGuard] },
  { path: 'join', component: JoinGameComponent },
  { path: 'host/:quizId', component: HostQuizComponent, canActivate:[LoginActivateGuard] },
  { path: 'game', component: PlayerGameComponent },
  { path: 'host-game', component: HostGameComponent, canActivate:[LoginActivateGuard] }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
