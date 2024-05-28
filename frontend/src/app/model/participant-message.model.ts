import { State } from "./quiz-session.model";

export interface ParticipantMessage {
    answer: Answer | undefined;
    participant: string;
    quizSessionId: number;
    type: MessageType;
}

export interface Answer {
    questionType: String;
    answer: number[];
}

export enum MessageType {
    JOIN = 'JOIN',
    ANSWER = 'ANSWER',
    LEAVE = 'LEAVE'
}

export interface NextQuestion {
    state: State;
    sessionId: number;
}