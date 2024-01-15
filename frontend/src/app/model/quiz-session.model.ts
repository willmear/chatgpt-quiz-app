export interface QuizSession {
    id: number;
    userId: number;
    quizCoreId: number;
    quiz: SessionQuiz;
    state: State;
    joinCode: String;
}

export interface SessionQuiz {
    id: number;
    quizCoreId: number;
    userId: number;
    questions: SessionQuestion[];
    title: String;
    currentQuestion: number;
}

export interface SessionQuestion {
    id: number;
    question: String;
    choices: String[];
    answer: number[];
    questionType: String;
    timeSeconds: number;
    pointsMultiplier: number;
}

export interface Participant {
    id: number;
    alias: String;
    points: number;
    status: Status;
    answers: number[]; 
    quizSessionId: number;
}

export enum State {
    WAITING_FOR_PLAYERS = 'WAITING_FOR_PLAYERS',
    PRE_QUESTION = 'PRE_QUESTION',
    QUESTION = 'QUESTION',
    POST_QUESTION = 'POST_QUESTION',
    LEADERBOARD = 'LEADERBOARD',
    FINISHED = 'FINISHED'
}

export enum Status {
    ACTIVE,
    OFFLINE,
    FINISHED
}