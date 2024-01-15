import { Component, OnInit } from '@angular/core';
import { OpenaiService } from '../service/openai.service';
import { ChatRequest } from '../model/chat-request.model';
import { ChatResponse } from '../model/chat-response.model';

@Component({
  selector: 'app-create-quiz',
  templateUrl: './create-quiz.component.html',
  styleUrl: './create-quiz.component.css'
})
export class CreateQuizComponent implements OnInit {

  // questions: ChatResponse[] | null = null;
  questions: ChatResponse[] = [];
  currentQuestion: number = 0;
  isDropdownOpen1: boolean = false;
  questionType: string = "Question Type"
  questionAmount: string = "No. of Questions"
  isDropdownOpen2: boolean = false;
  editableTitle: String = "";
  editTitle: boolean = false;
  topics: string[] = ["Software","Hardware","Resource Management", "Virtual Memory & Paging", "Main Memory", "Addressable Memory",
   "Buses", "I/O Controllers", "Von Neumann", "External Hardware Devices", "Digital Camera", "Barcode Reader", "RFID", "Laser Printer",
    "Magnetic Hard Disk", "Optical Disk", "Solid State Drive (SSD)", "Storage Devices Compared"];
  questionTypes: string[] = ["Multiple Choice","True/False", "Multiple Answers"]
  questionAmounts: string[] = ["1","2","3","4","5","6","7","8","9","10"]
  initialTopic: String = "";

  constructor(private openaiService: OpenaiService) {}

  ngOnInit(): void { 
   }

  // Scroll through questions
  goToQuestion(index: number): void {
    if (index >= 0 && index <= this.questions.length) {
      this.currentQuestion = index;
    }
  }

  //Delete current question
  deleteQuestion(index: number): void {
    console.log(index);
    this.questions.splice(index, 1);
    console.log(this.questions);
  }

  addTopic(topic: {topic:string}): void {
    if (topic.topic.length > 0 && !this.topics.includes(topic.topic)) {
      this.topics.push(topic.topic);
    }
  }

  
  chooseTopics(topicIds: { [key: number]: boolean }): void {
    const selectedTopics: number[] = [];
    let topicString: String = "[";

    for (const id in topicIds) {
      if (topicIds[id]) {
        const topicValue = this.topics.at(Number(id));
            if (topicValue !== undefined) {
                topicString += topicValue.toString();
                topicString += ","
                selectedTopics.push(Number(id));
            }
      }
    }
    topicString = topicString.slice(0, -1) + "]";
    console.log(topicString);

    const chat: ChatRequest = {topic: topicString, questionType: this.questionType, questionAmount: this.questionAmount};
    
    console.log("Sending topics + Getting questions")
    this.openaiService.createQuestions(chat).subscribe({
      next: data => {
        console.log(data);
        if (this.questions) {
          data.body.forEach((question: ChatResponse) => {
            this.questions?.push(question);
          });          
          this.questions.forEach((question, index) => {
            question.id = index;
          });
        } else {
          this.questions = data.body;
          this.currentQuestion = 0;
        }
        console.log(this.questions);
      },
      error: error => {
        console.log(error);
      }
    });

  }
  
  toggleDropdown1() {
    this.isDropdownOpen1 = !this.isDropdownOpen1;
  }
  toggleDropdown2() {
    this.isDropdownOpen2 = !this.isDropdownOpen2;
  }

  updateSelectedOption1(option: string) {
    this.questionType=option;
    this.toggleDropdown1();
  }
  updateSelectedOption2(option: string) {
    this.questionAmount=option;
    this.toggleDropdown2();
  }

  save(): void {
    if (this.questions.length > 0) {
      this.openaiService.saveQuestions(this.questions).subscribe({
        next: data => {
          console.log(data);
          this.questions = [];
          this.currentQuestion = 0;
        },
        error: error => {
          console.log(error);
        }
      });
    }
  }

  onQuestionBlur(event: any, index: number) {
    const editedQuestion = event.target.innerText;
    this.questions[index].question = editedQuestion;
  }

  getQuestion(index: number) {
    return this.questions && this.questions[index].question ? this.questions[index].question : '';
  }

  editQuestionChoice(event: any, index: number) {
    const editedChoice = event.target.innerText;
    this.questions[this.currentQuestion].choices[index] = editedChoice;
  }
  
  changeCorrectChoice(index: number) {

    if (this.questions[this.currentQuestion].answer.includes(index) && this.questions[this.currentQuestion].answer.length > 1) {
      this.questions[this.currentQuestion].answer.splice(this.questions[this.currentQuestion].answer.indexOf(index),1);
    } else if (!(this.questions[this.currentQuestion].answer.includes(index))) {
      this.questions[this.currentQuestion].answer.push(index);
    }

    console.log(this.questions[this.currentQuestion]);
    
  }

}
