package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.geojson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the properties of a Feature in a GeoJSON FeatureCollection for disaster data
 */
@Data
public class Properties {
    private Double mag;
    private String place;
    private Long time;
    private Long updated;
    private Integer tz;
    private String url;
    private String detail;
    private Integer felt;
    private Double cdi;
    private Double mmi;
    private String alert;
    private String status;
    private Integer tsunami;
    private Integer sig;
    private String net;
    private String code;
    private String ids;
    private String sources;
    private String types;
    private Integer nst;
    private Double dmin;
    private Double rms;
    private Double gap;
    private String magType;
    private String type;
    private String title;
}