package ra.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngestDocumentService {
    private final PgVectorStore  pgVectorStore;
    private final JdbcTemplate  jdbcTemplate;

    public String ingestDocument(){

        if(!jdbcTemplate.queryForList("select *  from vector_store").isEmpty()){
            return "Tài liệu đã được vector hóa";
        }

        Resource resource = new ClassPathResource("rikkeisoft_profile.pdf");
        TikaDocumentReader reader = new  TikaDocumentReader(resource);

        List<Document> documents = reader.read();

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(5)
                .withKeepSeparator(true)
                .withMaxNumChunks(10000)
                .build();
        List<Document> chunkedDocuments = splitter.apply(documents);

        pgVectorStore.accept(chunkedDocuments);
        return "Tài liệu "+resource.getFilename()+" đã được vector hóa và lưu vào csdl";
    }
}
