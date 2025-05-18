package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.geojson;

import lombok.Data;

/**
 * Represents a Feature in a GeoJSON FeatureCollection for disaster data
 */
@Data
public class Feature {
    private String type;
    private Properties properties;
    private Geometry geometry;
    private String id;
}