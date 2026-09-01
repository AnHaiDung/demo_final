package ra.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;
import ra.demo.tools.MyTools;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatWithRAGTools {
    private final ChatClient   chatClient;
    private final PgVectorStore pgVectorStore;
    private final MyTools  myTools;

    private static final String SYSTEM_TEMPLATE = """
            Bạn là trợ lý AI, hãy sử dụng các tool đươợc đăng ký và phần CONTEXT được
            cung cấp để trả lời câu hỏi của khách hàng.
            
            Yêu cầu nghiêm ngặt:
            1. Chỉ sử dụng các tool và phần CONTEXT được cung cấp
            2. Không tự bịa thông tin ngoài nội dung của các tool và phần CONTEXT
            3. Nếu thông tin của các tool và phần CONTEXT không đủ để trả lời câu hỏi thì trả lời 
            theo nội dung sau: "Xin lỗi, tài liệu nội bộ hoặc các tool hiện có không đủ thông tin để trả lời câu hỏi của bạn. Hãy liên hệ
            với bộ phận chăm sóc khách hàng hoặc gọi tới số hotline của công ty để được hỗ trợ thêm "
            
            Phần CONTEXT được cung cấp:
            {context}
            """;
    public String chatRAGTool(String userQuery, String conversationId){
        SearchRequest request = SearchRequest.builder()
                .query(userQuery)
                .topK(3)
                .similarityThreshold(0.3)
                .build();

        List<Document> documents = pgVectorStore.similaritySearch(request);

        String context = documents.stream().map(Document::getText).collect(Collectors.joining("\n\n---\n\n"));

        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(SYSTEM_TEMPLATE);
        Message systemMessage = promptTemplate.createMessage(Map.of(
                "context", context
        ));

        UserMessage userMessage = new UserMessage(userQuery);

        Prompt prompt = new Prompt(systemMessage, userMessage);

        return chatClient.prompt(prompt)
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id",conversationId))
                .tools(myTools)
                .call()
                .content();
    }
}
