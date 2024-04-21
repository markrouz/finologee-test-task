package com.mgerman.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class IbanValidatorClientImpl implements IbanValidatorClient {
	private static final String API_URL = "https://openiban.com/validate/";

	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean isValidIban(String iban) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(API_URL + iban))
				.GET()
				.build();
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			final String responseBody = response.body();
			log.info("Response from {}: {}", API_URL, responseBody);
			if (response.statusCode() == 200) {
				JsonNode jsonResponse = objectMapper.readTree(responseBody);
				return jsonResponse.get("valid").asBoolean();
			} else {
				log.error("Got status code {} from IBAN validation API. Message: {}", response.statusCode(), responseBody);
				return false;
			}
		} catch (InterruptedException e) {
			log.warn("Interrupted!", e);
			Thread.currentThread().interrupt();
			return false;
		} catch (Exception e) {
			log.error("Got an exception {} while calling IBAN validation API", e.getMessage());
			return false;
		}
	}
}
