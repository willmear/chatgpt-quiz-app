import { Component, OnInit } from '@angular/core';
import { State } from '../model/quiz-session.model';
import { PlayerService } from '../service/player.service';
import { Subscription } from 'rxjs';
import { Question } from '../model/question.model';
import { MessageType } from '../model/participant-message.model';
import { PlayQuizService } from '../service/play-quiz.service';

@Component({
  selector: 'app-player-game',
  templateUrl: './player-game.component.html',
  styleUrl: './player-game.component.css'
})
export class PlayerGameComponent implements OnInit {

  state = State;
  alias: string;
  sessionId: number;
  private questionSubscription!: Subscription;
  private stateSubscription!: Subscription;
  currentQuestion!: Question;
  questionAnswered: boolean = false;
  currentState: State = State.WAITING_FOR_PLAYERS;

  constructor(private playerService: PlayerService) {
    this.alias = this.playerService.getPlayer();
    this.sessionId = this.playerService.getSessionId();
    this.playerService.initializeWebSocketConnectionQuestions();
  }


  // TODO: Subscribe to state to know when to display questions
  ngOnInit(): void {

    this.questionSubscription = this.playerService.updateQuestion().subscribe((newQuestion: Question) => {
      console.log("got question");
      this.currentQuestion = newQuestion;
      console.log(this.currentQuestion);
    });

    this.stateSubscription = this.playerService.updateState().subscribe((newState: State) => {
      this.currentState=newState;
      if (newState == State.PRE_QUESTION) {
        this.questionAnswered = false;
      }
    });

  }

  ngOnDestroy() {
    this.questionSubscription.unsubscribe();
  }

  submitAnswer(index: number):void {
    const alias = this.playerService.getPlayer();
    this.playerService.submitAnswer({answer: index, participant: alias, quizSessionId: this.sessionId, type: MessageType.ANSWER});
    this.questionAnswered = true;
  }

}
