package kr.ac.korea.gdg.disasterassistantforblind.modules.medical.model;

import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MedicalInfo entity representing a blind person's medical information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalInfo {
    
    private Long id;
    private String userId;
    private Integer age;
    private Double height; // in cm
    private Double weight; // in kg
    private String bloodType;
    private String allergies;
    
    // Reference to the user (not stored in database)
    private User user;
}