package com.wxm158.promptgeneration.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wxm158.promptgeneration.OpenAI.OpenAiEmbeddingModel;
import com.wxm158.promptgeneration.model.dto.QuestionGeneration;
import com.wxm158.promptgeneration.weviate.WeaviateEmbeddingStore;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;
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
    String baseUrl = "https://api.openai.com/v1";
    String modelName = "text-embedding-ada-002";  // You can change this if needed
    Duration timeout = Duration.ofSeconds(120);  // You can change this if needed
    Integer maxRetries = 3;  // You can change this if needed
    Proxy proxy = null;  // You can provide a proxy if needed
    Boolean logRequests = true;  // Set to true if you want to log requests
    Boolean logResponses = true;  // Set to true if you want to log responses

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
                .host("question-generation-8jhkh9kk.weaviate.network")
                .avoidDups(true)
                .consistencyLevel("ALL")
                .build();
    }
//    Create Chat Model
    private OpenAiChatModel createChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .modelName("ft:gpt-3.5-turbo-1106:personal::8QL57zvu")
                .timeout(timeout)
                .temperature(0.7)
                .build();
    }

    private String format(List<TextSegment> relevantSegments) {
        return relevantSegments.stream()
                .map(TextSegment::text)
                .map(segment -> "..." + segment + "...")
                .collect(joining("\n\n"));
    }

    private ConversationalRetrievalChain getConversation() {

        OpenAiChatModel chatModel = createChatModel();
        EmbeddingModel embeddingModel = createEmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore();

        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(chatModel)
                .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel, 3))
                .chatMemory(MessageWindowChatMemory.withMaxMessages(1))
                .promptTemplate(PromptTemplate
                .from("Within the scope of topics: {{question}}\n\nCreate five new multiple" +
                        " choice questions and answers, return the answer as a JSON object:" +
                        " questions: [{String: question, List<String>: choices, int: answer," +
                        " List<String>: topics, String: questionType}]." +
                        " index from 0-3. questionType = Multiple Choice." +
                        " Topics should be a list of relevant and similar topics" +
                        " Base your answer on the following information:\n{{information}}"))
                .build();
    }

//    Generate Questions from topic
//    Response as JSON
//    Map JSON to QuestionGeneration DTO
//    Map QuestionGeneration DTO to Question Entity and save
//    Return QuestionGeneration DTO list. Will get from repo next
    public List<QuestionGeneration> generateQuestions(String message) {

        //        GET RELEVANT INFORMATION FOR GIVEN MESSAGE
        EmbeddingModel embeddingModel = createEmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore();
        Retriever<TextSegment> retriever = EmbeddingStoreRetriever.from(embeddingStore, embeddingModel, 3);

        ConversationalRetrievalChain chain = getConversation();
        List<String> topicList = new ArrayList<>(Arrays.asList("Software",
                "Hardware",
                "Application Software",
                "System Software",
                "Utility Programs",
                "Library Programs",
                "Translators",
                "Operating System Software",
                "Hardware and Software",
                "Resource Management",
                "Managing Input/Output Devices",
                "Memory Management",
                "Virtual Memory and Paging",
                "File Management",
                "Types of Programming Languages",
                "Machine Code",
                "Assembly Language",
                "High-level languages",
                "Translating High Level Languages",
                "Interpreter",
                "Compiler",
                "Bytecode",
                "Stored Program Concept",
                "Fetch-execute-cycle",
                "Registers",
                "Clock",
                "ALU",
                "Processor Performance",
                "Interrupts",
                "Interrupt Service Routine",
                "Priorities",
                "Vectored Interrupt Mechanism",
                "External Hardware Devices",
                "Digital Camera",
                "Barcode Reader",
                "RFID",
                "Laser Printer",
                "Magnetic Hard Disk",
                "Optical Disk",
                "Solid State Disk (SSD)",
                "Storage Devices Compared"
        ));

        for (String topic: topicList) {
            message = topic;
            String information = format(retriever.findRelevant(message));
            int questionAmount = 5;
            String questionType = "Multiple Choice";
            String prompt = "SCOPE: " + message + ", QUESTION_TYPE: " + questionType + ", QUESTION_AMOUNT: " + questionAmount + ", TEXT: "
                    + information;
            for (int i =0; i< 10; i++) {
                String response = chain.execute(message);
                int startIndex = response.indexOf("[");
                int endIndex = response.lastIndexOf("]");

                String q = response.substring(startIndex, endIndex + 1);


//        Creating Training data

                String escapedJsonString = StringEscapeUtils.escapeJava(q);
                String JSON = "{" +
                        "\"messages\": [" +
                        "{" +
                        "\"role\": \"system\"," +
                        "\"content\": \"You are an A-level Computer Science teacher. You aim to generate various" +
                        " questions for your students.\"" +
                        "}," +
                        "{" +
                        "\"role\": \"user\"," +
                        "\"content\": \"" + prompt + "\"" +
                        "}," +
                        "{" +
                        "\"role\": \"assistant\"," +
                        "\"content\": \"" + escapedJsonString + "\"" +
                        "}" +
                        "]" +
                        "}";
                JSON=JSON.replace("\n", " ");

                String filePath = "C:/Users/willl/OneDrive/Desktop/finetuning.jsonl";

                try (FileWriter fileWriter = new FileWriter(filePath, true)) {

                    fileWriter.write(JSON);
                    fileWriter.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(i + " loops complete for: " + message);
            }
        }


        return null;

    }

//        Create embedding
    public ResponseEntity<List<String>> createEmbedding() {

        EmbeddingModel embeddingModel = createEmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore();

        Document document = loadDocument(Paths.get("C:/Users/willi/Desktop/wxm158/Embeddings Data/AQA A Level Computer Science-part-6.pdf"));
        DocumentSplitter splitter = DocumentSplitters.recursive(100,
                0,
                new OpenAiTokenizer(GPT_3_5_TURBO));
        List<TextSegment> segments = splitter.split(document);

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();


        return ResponseEntity.ok(embeddingStore.addAll(embeddings, segments));

    }
}
