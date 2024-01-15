package com.wxm158.promptgeneration.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wxm158.promptgeneration.OpenAI.OpenAiEmbeddingModel;
import com.wxm158.promptgeneration.mapper.QuestionMapper;
import com.wxm158.promptgeneration.model.dto.ChatRequest;
import com.wxm158.promptgeneration.model.dto.QuestionGeneration;
import com.wxm158.promptgeneration.model.dto.TopicResponse;
import com.wxm158.promptgeneration.model.entity.Question;
import com.wxm158.promptgeneration.repository.QuestionRepository;
import com.wxm158.promptgeneration.weviate.WeaviateEmbeddingStore;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static java.util.stream.Collectors.joining;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    @Value("${OPENAI_API_KEY}")
    private String API_KEY;
    @Value("${WEAVIATE_API_KEY}")
    private String WEAVIATE_API_KEY;
    private static final String DEFAULT_NAMESPACE = "default"; // do not change, will break backward compatibility!
    private static final String DEFAULT_METADATA_TEXT_KEY = "text_segment"; // do not change, will break backward compatibility!
    String baseUrl = "https://api.openai.com/v1";
    String modelName = "text-embedding-ada-002";  // You can change this if needed
    Duration timeout = Duration.ofSeconds(120);  // You can change this if needed
    Integer maxRetries = 3;  // You can change this if needed
    Proxy proxy = null;  // You can provide a proxy if needed
    Boolean logRequests = true;  // Set to true if you want to log requests
    Boolean logResponses = true;  // Set to true if you want to log responses
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

//    Create embedding model
    private EmbeddingModel createEmbeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .apiKey(API_KEY)
                .modelName(modelName)
                .timeout(timeout)
                .maxRetries(maxRetries)
                .proxy(proxy)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
    }
//    Create embedding store
    private EmbeddingStore<TextSegment> createEmbeddingStore() {
        return WeaviateEmbeddingStore.builder()
                .apiKey(WEAVIATE_API_KEY)
                .scheme("https")
                .host("question-gen-wwxbinax.weaviate.network")
                .avoidDups(true)
                .consistencyLevel("ALL")
                .build();
    }
//    Create Chat Model
    private OpenAiChatModel createChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(API_KEY)
                // old key 8T6eTtmk
                .modelName("ft:gpt-3.5-turbo-1106:personal::8VzKieWR")
                .timeout(timeout)
                .temperature(0.3)
                .build();
    }

    private String format(List<TextSegment> relevantSegments) {
        return relevantSegments.stream()
                .map(TextSegment::text)
                .map(segment -> "..." + segment + "...")
                .collect(joining("\n\n"));
    }


//    Create question from embedding and fine tuning
    public List<QuestionGeneration> createQuestions(ChatRequest chatRequest) {

        String message = chatRequest.getTopic();
        String questionType = chatRequest.getQuestionType();
        String questionAmount = chatRequest.getQuestionAmount();

//        Initialise fine tuned chat model, embedding model, embedding store.
        OpenAiChatModel chatModel = createChatModel();
        EmbeddingModel embeddingModel = createEmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore();

//        retrieve relevant text from embedding store. max three text segments.
        Retriever<TextSegment> retriever = EmbeddingStoreRetriever.from(embeddingStore, embeddingModel, 1);
        String information = format(retriever.findRelevant(message)).replace("\n", " ");

//        Create the prompt in format used in training fine-tuned model.
        ChatMessage[] messagesArray = {
                new SystemMessage("You are an A-level Computer Science teacher. You aim to generate various questions for your students."),
                new UserMessage("SCOPE: " + message + ", QUESTION_TYPE: " + questionType + ", QUESTION_AMOUNT: " + questionAmount +
                        ", TEXT: " + information)
        };
        List<ChatMessage> messages = new ArrayList<>(List.of(messagesArray));
        System.out.println(messages.toString());

//        Get response from model (json list of questions and answers)
        Response<AiMessage> response = chatModel.generate(messages);
        String stringResponse = response.content().text();
        System.out.println(stringResponse);

//        Map response to List of QuestionGeneration object for the frontend.
        int startIndex = stringResponse.indexOf("[");
        int endIndex = stringResponse.lastIndexOf("]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<QuestionGeneration>>(){}.getType();
        List<QuestionGeneration> questions = gson.fromJson(stringResponse.substring(startIndex, endIndex + 1), type);
        for (QuestionGeneration question : questions) {
            question.setId((long) questions.indexOf(question));
            question.setQuestionType(questionType);
        }

        return questions;
    }

    // Save Questions in question-generation-db
    public List<Question> saveQuestions(List<QuestionGeneration> questions, String userId) {
        List<Question> questionList = questionMapper.mapQuestionGenerationsToQuestions(questions, userId);
        List<Question> savedQuestions = new ArrayList<>();

        for (Question question: questionList) {
            if (!questionRepository.existsByQuestionAndQuestionType(question.getQuestion(), question.getQuestionType())) {
                 savedQuestions.add(questionRepository.save(question));
            }
        }

        return savedQuestions;
    }

    public List<Question> getAllQuestions(String userId) {
        return questionRepository.findAllByUserId(Long.valueOf(userId));
//        return questionMapper.mapQuestionsToQuestionGenerations(questionList);
    }

}
