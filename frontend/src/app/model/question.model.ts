export interface Question {
    id: number;
    userId: number | undefined;
    question: String;
    choices: String[];
    answer: number[];
    topics: String[] | undefined;
    generatedAt: Date;
    questionType: String;
    difficulty: number | undefined;
}

export interface DragAndDrop {
    text: string;
    blanks: Blank[];
}

export interface Blank {
    value: string | null;
}

export interface Difficulty {
    questionIds: (number | null)[];
    answers: boolean[];
}