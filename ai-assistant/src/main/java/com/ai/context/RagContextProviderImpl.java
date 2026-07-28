package com.ai.context;

import com.ai.dto.ChatRequest;
import com.ai.dto.RagContext;
import com.ai.dto.RagDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RagContextProviderImpl implements RagContextProvider {
    private final VectorStore vectorStore;
    @Override
    public RagContext getContext(ChatRequest chatRequest) {
        String query = chatRequest.getMessage();
        log.info("Searching RAG context for question : {}",
                chatRequest.getMessage());
        SearchRequest searchRequest = SearchRequest.builder().query(query).topK(10).build();
        List<Document> documentList = vectorStore.similaritySearch(searchRequest);

        if(documentList.isEmpty() ||  documentList ==null){
            return RagContext.builder()
                    .query(query)
                    .context("")
                    .totalDocuments(0)
                    .documents(List.of())
                    .build();
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<RagDocument> documents = new ArrayList<>();
        for(Document document :documentList ){
            stringBuilder.append(document.getText()).append("\n\n");
            RagDocument ragDocument = RagDocument.builder().content(document.getText())
                    .metadata(document.getMetadata()).score(null)
                    .page(documentList.size()).fileName(document.getMetadata().get("fileName").toString())
                    .build();
            documents.add(ragDocument);
        }
        return RagContext.builder()
                .query(chatRequest.getMessage())
                .context(stringBuilder.toString())
                .totalDocuments(documents.size())
                .documents(documents)
                .build();

    }
}
