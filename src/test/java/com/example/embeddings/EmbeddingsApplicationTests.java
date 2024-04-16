package com.example.embeddings;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.chat.ChatLanguageModel;

import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.junit.Before;
import org.junit.Test;
import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import dev.langchain4j.store.embedding.opensearch.OpenSearchEmbeddingStore;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Objects;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.*;


@SpringBootTest
@Testcontainers
public class EmbeddingsApplicationTests {

	static EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
	static EmbeddingStoreIngestor embeddingStoreIngestor;
	static OpenSearchEmbeddingStore embeddingStore;
	static ChatLanguageModel chatLanguageModel;

	static String OLLAMA_MODEL = "orca-mini";
	static Integer OLLAMA_PORT = 11434;


	@Container
	static OpensearchContainer<?> opensearch = new OpensearchContainer<>(
			DockerImageName.parse("opensearchproject/opensearch:2.11.0"))
			.withExposedPorts(9200);


	@Container
	static GenericContainer<?> ollama = new GenericContainer<>(String.format("langchain4j/ollama-%s:latest", OLLAMA_MODEL))
			.withExposedPorts(OLLAMA_PORT)
			.withStartupTimeout(Duration.ofSeconds(600));


	@Before
    public void starter()  {

		ollama.start();
		opensearch.start();


		 embeddingStore = OpenSearchEmbeddingStore
                .builder()
                .serverUrl(opensearch.getHttpHostAddress())
                .build();


		String ollamaUrl = String.format("http://%s:%d", ollama.getHost(), ollama.getMappedPort(OLLAMA_PORT));

		chatLanguageModel = OllamaChatModel.builder()
				.baseUrl(ollamaUrl)
				.modelName(OLLAMA_MODEL)
				.build();


		embeddingStoreIngestor = new EmbeddingStoreIngestor.Builder()
				.documentSplitter(DocumentSplitters.recursive(300, 0))
				.embeddingModel(embeddingModel)
				.embeddingStore(embeddingStore)
				.build();


	}

	@Test
	public void should_ingest_pdf_and_retrieve() throws InterruptedException {
		Document document = loadDocument(toPath("eu.pdf"), new ApachePdfBoxDocumentParser());

	    embeddingStoreIngestor.ingest(document);

		// time to ingest
		Thread.sleep(1000);

		ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
				.embeddingStore(embeddingStore)
				.embeddingModel(embeddingModel)
				.maxResults(1)
				.build();


		ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
				.chatLanguageModel(chatLanguageModel)
				.contentRetriever(contentRetriever)
				.build();

		String answer = chain.execute("When did the european council adopt a frame work for european digital identity?");
		System.out.println(answer);


	}

	private static Path toPath(String fileName) {
		try {
			return Paths.get(Objects.requireNonNull(EmbeddingsApplicationTests.class.getClassLoader().getResource(fileName)).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
