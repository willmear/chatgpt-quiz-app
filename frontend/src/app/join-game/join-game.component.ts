import { Component, OnInit } from '@angular/core';
import { PlayQuizService } from '../service/play-quiz.service';
import { Router } from '@angular/router';
import { PlayerService } from '../service/player.service';
import { MessageType, ParticipantMessage } from '../model/participant-message.model';

@Component({
  selector: 'app-join-game',
  templateUrl: './join-game.component.html',
  styleUrl: './join-game.component.css'
})
export class JoinGameComponent implements OnInit {

  errorMessage: string = '';
  correctJoinCode: boolean = false;
  joinCode: string = '';

  constructor(private playService: PlayQuizService, private router: Router, private playerService: PlayerService) {
    playerService.initializeWebSocketConnectionQuestions();
  }

  ngOnInit(): void {
  }

  join(form: any): void {
    this.playService.checkJoinCode(form.code).subscribe({
      next: data => {
        console.log(data);
        if (data.status === 200) {
          this.correctJoinCode=true;
          this.joinCode=form.code;
          this.playerService.setSessionId(data.body.id);
        }
      },
      error: error => {
        console.log(error);
        if (error.status === 400) {
          this.errorMessage = 'Incorrect join code';
        }
         
      }
    });
  }

  enterName(form: any):void {
    let alias = form.alias;
    let sessionId = this.playerService.getSessionId();
    const participant: ParticipantMessage = {answer: undefined, participant: alias, quizSessionId: sessionId, type: MessageType.JOIN};
    this.playerService.addParticipant(participant);
    this.playerService.setPlayer(alias);
    this.router.navigate(['game'])
  }

}
