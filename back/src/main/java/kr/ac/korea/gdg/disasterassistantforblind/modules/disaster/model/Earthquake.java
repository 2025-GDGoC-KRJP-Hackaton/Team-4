package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Disaster entity representing disaster information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Earthquake {
    
    private String id;
    private LocalDateTime time;
    private Double magnitude;
    private Double latitude;
    private Double longitude;
    private Double depth;
    private String location;
    private Boolean active;
    private LocalDateTime createdAt;
    
    public boolean isActive() {
        return active != null && active;
    }
}