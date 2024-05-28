import { Component, OnInit } from '@angular/core';
import { State } from '../model/quiz-session.model';
import { PlayerService } from '../service/player.service';
import { Subscription } from 'rxjs';
import { Blank, DragAndDrop, Question } from '../model/question.model';
import { Answer, MessageType } from '../model/participant-message.model';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';


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
  orderedChoices: String[] = [];
  dragAndDropAnswer: string[] = [];
  dragAndDropChoices: string[] = [];
  questionParts!: DragAndDrop;
  questionAnswered: boolean = false;
  questionPartsFtb: Blank[] = [];
  ftbAnswer: string = '';
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

      if (this.currentQuestion.questionType === 'Ordering') {
        this.orderedChoices = [...newQuestion.choices];
      } else if (this.currentQuestion.questionType === 'Drag and Drop') {
        this.questionParts = this.createQuestionParts(String(newQuestion.question));
        this.dragAndDropChoices = [...this.currentQuestion.choices.map((strObject) => String(strObject))];
      } else if (this.currentQuestion.questionType === 'Fill The Blanks') {
        this.ftbAnswer = '';
        this.questionPartsFtb = this.createFTB(String(this.currentQuestion.question));
      }
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
    this.stateSubscription.unsubscribe();
  }

  submitAnswer(index: number):void {
    const answer: Answer = {questionType: this.currentQuestion.questionType, answer: [index]};
    const alias = this.playerService.getPlayer();
    this.playerService.submitAnswer({answer, participant: alias, quizSessionId: this.sessionId, type: MessageType.ANSWER});
    this.questionAnswered = true;
  }

  submitOrdering(): void {
    let indexes: number[] = []
    this.currentQuestion.choices.forEach((choice) => {
      const index = this.orderedChoices.indexOf(choice);
      indexes.push(index);
    });
    const answer: Answer = {questionType: this.currentQuestion.questionType, answer: indexes};
    const alias = this.playerService.getPlayer();
    this.playerService.submitAnswer({answer, participant: alias, quizSessionId: this.sessionId, type: MessageType.ANSWER});
    this.questionAnswered = true;
  }

  submitDragAndDrop() {
        
    let indexes: number[] = [];
    this.dragAndDropAnswer.forEach((answer: string) => {
      console.log(answer);
      console.log(this.currentQuestion.choices);
      const index = this.currentQuestion.choices.indexOf(answer);
      indexes.push(index);
    });

    const answer: Answer = {questionType: 'Drag and Drop', answer: indexes}

    this.dragAndDropAnswer = [];

    const alias = this.playerService.getPlayer();
    this.playerService.submitAnswer({answer, participant: alias, quizSessionId: this.sessionId, type: MessageType.ANSWER});
    this.questionAnswered = true;
  }

  dropOrdering(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.orderedChoices, event.previousIndex, event.currentIndex);
    console.log(this.orderedChoices);
  }
  
  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );        
    }
  }

  createQuestionParts(text: string): DragAndDrop {
    const parts = text.split(/(_)/);
    const blankRegex = /_/g;
  
    const blanks = text.match(blankRegex)?.map(() => ({ value: null })) || [];

    const formattedText = text.replace(blankRegex, '_');

    return {
      text: formattedText,
      blanks: blanks
    };
  }

  createFTB(text: string): Blank[] {
    const parts = text.split(/(_)/);
    const blankRegex = /_/g;

    let blanks: Blank[] = [];

    // Loop through each part of the text
    parts.forEach(part => {
      if (part === "_") {
        // If the part is a blank, push a blank object with null value
        blanks.push({ value: null });
      } else {
        // If the part is not a blank, push a blank object with the text value
        blanks.push({ value: part });
      }
    });

    return blanks;
  }

  submitFTB() {

    const answer: Answer = {questionType: 'Fill The Blanks', answer: this.checkAnswer(this.ftbAnswer, this.currentQuestion.choices.map(obj => String(obj)), 0.7)};

    const alias = this.playerService.getPlayer();
    this.playerService.submitAnswer({answer, participant: alias, quizSessionId: this.sessionId, type: MessageType.ANSWER});
    this.questionAnswered = true;

  }

  checkAnswer(studentAnswer: string, modelAnswers: string[], threshold: number): number[] {
    let answer: number[] = [];
    const levenshteinDistance = (s1: string, s2: string): number => {
      const track = Array(s2.length + 1).fill(null).map(() => Array(s1.length + 1).fill(null));
      for (let i = 0; i <= s1.length; i++) {
        track[0][i] = i;
      }
      for (let j = 0; j <= s2.length; j++) {
        track[j][0] = j;
      }
      for (let j = 1; j <= s2.length; j++) {
        for (let i = 1; i <= s1.length; i++) {
          const cost = s1[i - 1] === s2[j - 1] ? 0 : 1;
          track[j][i] = Math.min(
            track[j][i - 1] + 1,
            track[j - 1][i] + 1,
            track[j - 1][i - 1] + cost,
          );
        }
      }
      return track[s2.length][s1.length];
    };

    for (const modelAnswer of modelAnswers) {
      const sanitizedStudentAnswer = studentAnswer.toLowerCase();
      const sanitizedModelAnswer = modelAnswer.toLowerCase();
      if (sanitizedStudentAnswer === '' || sanitizedModelAnswer === '') {
        continue;
      }
      
      const maxStringLength = Math.max(sanitizedStudentAnswer.length, sanitizedModelAnswer.length);
      if (maxStringLength === 0) {
        continue;
      }
      
      const similarityScore = 1.0 - levenshteinDistance(sanitizedStudentAnswer, sanitizedModelAnswer) / maxStringLength;
      console.log(similarityScore);
      if (similarityScore >= threshold) {
        answer = [this.currentQuestion.choices.indexOf(modelAnswer)];
        return answer;
      }
    }
    answer = [998];
    return answer;
  }

}
