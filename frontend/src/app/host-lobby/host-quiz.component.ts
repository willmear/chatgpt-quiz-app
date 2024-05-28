import { Component, OnInit } from '@angular/core';
import { PlayQuizService } from '../service/play-quiz.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CreateQuizService } from '../service/create-quiz.service';
import { State } from '../model/quiz-session.model';
import { Subscription } from 'rxjs';
import { PlayerService } from '../service/player.service';
import { MessageType, ParticipantMessage } from '../model/participant-message.model';

@Component({
  selector: 'app-host-quiz',
  templateUrl: './host-quiz.component.html',
  styleUrl: './host-quiz.component.css'
})
export class HostQuizComponent implements OnInit {

  quizId: string = '';
  quizSessionId: string = '';
  quizSession: any;
  participants: string[] = [];
  private refreshSubscription: Subscription | undefined;
  private participantSubscription!: Subscription;

  constructor(private playService: PlayQuizService, private route: ActivatedRoute,
    private createQuizService: CreateQuizService, private router: Router, private playerService: PlayerService) {
      this.quizId = this.route.snapshot.params['quizId'];
      this.createQuiz(this.quizId);
      playerService.initializeWebSocketConnectionAnswers();
  }

  ngOnInit(): void {
    this.participantSubscription = this.playerService.updateParticipants().subscribe((newMessage: ParticipantMessage) => {
      console.log(newMessage);
      switch(newMessage.type) {

        case MessageType.JOIN:
          this.participants.push(newMessage.participant);
          break;

        case MessageType.LEAVE:
          this.participants = this.participants.filter(item => item !== newMessage.participant);
          break;

        default:
          console.log("default");
          
      }
    })
  }

  ngOnDestroy(): void {
    // Unsubscribe from all subscriptions when the component is destroyed
    this.unsubscribeRefreshSubscription();
  }

  getQuiz(quizSessionId: string) {

    this.playService.getQuizSession(Number(quizSessionId)).subscribe({
      next: data => {
        this.quizSession = data.body;
      },
      error: error => {
        console.log(error);
      }
    });

  }

  createQuiz(quizId: string) {
    
    this.createQuizService.createQuizSession(Number(quizId)).subscribe({
      next: data => {
        this.quizSessionId = data.id
        console.log(this.quizSessionId)
        this.quizSession = data;
        this.getQuiz(this.quizSessionId);
        // this.getParticipants(this.quizSessionId);
      },
      error: error => {
        console.log(error);
      }
    })

  }

  private unsubscribeRefreshSubscription(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  start(): void {
    let updatedSession = this.quizSession;
    updatedSession.state = State.PRE_QUESTION;
    if (this.participants.length > 0) {
      this.playService.updateState(updatedSession, Number(this.quizSessionId)).subscribe({
        next: data => {
          console.log(data);
          this.participantUnsubscribe();
          this.router.navigate(['host-game'], { queryParams: {id: this.quizSessionId} });
        },
        error: error => {
          console.log(error);
        }
      })
      
    }
  }

  private participantUnsubscribe():void {
    if (this.participantSubscription) {
      this.participantSubscription.unsubscribe();
    }
  }
  

}
