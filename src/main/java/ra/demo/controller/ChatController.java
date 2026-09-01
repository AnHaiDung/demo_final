package ra.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ra.demo.service.ChatWithRAGTools;
import ra.demo.service.IngestDocumentService;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final IngestDocumentService  ingestDocumentService;
    private final ChatWithRAGTools  chatWithRAGTools;

    @PostMapping("/ingest")
    public String ingest(){
        return ingestDocumentService.ingestDocument();
    }

    @GetMapping
    public String chat(@RequestParam String userMessage,
                       @RequestParam String conversationId){
        return chatWithRAGTools.chatRAGTool(userMessage,conversationId);
    }
}
