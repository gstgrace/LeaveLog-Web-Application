package com.lms.service;

import org.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Service class that communicates with the Gemini API to retrieve wellbeing tips.
 * This class handles the construction of HTTP requests and processes the JSON responses.
 */
@Service
public class GeminiApiClient {

  @Value("${gemini.api.key}")
  private String apiKey;

  @Value("${gemini.api.url}")
  private String apiUrl;

  private final RestTemplate restTemplate;
  private static final Logger log = LoggerFactory.getLogger(GeminiApiClient.class);

  public GeminiApiClient() {
    this.restTemplate = new RestTemplate();
  }

  /**
   * Fetches wellbeing tips from the Gemini API.
   *
   * @return a wellbeing tip as a String
   * @throws HttpClientErrorException if an error occurs during the HTTP request
   */
  public String getWellBeingTips() {
    String url = apiUrl + "?key=" + apiKey;  // Append the API key as a query parameter

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"Write a 2-3 sentence wellbeing tip.\"}]}]}";
    HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
      return extractAndCondenseTip(response.getBody());
    } catch (HttpClientErrorException e) {
      log.error("HttpClientErrorException: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
      throw e;
    }
  }

  /**
   * Extracts and condenses a wellbeing tip from a JSON response.
   *
   * @param jsonResponse the JSON response string from the API
   * @return a condensed version of the wellbeing tip
   * @throws JSONException if an error occurs during JSON parsing
   */
  private String extractAndCondenseTip(String jsonResponse) {
    try {
      JSONObject jsonObject = new JSONObject(jsonResponse);
      JSONObject content = jsonObject.getJSONArray("candidates")
          .getJSONObject(0)
          .getJSONObject("content");
      String tip = content.getJSONArray("parts")
          .getJSONObject(0)
          .getString("text");
      return tip;
    } catch (JSONException e) {
      log.error("Error processing JSON", e);
      return "Error in processing tip";
    }
  }
}
