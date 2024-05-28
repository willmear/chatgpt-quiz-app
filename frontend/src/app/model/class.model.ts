import { QuizQuestions } from "./quiz.model";

export interface Classroom {
    id: number;
    name: string;
    adminId: number;
    createdAt: Date;
    joinCode: string;
    members: Member[];
    assignments: Assignment[];
}

export interface Member {
    id: number;
    memberId: number;
}

export interface Assignment {
    id: number;
    questions: QuizQuestions[];
    classroom: Classroom;
    releaseTime: Date;
    deadline: Date
    name: string;
    timer: number;
    multipleAttempts: boolean;
}

export interface AssignmentCompletion {
    id: number;
    completionTime: number;
    answer: boolean[];
    assignment: Assignment;
    member: Member;
    answers: AssignmentAnswer[];
    timeOver: boolean;
    submittedAt: Date;
    exitedBeforeFinished: boolean;
}

export interface AssignmentAnswer {
    id?: number;
    questionType: string;
    answer: number[];
}

export interface AssignmentResponse {
    assignment: Assignment;
    completions: AssignmentCompletion[];
}

export type NewClassroom = Omit<Classroom, 'id' | 'adminId' | 'createdAt' | 'joinCode' | 'members' | 'assignments'>
export type NewAssignment = Omit<Assignment, 'id' | 'assignmentCompletions'>
export type NewAssignmentForm = Omit<Assignment, 'id' | 'assignmentCompletions'>
export type CompletionRequest = {
    assignmentId: number;
    classId: number;
    answers: AssignmentAnswer[];
    completionTime: number;
    timeOver: boolean;
    exitedBeforeFinished: boolean;
}