# Embeddings with Langchain4j in Spring Boot / A Test

In this code's test, pdf format of a [EU Council press release](https://www.consilium.europa.eu/en/press/press-releases/2024/03/26/european-digital-identity-eid-council-adopts-legal-framework-on-a-secure-and-trustworthy-digital-wallet-for-all-europeans/#:~:text=The%20European%20digital%20identity%20wallet&text=Under%20the%20new%20law%2C%20member,%2C%20qualifications%2C%20bank%20account)
which is classpath will be read, embedded and saved in an Opensearch instance.

A conversational chat using [Ollama](https://github.com/ollama/ollama)'s orca-mini will be started 
and a question about the information to be retrieved from the pdf will be asked. 

Both for Opensearch and Ollama test containers will be used.

It takes quite a while to pull ollama container! So be patient for the first time docker pulling it.
