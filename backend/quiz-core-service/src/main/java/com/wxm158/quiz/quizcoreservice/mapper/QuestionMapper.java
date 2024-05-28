package com.wxm158.quiz.quizcoreservice.mapper;


import com.wxm158.quiz.quizcoreservice.model.dto.request.QuestionRequest;
import com.wxm158.quiz.quizcoreservice.model.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    public List<Question> mapQuestionGenerationsToQuestions(List<QuestionRequest> questionGenerations) {
        return questionGenerations.stream()
                .map(this::mapQuestionGenerationToQuestion)
                .collect(Collectors.toList());
    }

    private Question mapQuestionGenerationToQuestion(QuestionRequest questionRequest) {
        return Question.builder()
                .questionBankId(questionRequest.getId())
                .question(questionRequest.getQuestion())
                .choices(questionRequest.getChoices())
                .answer(questionRequest.getAnswer())
                .questionType(questionRequest.getQuestionType())
                .timeSeconds(20)
                .points(200)
                .build();
    }

}
