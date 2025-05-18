package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.geojson;

import lombok.Data;

import java.util.List;

/**
 * Represents a GeoJSON FeatureCollection containing disaster data
 */
@Data
public class FeatureCollection {
    private String type;
    private Metadata metadata;
    private List<Feature> features;
    private double[] bbox;
}