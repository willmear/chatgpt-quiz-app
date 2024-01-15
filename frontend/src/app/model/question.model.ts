export interface Question {
    id: number;
    userId: number;
    question: String;
    choices: String[];
    answer: number[];
    topics: String[];
    generatedAt: Date;
    questionType: String;
}
