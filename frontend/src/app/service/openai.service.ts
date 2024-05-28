import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ChatRequest } from '../model/chat-request.model';
import { Observable } from 'rxjs';
import { ChatResponse } from '../model/chat-response.model';
import { Difficulty, Question } from '../model/question.model';
import { AssignmentCompletion } from '../model/class.model';

export type EntityArrayResponseType = HttpResponse<Question[]>;

@Injectable({
  providedIn: 'root'
})
export class OpenaiService {

  private url = 'http://localhost:8082/question/api/v1/embedding';

  constructor(private http: HttpClient) { }

  createQuestions(chat: ChatRequest): Observable<HttpResponse<any>> {
    return this.http.post<ChatRequest>(`${this.url}/create`, chat, {observe: 'response'});
  }

  saveQuestions(questions: ChatResponse[]): Observable<HttpResponse<any>> {
    return this.http.post<ChatResponse[]>(`${this.url}/save`, questions, { observe: 'response' });
  }

  getAllQuestions(): Observable<EntityArrayResponseType> {
    return this.http.get<Question[]>(`${this.url}/questions`, { observe: 'response' });
  }

  updateQuestionDifficulty(completion: Difficulty): Observable<HttpResponse<Difficulty>> {
    return this.http.post<Difficulty>(`${this.url}/questions/difficulty`, completion, { observe: 'response' });
  }
}
