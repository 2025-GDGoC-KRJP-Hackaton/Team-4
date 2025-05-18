package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for receiving location data as JSON
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}