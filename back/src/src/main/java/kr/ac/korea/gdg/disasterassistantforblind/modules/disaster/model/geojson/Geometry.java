package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.geojson;

import lombok.Data;

/**
 * Represents the geometry of a Feature in a GeoJSON FeatureCollection for disaster data
 */
@Data
public class Geometry {
    private String type;
    private double[] coordinates;
}