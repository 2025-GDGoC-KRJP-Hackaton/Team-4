package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.service;

import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.mapper.EarthquakeMapper;
import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.Earthquake;
import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.model.MedicalInfo;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.service.MedicalInfoService;
import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.service.DisasterDetectionService.EarthquakeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class AiService {

    @Value("${ai.server.url:http://127.0.0.1:8081/gemini}")
    private String aiServerUrl;

    private final RestTemplate restTemplate;
    private final EarthquakeMapper earthquakeMapper;
    private final MedicalInfoService medicalInfoService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public AiService(RestTemplate restTemplate, EarthquakeMapper earthquakeMapper, MedicalInfoService medicalInfoService) {
        this.restTemplate = restTemplate;
        this.earthquakeMapper = earthquakeMapper;
        this.medicalInfoService = medicalInfoService;
        log.info("Initialized AiService with AI server URL: {}", aiServerUrl);
    }

    /**
     * Process Earthquake data for a specific user using AI server
     * This method can be called in a blocking or non-blocking way
     * 
     * @param earthquakeData The Earthquake data
     * @param user The user data
     * @return The response from AI server
     */
    public String processEarthquakeForUser(EarthquakeData earthquakeData, User user) {
        try {
            // Get medical info for the user
            MedicalInfo medicalInfo = getMedicalInfoForUser(user.getId());

            // Call AI server with text prompt
            return callAiServerWithText(user, medicalInfo, null, null);
        } catch (Exception e) {
            log.error("Error processing Earthquake data for user: {}", user.getId(), e);
            return "Error processing Earthquake data. Please stay safe and follow general Earthquake safety guidelines.";
        }
    }

    /**
     * Process user location, image data, and voice prompt using AI server
     * 
     * @param user The user data
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param image Image file (JPEG)
     * @param voice Voice file (WebM)
     * @return The response from AI server
     */
    public String processUserLocationImageAndVoice(User user, Double latitude, Double longitude, MultipartFile image, MultipartFile voice) {
        try {
            // Get or create medical info for the user
            MedicalInfo medicalInfo = getMedicalInfoForUser(user.getId());

            // Call AI server with direct file upload
            return callAiServerWithFiles(user, medicalInfo, image, voice, latitude, longitude);
        } catch (Exception e) {
            log.error("Error processing location, image data, and voice prompt for user: {}", user.getId(), e);
            return "Error processing your location, image, and voice data. Please stay in an open area if possible and wait for help.";
        }
    }

    /**
     * Get or create medical info for a user without updating location
     * 
     * @param userId The user ID
     * @return The medical info
     */
    private MedicalInfo getMedicalInfoForUser(String userId) {
        // Get existing medical info or create new one
        MedicalInfo medicalInfo = medicalInfoService.getMedicalInfoByUserId(userId);
        if (medicalInfo == null) {
            medicalInfo = new MedicalInfo();
            medicalInfo.setUserId(userId);
        }

        return medicalInfo;
    }

    /**
     * Process Earthquake data for a specific user asynchronously
     * 
     * @param earthquakeData The Earthquake data
     * @param user The user data
     * @return A CompletableFuture that will contain the response from AI server
     */
    public CompletableFuture<String> processEarthquakeForUserAsync(EarthquakeData earthquakeData, User user) {
        return CompletableFuture.supplyAsync(() -> processEarthquakeForUser(earthquakeData, user), executorService);
    }

    /**
     * Process user location, image data, and voice prompt asynchronously
     * 
     * @param user The user data
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param image Image file (JPEG)
     * @param voice Voice file (WebM)
     * @return A CompletableFuture that will contain the response from AI server
     */
    public CompletableFuture<String> processUserLocationImageAndVoiceAsync(User user, Double latitude, Double longitude, MultipartFile image, MultipartFile voice) {
        return CompletableFuture.supplyAsync(() -> processUserLocationImageAndVoice(user, latitude, longitude, image, voice), executorService);
    }

    /**
     * Get recent earthquakes from the database
     * 
     * @return List of recent earthquakes
     */
    private List<Earthquake> getRecentEarthquakes() {
        try {
            // Get earthquakes from the last 24 hours
            LocalDateTime since = LocalDateTime.now().minusHours(24);
            return earthquakeMapper.findRecentEarthquakes(since);
        } catch (Exception e) {
            log.error("Error getting recent earthquakes", e);
            return List.of();  // Return empty list on error
        }
    }

    /**
     * Clean up resources when the service is destroyed
     */
    @PreDestroy
    public void cleanup() {
        try {
            if (!executorService.isShutdown()) {
                executorService.shutdown();
                log.info("Shut down executor service");
            }
        } catch (Exception e) {
            log.error("Error cleaning up resources", e);
        }
    }

    /**
     * Build a prompt with user information only
     *
     * @param user The user data
     * @param medicalInfo The medical information
     * @return A text prompt with user information
     */
    private String buildUserInfoPrompt(User user, MedicalInfo medicalInfo, Double latitude, Double longitude) {
        return "User Information:\n" +
                "- Name: " + user.getName() + "\n" +
                "- Age: " + (medicalInfo != null && medicalInfo.getAge() != null ? medicalInfo.getAge() : "Unknown") + "\n" +
                "- Blood type: " + (medicalInfo != null && medicalInfo.getBloodType() != null ? medicalInfo.getBloodType() : "Unknown") + "\n" +
                "- Allergies: " + (medicalInfo != null && medicalInfo.getAllergies() != null ? medicalInfo.getAllergies() : "None") + "\n" +
                "- Location: " + (latitude != null && longitude != null ? latitude + ", " + longitude : "Unknown") + "\n";
    }

    /**
     * Build a prompt with Earthquake information
     *
     * @return A text prompt with Earthquake information
     */
    private String buildEarthquakeInfoPrompt() {
        StringBuilder prompt = new StringBuilder();

        // Recent earthquakes information
        List<Earthquake> recentEarthquakes = getRecentEarthquakes();
        if (!recentEarthquakes.isEmpty()) {
            prompt.append("Recent Earthquakes Information:\n");
            int count = 0;
            for (Earthquake Earthquake : recentEarthquakes) {
                prompt.append("- Magnitude: ").append(Earthquake.getMagnitude())
                      .append(", Location: ").append(Earthquake.getLocation())
                      .append(", Time: ").append(Earthquake.getTime())
                      .append(", Depth: ").append(Earthquake.getDepth()).append(" km\n");

                count++;
                if (count >= 3) {  // Limit to 3 recent earthquakes
                    break;
                }
            }
        } else {
            prompt.append("No recent earthquakes detected.\n");
        }

        return prompt.toString();
    }

    /**
     * Call AI server with files (image and voice)
     *
     * @param user The user data
     * @param medicalInfo The medical information
     * @return The response from AI server
     */
    private String callAiServerWithText(User user, MedicalInfo medicalInfo, Double latitude, Double longitude) {
        return callAiServerWithFiles(user, medicalInfo, null, null, latitude, longitude);
    }

    /**
     * Call AI server with files (image and voice)
     * 
     * @param user The user data
     * @param medicalInfo The medical information
     * @param image Image file (JPEG)
     * @param voice Voice file (WebM)
     * @return The response from AI server
     */
    private String callAiServerWithFiles(User user, MedicalInfo medicalInfo, MultipartFile image, MultipartFile voice, Double latitude, Double longitude) {
        try {
            log.info("Calling AI server with files for user: {}", user.getId());

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create multipart request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Add user info as a separate parameter
            String userInfo = buildUserInfoPrompt(user, medicalInfo, latitude, longitude);
            body.add("user_profile", userInfo);

            // Add Earthquake info as a separate parameter
            String earthquakeInfo = buildEarthquakeInfoPrompt();
            body.add("earthquake_data", earthquakeInfo);

            // Add image if available
            if (image != null && !image.isEmpty()) {
                log.info("Adding image file to AI server request: {}, size: {}", image.getOriginalFilename(), image.getSize());
                body.add("image", image.getResource());
            }

            // Add voice if available
            if (voice != null && !voice.isEmpty()) {
                log.info("Adding voice file to AI server request: {}, size: {}", voice.getOriginalFilename(), voice.getSize());
                body.add("audio", voice.getResource());
            }

            // Create request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Call AI server
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiServerUrl + "/multimodal_alert",
                    requestEntity,
                    Map.class
            );
            log.info("AI server request: {}", aiServerUrl + "/multimodal_alert");

            // Extract and return the text response
            if (response.getBody() != null && response.getBody().containsKey("ai_message")) {
                return response.getBody().get("ai_message").toString();
            } else {
                return "No response from AI server. Please stay in an open area if possible and wait for help.";
            }
        } catch (Exception e) {
            log.error("Error calling AI server with files", e);
            return "Error processing your location, image, and voice data. Please stay in an open area if possible and wait for help.";
        }
    }
}