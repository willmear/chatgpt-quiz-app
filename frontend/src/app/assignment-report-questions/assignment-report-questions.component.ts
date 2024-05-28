import { Component, OnInit } from '@angular/core';
import { ClassroomService } from '../service/classroom.service';
import { ActivatedRoute } from '@angular/router';
import { Assignment, AssignmentCompletion } from '../model/class.model';
import { UserService } from '../service/user.servce';
import { User } from '../model/user.model';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-assignment-report-questions',
  templateUrl: './assignment-report-questions.component.html',
  styleUrl: './assignment-report-questions.component.css'
})
export class AssignmentReportQuestionsComponent implements OnInit {

  assignmentId: number;
  completions: AssignmentCompletion[] = [];
  assignment: Assignment | undefined;
  students: User[] = [];
  question1: any = [];
  chartData: any[] = [];
  correct: string[] = [];

  constructor(private classroomService: ClassroomService, private route: ActivatedRoute, private userService: UserService) {
    this.assignmentId = +this.route.snapshot.params['assignmentId'];
  }

  ngOnInit(): void {
    this.getCompletions();
  }

  // ngAfterViewInit(): void {
  //   if (this.assignment) {
  //     this.createCharts();
  //   }
  // }


  getCompletions() {
    this.classroomService.getAssignmentCompletionsAdmin(this.assignmentId).subscribe({
      next:data => {
        this.completions = data.body ?? [];
        this.assignment = this.completions[0].assignment;
        // this.classroom = this.assignment.classroom;
        // this.members = this.classroom.members;
        // this.getStudents();
        this.correct = Array(this.assignment.questions.length).fill(0);
        if (this.assignment) {
          this.createCharts();
        }
      },
      error: error => {
        console.log(error);
      }
    });
  }

  // createCharts() {
  //   this.assignment?.questions.forEach((_, index) => {
  //     this.chartData.push(this.createChartsDynamic(index));
  //   });
  // }

  createCharts() {
    setTimeout(() => {
      this.assignment?.questions.forEach((_, index) => {
        this.createChartsDynamic(index);
      });
    }, 100);

    let tempCorrect: number[] = Array(this.assignment!.questions.length).fill(0);
    this.completions.forEach((completion: AssignmentCompletion) => {
      for (let j = 0; j< completion.answer.length; j++) {
        if (completion.answer[j] === true) {
          tempCorrect[j]++;
        }
      }
    });
    this.correct = tempCorrect.map(obj => Math.round((obj/this.completions.length)*100) + '%');
  }


  createChartsDynamic(questionIndex: number): any {


    const question = this.assignment?.questions[questionIndex];
    if (!question) {
      return;
    }

    if (question.questionType !== 'Ordering' && question.questionType !== 'Fill The Blanks') {

      const labels = question.choices;
      labels.push("No Answer");
      let data = Array(question.choices.length).fill(0);
      let backgroundColors = Array(question.choices.length+1).fill('rgba(54, 162, 235, 0.2)');
      let borderColors = Array(question.choices.length+1).fill('rgba(54, 162, 235)');

      question.answer.forEach((index: number) => {
        backgroundColors[index] = 'rgb(34, 197, 94, 0.2)';
        borderColors[index] = 'rgba(34, 197, 94)';
      });
  
      this.completions.forEach((completion: AssignmentCompletion) => {

        const answer = completion.answers[questionIndex]?.answer;
        if (!answer) {
          return;
        } 
        
        for (let i = 0; i < answer.length; i++) {
          const index = answer[i];
          if (index < data.length) {
            data[index]++;
          } else {
            data[data.length-1]++;
          }
        }
      });

      console.log(data);

      data = data.map(obj => Math.round((obj / data.reduce((accumulator, currentValue) => accumulator + currentValue, 0)) * 100));

      return new Chart(`question${questionIndex}`, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            data: data,
            backgroundColor: backgroundColors,
            borderColor: borderColors,
            borderWidth: 1,
            minBarLength: 5,
          }],
        },
        options: {
          indexAxis: 'y',
          plugins: {
            legend: {
              display: false
            },
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                precision: 0,
                font: {
                  size: 16,
                }
              },
              grid: {
                drawOnChartArea: false
              }
            },
            x: {
              beginAtZero: true,
              ticks: {
                callback: function (value, index, values) {
                  return value + '%';
                },
                maxTicksLimit: 11,
                stepSize: 10,
                maxRotation: 0,
                minRotation: 0,
                font: {
                  size: 16,
                }
              },
              grid: {
                drawOnChartArea: false
              }
            }
          },
          aspectRatio: 5,
        },
      });

    } else if (question.questionType === 'Ordering') {

      const labels = ['Correct', 'Incorrect'];
      let data = Array(2).fill(0);

      this.completions.forEach((completion: AssignmentCompletion) => {
        
        if (completion.answer[questionIndex] === true) {
          data[0]++;
        } else if (completion.answer[questionIndex] === false) {
          data[1]++;
        }

      });

      data = data.map(obj => Math.round((obj / data.reduce((accumulator, currentValue) => accumulator + currentValue, 0)) * 100));

      return new Chart(`question${questionIndex}`, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            data: data,
            backgroundColor: [
              'rgb(34, 197, 94, 0.2)',
              'rgba(54, 162, 235, 0.2)'
            ],
            borderColor: [
              'rgb(34, 197, 94)',
              'rgb(54, 162, 235)'
            ],
            borderWidth: 1,
            minBarLength: 5,
          }],
        },
        options: {
          indexAxis: 'y',
          plugins: {
            legend: {
              display: false
            },
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                precision: 0,
                font: {
                  size: 16,
                }
              },
              grid: {
                drawOnChartArea: false
              }
            },
            x: {
              beginAtZero: true,
              ticks: {
                callback: function (value, index, values) {
                  return value + '%';
                },
                maxTicksLimit: 11,
                stepSize: 10,
                maxRotation: 0,
                minRotation: 0,
                font: {
                  size: 16,
                }
              },
              grid: {
                drawOnChartArea: false
              }
            }
          },
          aspectRatio: 5,
        },
      });

    } else if (question.questionType === 'Fill The Blanks') {

      const labels = question.choices;
      labels.push("Incorrect");
      let data = Array(question.choices.length).fill(0);
      let backgroundColors = Array(question.choices.length+1).fill('rgba(54, 162, 235, 0.2)');
      let borderColors = Array(question.choices.length+1).fill('rgba(54, 162, 235)');

      question.answer.forEach((index: number) => {
        backgroundColors[index] = 'rgb(34, 197, 94, 0.2)';
        borderColors[index] = 'rgba(34, 197, 94)';
      });
  
      this.completions.forEach((completion: AssignmentCompletion) => {

        const answer = completion.answers[questionIndex]?.answer;
        if (!answer) {
          return;
        } 
        
        for (let i = 0; i < answer.length; i++) {
          const index = answer[i];
          if (index < data.length) {
              data[index]++;
          } else {
            console.log("here")
            data[data.length-1]++;
          }
        }
      });

      console.log(data);

      data = data.map(obj => Math.round((obj / data.reduce((accumulator, currentValue) => accumulator + currentValue, 0)) * 100));

      return new Chart(`question${questionIndex}`, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            data: data,
            backgroundColor: backgroundColors,
            borderColor: borderColors,
            borderWidth: 1,
            minBarLength: 5,
          }],
        },
        options: {
          indexAxis: 'y',
          plugins: {
            legend: {
              display: false
            },
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                precision: 0,
                font: {
                  size: 16,
                }
              },
              grid: {
                drawOnChartArea: false
              }
            },
            x: {
              beginAtZero: true,
              ticks: {
                callback: function (value, index, values) {
                  return value + '%';
                },
                maxTicksLimit: 11,
                stepSize: 10,
                maxRotation: 0,
                minRotation: 0,
                font: {
                  size: 16,
                }
              },
              grid: {
                drawOnChartArea: false
              }
            }
          },
          aspectRatio: 5,
        },
      });

    }
  
    
  
    
  
  }
  


}
