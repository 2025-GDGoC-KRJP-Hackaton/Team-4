package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.geojson;

import lombok.Data;

/**
 * Represents the metadata section of a GeoJSON FeatureCollection for disaster data
 */
@Data
public class Metadata {
    private long generated;
    private String url;
    private String title;
    private int status;
    private String api;
    private int count;
}