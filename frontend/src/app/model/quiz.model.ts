export interface Quiz {
    id: number;
    userId: number;
    questions: QuizQuestions[]
    title: String;
    isDraft: boolean;
    createdAt: Date;
    lastPlayed: Date;
    plays: number;
}

export interface QuizQuestions {
    id: number | undefined;
    questionBankId: number | undefined;
    question: String;
    choices: String[];
    answer: number[];
    questionType: String;
    timeSeconds: number;
    points: number;
    difficulty: number | undefined;
}