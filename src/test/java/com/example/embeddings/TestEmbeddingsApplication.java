package com.example.embeddings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestEmbeddingsApplication {

	public static void main(String[] args) {
		SpringApplication.from(EmbeddingsApplication::main).with(TestEmbeddingsApplication.class).run(args);
	}

}
