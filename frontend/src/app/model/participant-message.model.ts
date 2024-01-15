import { State } from "./quiz-session.model";

export interface ParticipantMessage {
    answer: number | undefined;
    participant: string;
    quizSessionId: number;
    type: MessageType;
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