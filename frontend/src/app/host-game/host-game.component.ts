import { Component, OnInit } from '@angular/core';
import { Participant, QuizSession, SessionQuestion, State } from '../model/quiz-session.model';
import { PlayQuizService } from '../service/play-quiz.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, interval, take } from 'rxjs';
import { PlayerService } from '../service/player.service';


@Component({
  selector: 'app-host-game',
  templateUrl: './host-game.component.html',
  styleUrl: './host-game.component.css'
})
export class HostGameComponent implements OnInit {

  state = State;

  quizSessionId!: number;
  quizSession!: QuizSession;
  currentQuestion!: SessionQuestion;
  private gameStateSubscription!: Subscription;
  private countdownSubscription!: Subscription;

  countdown = 0;
  participants: Participant[] = [];

  constructor(private playService: PlayQuizService, private route: ActivatedRoute, private router: Router, private playerService: PlayerService) {
    this.quizSessionId = Number(this.route.snapshot.queryParamMap.get('id')) ?? '';
    this.getQuiz(this.quizSessionId);
    playerService.initializeWebSocketConnectionAnswers();
  }

  ngOnInit(): void {
    this.gameStateSubscription = this.playService.watchGameState().subscribe((newState: State) => {
      console.log("game state changed: ", newState);

      switch (newState) {
        
        case State.PRE_QUESTION:
          this.currentQuestion = this.playService.getCurrentQuestion();
          this.countdown = 5;
          this.countdownSubscription = interval(1000)
          .pipe(take(this.countdown))
          .subscribe({
            next: () => {
              this.countdown--;
            },
            complete: () => {
              this.countdownUnsubscribe();
              this.updateState(State.QUESTION);
            }
          });
        break;

        case State.QUESTION:
          this.playerService.nextQuestion({state: State.QUESTION, sessionId: this.quizSessionId});
          this.countdown = this.currentQuestion.timeSeconds;
          interval(1000)
          .pipe(take(this.countdown))
          .subscribe({
            next: () => {
              this.countdown--;
            },
            complete: () => {
              this.countdownUnsubscribe();
              this.updateState(State.POST_QUESTION);
            }
          });
          break;

        case State.POST_QUESTION:
          // get participants
          this.playService.participants(this.quizSession.id).subscribe({
            next: data => {
              this.participants = data.body ?? [];
            },
            error: error => {
              console.log(error);
            }
          });

          this.countdown = 3;
          interval(1000)
          .pipe(take(this.countdown))
          .subscribe({
            next: () => {
              this.countdown--;
            },
            complete: () => {
              this.countdownUnsubscribe();
              this.updateState(State.LEADERBOARD);
            }
          });
          break;

        case State.LEADERBOARD:
          // call to update state in html on button click
          break;
        case State.FINISHED:
          this.watchStateUnsubscribe();
          console.log("finished from switch");
          break;

        default:
          console.log("Unexpected state:", newState);

      }
    })
  }

  ngOnDestroy() {
    this.gameStateSubscription.unsubscribe();
  }
  

  getQuiz(quizSessionId: number) {

    this.playService.getQuizSession(quizSessionId).subscribe({
      next: data => {
        this.quizSession = data.body;
        this.playService.setCurrentQuestion(this.quizSession.quiz.questions[this.quizSession.quiz.currentQuestion]);
        this.currentQuestion = this.playService.getCurrentQuestion();
        this.playService.setGameState(this.quizSession.state);
      },
      error: error => {
        console.log(error);
      }
    });

  }

  
  updateState(state: State):void {

    // if on leaderboard and not on final question
    if (state === State.PRE_QUESTION && (this.quizSession.quiz.currentQuestion < this.quizSession.quiz.questions.length-1)) {
      this.quizSession.state = state;
      this.quizSession.quiz.currentQuestion++;

      this.playerService.nextState({state: state, sessionId: this.quizSessionId});
      this.playService.updateState(this.quizSession, this.quizSessionId).subscribe({
        next: data => {
          console.log("game session updated");
          // update to next question
          this.playService.setCurrentQuestion(this.quizSession.quiz.questions[this.quizSession.quiz.currentQuestion]);
          this.quizSession=data.body;
          console.log("currently stored current question: ", this.playService.getCurrentQuestion());
          this.playService.setGameState(this.quizSession.state);
        },
        error: error => {
          console.log(error);
        }
      });

    } 
    // if on leaderboard and on final question
    else if (state === State.PRE_QUESTION && (this.quizSession.quiz.currentQuestion >= this.quizSession.quiz.questions.length-1)) {
      state = State.FINISHED;
      this.quizSession.state = state;

      this.playerService.nextState({state: state, sessionId: this.quizSessionId});
      this.playService.updateState(this.quizSession, this.quizSessionId).subscribe({
        next: data => {
          console.log(data);
          this.quizSession=data.body;
          this.playService.setGameState(state);
        },
        error: error => {
          console.log(error);
        }
      });

    }
    // if not on leaderboard
    else {
      this.quizSession.state = state;

      this.playerService.nextState({state: state, sessionId: this.quizSessionId});
      this.playService.updateState(this.quizSession, this.quizSessionId).subscribe({
        next: data => {
          this.quizSession=data.body;
          this.playService.setGameState(state);
        },
        error: error => {
          console.log(error);
        }
      });
    }
  }

  private countdownUnsubscribe():void {
    if (this.countdownSubscription) {
      this.countdownSubscription.unsubscribe();
    }
  }

  private watchStateUnsubscribe():void {
    if (this.gameStateSubscription) {
      this.gameStateSubscription.unsubscribe();
    }
  }

}
