package kr.ac.korea.gdg.disasterassistantforblind.modules.medical.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.model.MedicalInfo;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.service.MedicalInfoService;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling medical information separately from user registration
 */
@RestController
@RequestMapping("/api/medical")
@Tag(name = "Medical Information", description = "Medical information management API")
public class MedicalController {

    private final UserService userService;
    private final MedicalInfoService medicalInfoService;

    public MedicalController(UserService userService, MedicalInfoService medicalInfoService) {
        this.userService = userService;
        this.medicalInfoService = medicalInfoService;
    }

    /**
     * Update medical information for the authenticated user
     */
    @Operation(summary = "Update medical information", description = "Update medical information for the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Medical information updated successfully", 
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", 
                    content = @Content),
        @ApiResponse(responseCode = "401", description = "Not authenticated", 
                    content = @Content)
    })
    @PostMapping("/update")
    public ResponseEntity<?> updateMedicalInfo(
            @Parameter(hidden = true) Authentication authentication, 
            @Parameter(description = "Medical information to update", required = true) 
            @RequestBody Map<String, Object> medicalInfoMap) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Get the authenticated user
            User user = userService.getUserById(authentication.getName());
            String userId = user.getId();

            // Get existing medical info or create new one
            MedicalInfo medicalInfo = medicalInfoService.getMedicalInfoByUserId(userId);
            if (medicalInfo == null) {
                medicalInfo = new MedicalInfo();
                medicalInfo.setUserId(userId);
            }

            // Update medical information
            if (medicalInfoMap.containsKey("age")) {
                medicalInfo.setAge((Integer) medicalInfoMap.get("age"));
            }
            if (medicalInfoMap.containsKey("height")) {
                medicalInfo.setHeight(Double.valueOf(medicalInfoMap.get("height").toString()));
            }
            if (medicalInfoMap.containsKey("weight")) {
                medicalInfo.setWeight(Double.valueOf(medicalInfoMap.get("weight").toString()));
            }
            if (medicalInfoMap.containsKey("bloodType")) {
                medicalInfo.setBloodType((String) medicalInfoMap.get("bloodType"));
            }
            if (medicalInfoMap.containsKey("allergies")) {
                medicalInfo.setAllergies((String) medicalInfoMap.get("allergies"));
            }

            // Save or update the medical info
            medicalInfoService.saveOrUpdateMedicalInfo(userId, medicalInfo);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Medical information updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}