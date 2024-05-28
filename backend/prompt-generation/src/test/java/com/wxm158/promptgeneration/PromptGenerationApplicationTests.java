package com.wxm158.promptgeneration;

import com.wxm158.promptgeneration.model.dto.ChatRequest;
import com.wxm158.promptgeneration.model.dto.Difficulty;
import com.wxm158.promptgeneration.model.dto.QuestionGeneration;
import com.wxm158.promptgeneration.model.entity.Question;
import com.wxm158.promptgeneration.repository.QuestionRepository;
import com.wxm158.promptgeneration.service.EmbeddingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptGenerationApplicationTests {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private EmbeddingService embeddingService;

    @Test
    public void testUpdateDifficulty() {
        Difficulty difficulty = new Difficulty();
        difficulty.setQuestionIds(Arrays.asList(1L, 2L));
        difficulty.setAnswers(Arrays.asList(true, false));

        when(questionRepository.findById(1L)).thenReturn(java.util.Optional.of(new Question()));
        when(questionRepository.findById(2L)).thenReturn(java.util.Optional.of(new Question()));

        ResponseEntity<Difficulty> responseEntity = embeddingService.updateDifficulty(difficulty);

        verify(questionRepository, times(2)).findById(anyLong());
        verify(questionRepository, times(2)).save(any(Question.class));

        assertEquals(difficulty, responseEntity.getBody());
    }

}
