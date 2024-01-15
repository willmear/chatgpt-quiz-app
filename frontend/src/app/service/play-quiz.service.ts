import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Quiz } from '../model/quiz.model';
import { Participant, QuizSession, SessionQuestion, State } from '../model/quiz-session.model';
import { Question } from '../model/question.model';

export type EntityArrayResponseType = HttpResponse<Quiz[]>;
export type EntityResponseType = HttpResponse<QuizSession>;



@Injectable({
  providedIn: 'root'
})
export class PlayQuizService {

  private gameStateSubject: Subject<State> = new Subject<State>();
  private currentQuestion!: SessionQuestion;

  private url = 'http://localhost:8082/play/api/v1/quiz';

  constructor(private http: HttpClient) { }

  getQuizSession(quizSessionId: number): Observable<HttpResponse<any>> {
    return this.http.get<QuizSession>(`${this.url}/session/${quizSessionId}`, {observe: 'response'});
  }

  checkJoinCode(joinCode: string): Observable<HttpResponse<any>> {
    return this.http.post<string>(`http://localhost:8082/join/verify`, joinCode, { observe: 'response' });
  }

  join(joinRequest: {alias: string, joinCode: string}): Observable<HttpResponse<any>> {
    return this.http.post<{alias: string, joinCode: string}>(`http://localhost:8082/join`, joinRequest, {observe: 'response'});
  }
  
  participants(quizSessionId: number): Observable<HttpResponse<Participant[]>> {
    return this.http.get<Participant[]>(`${this.url}/participants/${quizSessionId}`, {observe: 'response'});
  }

  updateState(quizSession: QuizSession, sessionId: number): Observable<HttpResponse<any>> {
    return this.http.put<QuizSession>(`${this.url}/state/${sessionId}`, quizSession, {observe: 'response'});
  }

  setGameState(state: State): void {
    this.gameStateSubject.next(state);
  }

  watchGameState(): Observable<State> {
    return this.gameStateSubject.asObservable();
  }

  getCurrentQuestion(): SessionQuestion {
    return this.currentQuestion;
  }

  setCurrentQuestion(question: SessionQuestion): void {
    this.currentQuestion = question;
  }


}
