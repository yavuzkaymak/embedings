package com.example.embeddings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class EmbeddingsApplicationTests {

	@Container
	public static OpensearchContainer<?> opensearch = new OpensearchContainer<>(
			DockerImageName.parse("opensearchproject/opensearch:2.11.0"))
			.withExposedPorts(9200);

	@BeforeAll
    static void starter(){
		opensearch.start();
	}

	@Test
	void load() throws InterruptedException {

		Thread.sleep(10000);

	}

}
