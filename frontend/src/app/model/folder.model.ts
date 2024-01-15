import { Quiz } from "./quiz.model";

export interface Folder {
    id: number;
    userId: number;
    quizzes: Quiz[];
    description: string;
}