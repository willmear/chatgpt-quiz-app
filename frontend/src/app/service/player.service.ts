import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { NextQuestion, ParticipantMessage } from '../model/participant-message.model';
import { Observable, Subject } from 'rxjs';
import { Question } from '../model/question.model';
import { State } from '../model/quiz-session.model';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {

  private stompClient!: Stomp.Client;
  private participantSubject: Subject<ParticipantMessage> = new Subject<ParticipantMessage>();
  private questionSubject: Subject<Question> = new Subject<Question>();
  private stateSubject: Subject<State> = new Subject<State>();




  private url = 'http://localhost:8082/join';

  constructor(private http: HttpClient) {
     
  }

  initializeWebSocketConnectionAnswers() {
    const serverUrl = `${this.url}/ws`;
    const ws = new SockJS(serverUrl);
    this.stompClient = Stomp.over(ws);
    this.stompClient.connect({}, () => {
      console.log('Connected to WebSocket');
      this.subscribeToAnswers();
    });
  }

  initializeWebSocketConnectionQuestions() {
    const serverUrl = `${this.url}/ws`;
    const ws = new SockJS(serverUrl);
    this.stompClient = Stomp.over(ws);
    this.stompClient.connect({}, () => {
      console.log('Connected to WebSocket');
      this.subscribeToQuestions();
      this.subscribeToState();
    });
  }

  submitAnswer(participantMessage: ParticipantMessage): void {
    this.stompClient.send(`/app/quiz.sendAnswer`, {}, JSON.stringify(participantMessage));
  }

  addParticipant(participantMessage: ParticipantMessage): void {
    this.stompClient.send(`/app/quiz.addParticipant`, {}, JSON.stringify(participantMessage));
  }

  nextQuestion(nextQuestion: NextQuestion): void {
    this.stompClient.send(`/app/quiz.nextQuestion`, {}, JSON.stringify(nextQuestion));
  }

  nextState(nextQuestion: NextQuestion): void {
    this.stompClient.send(`/app/quiz.updateState`, {}, JSON.stringify(nextQuestion));
  }

  subscribeToAnswers(): void {
      this.stompClient.subscribe('/topic/public', (payload) => {
        this.setParticipants(JSON.parse(payload.body));
      });
  }

  subscribeToQuestions(): void {
      this.stompClient.subscribe('/topic/question', (payload) => {
        this.setQuestion(JSON.parse(payload.body));
      });
  }

  subscribeToState(): void {
    this.stompClient.subscribe('/topic/state', (payload) => {
      this.setState(JSON.parse(payload.body));
    });
  }

  setParticipants(participantMessage: ParticipantMessage) {
    this.participantSubject.next(participantMessage);
  }

  setQuestion(question: Question) {
    this.questionSubject.next(question);
  }

  setState(state: State) {
    this.stateSubject.next(state);
  }

  updateParticipants(): Observable<ParticipantMessage> {
    return this.participantSubject.asObservable();
  }

  updateQuestion(): Observable<Question> {
    return this.questionSubject.asObservable();
  }

  updateState(): Observable<State> {
    return this.stateSubject.asObservable();
  }

  setPlayer(alias: string): void {
    localStorage.setItem('alias', alias);
  }

  getPlayer(): string {
    return localStorage.getItem('alias') ?? '';
  }

  setSessionId(quizSessionId: number) {
    localStorage.setItem('sessionId', String(quizSessionId));
  }

  getSessionId(): number {
    return Number(localStorage.getItem('sessionId'));
  }


}
