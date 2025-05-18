package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.model.MedicalInfo;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.service.AiService;
import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.service.MedicalInfoService;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.service.UserService;
import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.dto.LocationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/disaster")
@Tag(name = "Disaster", description = "Disaster detection and response API")
@Slf4j
public class DisasterController {

    private final AiService aiService;
    private final UserService userService;
    private final MedicalInfoService medicalInfoService;

    public DisasterController(
            AiService aiService,
            UserService userService,
            MedicalInfoService medicalInfoService) {
        this.aiService = aiService;
        this.userService = userService;
        this.medicalInfoService = medicalInfoService;
    }

    /**
     * Endpoint for clients to send their location, image data, and voice prompt during an disaster
     * Uses multipart/form-data for file uploads with location data in JSON
     */
    @Operation(summary = "Send location and media", 
              description = "Send location, image data, and voice prompt during an disaster. " +
                           "Uses multipart/form-data for file uploads with location data in JSON.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location and media processed successfully", 
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Not authenticated", 
                    content = @Content),
        @ApiResponse(responseCode = "500", description = "Error processing location and media", 
                    content = @Content)
    })
    @PostMapping(value = "/prompt", consumes = {"multipart/form-data", "application/octet-stream"})
    public DeferredResult<ResponseEntity<?>> updateLocationAndFiles(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Location data (latitude, longitude, timestamp)", required = true) 
            @RequestPart(value = "location", required = false) LocationRequest locationRequest,
            @Parameter(description = "Image file (JPEG) of surroundings", required = false) 
            @RequestPart(value = "image", required = false) MultipartFile image,
            @Parameter(description = "Voice file (WebM) with user's message", required = false) 
            @RequestPart(value = "voice", required = false) MultipartFile voice) {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(30000L); // 30 seconds timeout

        if (authentication == null) {
            deferredResult.setResult(ResponseEntity.status(401).body("Authentication required"));
            return deferredResult;
        }

        log.debug("[Start Processing]");

        try {
            locationRequest = new LocationRequest(0.0, 0.0, LocalDateTime.now());

            // Get the authenticated user
            User user = userService.getUserById(authentication.getName());
            String userId = user.getId();

            // Extract location data from the JSON request
            Double latitude = locationRequest.getLatitude();
            Double longitude = locationRequest.getLongitude();
            log.debug("[Received Location] Latitude: {}, Longitude: {}", latitude, longitude);

            // Get or create medical info for the user
            MedicalInfo medicalInfo = medicalInfoService.getMedicalInfoByUserId(userId);
            if (medicalInfo == null) {
                medicalInfo = new MedicalInfo();
                medicalInfo.setUserId(userId);
            }

            // Log the timestamp received from the client
            log.info("Received location update with timestamp: {}", locationRequest.getTimestamp());

            // Process the location, image data, and voice prompt with Ai API asynchronously
            // Pass the MultipartFile objects directly to the service
            aiService.processUserLocationImageAndVoiceAsync(user, latitude, longitude, image, voice)
                .thenAccept(guidance -> {
                    // Return the guidance directly in the HTTP response instead of sending a separate notification
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Location, image data, and voice prompt processed");
                    response.put("guidance", guidance);
                    response.put("latitude", latitude);
                    response.put("longitude", longitude);

                    log.debug("[Processing Complete] Guidance: {}", guidance);
                    deferredResult.setResult(ResponseEntity.ok(response));
                })
                .exceptionally(ex -> {
                    log.error("Error processing location, image data, and voice prompt", ex);
                    deferredResult.setResult(ResponseEntity.status(500)
                        .body("Error processing location, image data, and voice prompt"));
                    return null;
                });

            return deferredResult;
        } catch (Exception e) {
            log.error("Error processing location, image data, and voice prompt", e);
            deferredResult.setResult(ResponseEntity.status(500)
                .body("Error processing location, image data, and voice prompt"));
            return deferredResult;
        }
    }
}