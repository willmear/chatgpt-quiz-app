import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClassroomService } from '../service/classroom.service';
import { CreateQuizService } from '../service/create-quiz.service';
import { Subscription, interval, take } from 'rxjs';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { AssignmentAnswer, AssignmentCompletion, CompletionRequest } from '../model/class.model';
import { Blank, Difficulty, DragAndDrop } from '../model/question.model';
import { QuizQuestions } from '../model/quiz.model';
import { OpenaiService } from '../service/openai.service';

@Component({
  selector: 'app-student-quiz',
  templateUrl: './student-quiz.component.html',
  styleUrl: './student-quiz.component.css'
})
export class StudentQuizComponent implements OnInit {

  assignmentId: number;
  classId: number;
  score: number = 0;
  assignment: any;
  submitted: boolean = false;
  submissionConfirmation: string = '';
  currentQuestion: number = 0;
  displayAnswer: boolean = false;
  currentAnswers: String[] = [];
  newAnswers: AssignmentAnswer[] = [];
  dragAndDropAnswer: string[] = [];
  orderedChoices: String[] = [];
  dragAndDropChoices: string[] = [];
  questionDaD: string = '';
  questionParts!: DragAndDrop;
  dragAndDropQuestion: string[] = [];
  questionPartsFtb: Blank[] = [];
  ftbAnswer: string = '';
  countdown: number = 0;
  threshold: number = 0.7;
  result: boolean | undefined = undefined;
  quit: boolean = false;
  submissionError: boolean = false;
  private currentQuestionSubscription!: Subscription;
  private countdownSubscription!: Subscription;

  constructor(private route: ActivatedRoute, private classroomService: ClassroomService, private quizService: CreateQuizService, private openAiService: OpenaiService) {
    this.classId = this.route.snapshot.params['classId'];
    this.assignmentId = this.route.snapshot.params['assignmentId'];
    this.getAssignment();
  }

  ngOnInit(): void {

    this.currentQuestionSubscription = this.classroomService.watchCurrentQuestion().subscribe((newQuestion: number) => {
          
      if (newQuestion >= this.assignment.questions.length) {
        let end = true;
        this.newAnswers.forEach((answer: AssignmentAnswer) => {
          if (answer.answer[0] === 999) {
            end = false;
            this.submissionError = true;
            this.classroomService.setCurrentQuestion(this.assignment.questions.length-1);
          }
        });
        if (end) {
          this.endAssignment();
        }
      } else {
        console.log("Next question:", newQuestion);
        this.currentQuestion = newQuestion;
        
        this.displayAnswer=false;
        this.assignment.questions[this.currentQuestion].answer.forEach((answer: number) => {
          this.currentAnswers = [this.assignment.questions[this.currentQuestion].choices[answer]];
        });
        
        
        // if (!this.newAnswers[this.currentQuestion]) {
        //   this.newAnswers[this.currentQuestion] = {questionType: this.assignment.questions[this.currentQuestion].questionType, answer: []};
        // }

        // this.countdown = this.assignment.questions[this.currentQuestion].timeSeconds;

        if (this.assignment.questions[this.currentQuestion].questionType === 'Ordering') {
          this.orderedChoices = [...this.assignment.questions[this.currentQuestion].choices];
        } else if (this.assignment.questions[this.currentQuestion].questionType === 'Drag and Drop') {
          this.questionParts = this.createQuestionParts(this.assignment.questions[this.currentQuestion].question);
          this.dragAndDropQuestion = this.formatDragAndDropQuestion(this.assignment.questions[this.currentQuestion].question);
          this.dragAndDropChoices = [...this.assignment.questions[this.currentQuestion].choices];
        } else if (this.assignment.questions[this.currentQuestion].questionType === 'Fill The Blanks') {
          this.questionPartsFtb = this.createFTB(this.assignment.questions[this.currentQuestion].question);
        }

        
      }

    });

    
    
  }

  ngOnDestroy() {
    this.currentQuestionUnsubscribe();
    this.countdownUnsubscribe();
  }

  startTimer() {
    if (!this.countdownSubscription) {
      this.countdownSubscription = interval(1000)
      .pipe(take(this.countdown))
      .subscribe({
        next: () => {
          this.countdown--;
        },
        complete: () => {
          this.currentQuestion = this.assignment.questions.length;
          this.endAssignment();
        }
      });
    }
  }

  getAssignment() {
    this.classroomService.getAssignment(this.assignmentId).subscribe({
      next: data => {
        this.assignment = data.body;
        this.countdown = this.assignment.timer*60;
        this.startTimer();
        this.classroomService.setCurrentQuestion(this.currentQuestion);
        this.assignment.questions.forEach((question: QuizQuestions) => {
          this.newAnswers.push({questionType: String(question.questionType), answer: [999]});
        });
        console.log(this.newAnswers);
      },
      error: error => {
        console.log(error);
      }
    })
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

  formatDragAndDropQuestion(text: string): string[] {
    const parts = text.split(/(_)/);

    let question: string[] = [];
    let maxLength = 0;
    this.assignment.questions[this.currentQuestion].choices.forEach((str: string) => {
      maxLength = Math.max(maxLength, str.length);
    });
    let str = '_';
    // Loop through each part of the text
    parts.forEach(part => {
      if (part === "_") {
        // If the part is a blank, push a blank object with null value
        question.push('' + str.repeat(maxLength) + '');
      } else {
        // If the part is not a blank, push a blank object with the text value
        question.push(part);
      }
    });

    return question;

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

  submitAnswer(questionType: string, index?: number) {
    // this.countdownUnsubscribe();
    this.newAnswers[this.currentQuestion] = {questionType: this.assignment.questions[this.currentQuestion].questionType, answer: []};

    let indexes: number[] = [];

    switch (this.assignment.questions[this.currentQuestion].questionType) {
      case 'True/False':
      case 'Multiple Answers':
      case 'Multiple Choice':
        this.newAnswers[this.currentQuestion].answer = [index!];
        break;
      case 'Ordering':
        this.assignment.questions[this.currentQuestion].choices.forEach((choice: String) => {
        const index = this.orderedChoices.indexOf(choice);
        indexes.push(index);
        });
        this.newAnswers[this.currentQuestion].answer = [...indexes];
        break;
      case 'Drag and Drop':
        this.dragAndDropAnswer.forEach((answer: string) => {
        const index = this.assignment.questions[this.currentQuestion].choices.indexOf(answer);
        indexes.push(index);
        });

        this.newAnswers[this.currentQuestion].answer = [...indexes];

        this.dragAndDropAnswer = [];
        break;
      case 'Fill The Blanks':
        this.result = this.checkAnswer(this.ftbAnswer, this.assignment?.questions[this.currentQuestion].choices, this.threshold);
        break;
      default:
    }
  
    this.displayAnswer = true;

  }

  nextQuestion() {
    // this.countdownSubscription.unsubscribe();
    this.ftbAnswer = '';
    this.result = undefined;
    this.currentAnswers = [];
    this.orderedChoices = [];
    this.classroomService.setCurrentQuestion(++this.currentQuestion);
  }

  lastQuestion() {
    if (this.currentQuestion > 0) {
      // this.countdownSubscription.unsubscribe();
      this.ftbAnswer = '';
      this.result = undefined;
      this.currentAnswers = [];
      this.orderedChoices = [];
      this.classroomService.setCurrentQuestion(--this.currentQuestion);
    }
  }

  private countdownUnsubscribe(): void {
    if (this.countdownSubscription) {
      this.countdownSubscription.unsubscribe();
    }
  }

  private currentQuestionUnsubscribe():void {
    if (this.currentQuestionSubscription) {
      this.currentQuestionSubscription.unsubscribe();
    }
  }

  private updateCurrentQuestion(): void {

    if (this.currentQuestion == this.assignment.questions.length-1) {

      this.currentQuestionUnsubscribe();

    } else {

      if (!this.newAnswers[this.currentQuestion].answer) {
        this.newAnswers[this.currentQuestion].answer = [999];
        this.displayAnswer = true;
      }

    }
    
  }

  dropOrdering(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.orderedChoices, event.previousIndex, event.currentIndex);
    console.log(this.orderedChoices);
  }

  endAssignment() {

    this.newAnswers.forEach((answer: AssignmentAnswer) => {
      if (answer.answer[0] === 999) {
        this.submissionError = true;
        return;
      }
    });

    this.countdownUnsubscribe();
    const completionTime = this.assignment.timer*60 - this.countdown;
    let exitedBeforeFinished: boolean = false;
    
    const completion: CompletionRequest = {assignmentId: this.assignmentId, classId: this.classId, answers: this.newAnswers, completionTime: completionTime, timeOver: this.countdown===0, exitedBeforeFinished: this.quit};
    console.log(completion);

    this.classroomService.submitAssignment(completion).subscribe({
      next: data => {
        this.submitted = true;
        this.submissionConfirmation = 'Click here to return to class.';
        const completionResponse: AssignmentCompletion | null = data.body;
        if (completionResponse) {
          this.updateQuestionDifficulties(completionResponse);
        }
      },
      error: error => {
        console.log(error);
        this.submissionConfirmation = 'Error submitting assignment. Click to retry.'
        this.submitted = false;
      }
    });

  }

  updateQuestionDifficulties(completion: AssignmentCompletion) {

    let difficulty: Difficulty = {questionIds: completion.assignment.questions.map(obj => obj.questionBankId || null), answers: completion.answer};

    this.openAiService.updateQuestionDifficulty(difficulty).subscribe({
      next:data => {
        console.log("difficulty updated");
      },
      error: error => {
        console.log(error);
      }
    })

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

  compareArrays(array1: any[], array2: any[]): boolean {
    if (this.assignment?.questions[this.currentQuestion].questionType == 'Ordering' || this.assignment?.questions[this.currentQuestion].questionType == 'Drag and Drop') {
      if (array1.length === array2.length && array1.every((value, index) => value === array2[index])) {
        this.score++;
        return true;
      } else {
        return false;
      }
    } else {
      if (this.assignment?.questions[this.currentQuestion].answer.indexOf(this.newAnswers[this.currentQuestion].answer[0]) >= 0) {
        this.score++;
        return true;
      } else {
        return false;
      }
    }
    
  }

  checkAnswer(studentAnswer: string, modelAnswers: string[], threshold: number): boolean {
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
        this.newAnswers[this.currentQuestion].answer = [this.assignment?.questions[this.currentQuestion].choices.indexOf(modelAnswer)];
        this.score++;
        return true;
      }
    }
    this.newAnswers[this.currentQuestion].answer = [998];
    return false;
  }

  jumpQuestion(index: number) {
    if (this.newAnswers[index]?.answer?.length > 0) {
      // this.countdownSubscription.unsubscribe();
      this.ftbAnswer = '';
      this.result = undefined;
      this.currentAnswers = [];
      this.orderedChoices = [];
      this.classroomService.setCurrentQuestion(index);
    }
  }

  getCountdown(): string {
    let hours,minutes,seconds = 0;
    hours = Math.floor(this.countdown / 3600);
    minutes = Math.floor((this.countdown % 3600) / 60);
    seconds = this.countdown % 60;

    if (hours > 0) {
      return `${hours}:${this.formatTime(minutes)}:${this.formatTime(seconds)}`;
    } else {
      return `${this.formatTime(minutes)}:${this.formatTime(seconds)}`;
    }
  }

  private formatTime(time: number): string {
    return time < 10 ? `0${time}` : `${time}`;
  }
  
}
