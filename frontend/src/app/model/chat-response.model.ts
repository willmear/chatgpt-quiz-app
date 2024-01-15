export interface ChatResponse {
    id: number;
    question: string;
    choices: string[];
    answer: number[];
    topics: string[];
    questionType: string;
}