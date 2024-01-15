package com.wxm158.promptgeneration.mapper;

import com.wxm158.promptgeneration.model.dto.QuestionGeneration;
import com.wxm158.promptgeneration.model.entity.Question;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    public List<Question> mapQuestionGenerationsToQuestions(List<QuestionGeneration> questionGenerations, String userId) {
        return questionGenerations.stream()
                .map(questionGeneration -> mapQuestionGenerationToQuestion(questionGeneration, userId))
                .collect(Collectors.toList());
    }

    private Question mapQuestionGenerationToQuestion(QuestionGeneration questionGeneration, String userId) {
        return Question.builder()
                .userId(Long.valueOf(userId))// Assuming id in QuestionGeneration corresponds to userId in Question
                .question(questionGeneration.getQuestion())
                .choices(questionGeneration.getChoices())
                .answer(questionGeneration.getAnswer())
                .topics(questionGeneration.getTopics())
                .generatedAt(LocalDateTime.now())
                .questionType(questionGeneration.getQuestionType())
                .build();
    }

    public List<QuestionGeneration> mapQuestionsToQuestionGenerations(List<Question> questions) {
        return questions.stream()
                .map(this::mapQuestionToQuestionGeneration)
                .collect(Collectors.toList());
    }

    private QuestionGeneration mapQuestionToQuestionGeneration(Question question) {
        return QuestionGeneration.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .choices(question.getChoices())
                .answer(question.getAnswer())
                .topics(question.getTopics())
                .questionType(question.getQuestionType())
                .build();
    }
}
