import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ChatRequest } from '../model/chat-request.model';
import { Observable } from 'rxjs';
import { ChatResponse } from '../model/chat-response.model';
import { Question } from '../model/question.model';

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
}
