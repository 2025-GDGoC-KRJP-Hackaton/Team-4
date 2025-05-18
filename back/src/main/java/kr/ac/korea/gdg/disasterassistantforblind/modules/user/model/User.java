package kr.ac.korea.gdg.disasterassistantforblind.modules.user.model;

import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.model.MedicalInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User entity representing a blind person's authentication information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String password;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean enabled;

    // Reference to medical information (not stored in database)
    private MedicalInfo medicalInfo;
}