import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Question } from '../model/question.model';
import { Quiz } from '../model/quiz.model';
import { Folder } from '../model/folder.model';

export type EntityArrayResponseType = HttpResponse<Quiz[]>;


@Injectable({
  providedIn: 'root'
})
export class CreateQuizService {

  private url = 'http://localhost:8082/core/api/v1/core';

  constructor(private http: HttpClient) { }

  createQuiz(questions: Question[]): Observable<HttpResponse<any>> {
    return this.http.post<Question>(`${this.url}/create`, questions, {observe: 'response'});
  }

  getQuiz(quizId: string): Observable<HttpResponse<Quiz>> {
    return this.http.get<Quiz>(`${this.url}/quiz/${quizId}`, {observe: 'response'});
  }

  getQuizStudent(quizId: string): Observable<HttpResponse<Quiz>> {
    return this.http.get<Quiz>(`${this.url}/student/quiz/${quizId}`, {observe: 'response'});
  }

  publishQuiz(quiz: Quiz, quizId: string): Observable<HttpResponse<any>> {
    return this.http.put<Quiz>(`${this.url}/quiz/${quizId}/publish`, quiz, { observe: 'response' });
  }

  getAllQuiz(): Observable<EntityArrayResponseType> {
    return this.http.get<Quiz[]>(`${this.url}/quiz/library`, { observe: 'response' });
  }

  getAllDraft(): Observable<EntityArrayResponseType> {
    return this.http.get<Quiz[]>(`${this.url}/quiz/library/draft`, { observe: 'response' });
  }

  createFolder(folder: Folder): Observable<HttpResponse<any>> {
    return this.http.post<Folder>(`${this.url}/quiz/library/folder`, folder, { observe: 'response' });
  }

  createQuizSession(quizId: number): Observable<any> {
    return this.http.post<any>(`${this.url}/create/${quizId}`, { observe: 'response' });
  }

  deleteQuiz(quizId: number): Observable<any> {
    return this.http.delete<number>(`${this.url}/quiz/delete/${quizId}`, { observe: 'response' });
  }

  updateLastPlayed(quizId: number): Observable<HttpResponse<Quiz>> {
    return this.http.put<Quiz>(`${this.url}/last-played`, quizId, { observe: 'response' });
  }

  
}
