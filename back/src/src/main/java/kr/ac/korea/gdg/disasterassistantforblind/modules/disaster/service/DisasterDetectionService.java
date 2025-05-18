package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.service;

import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.mapper.EarthquakeMapper;
import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.Earthquake;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.service.UserService;
import kr.ac.korea.gdg.disasterassistantforblind.modules.notification.service.NotificationService;
import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.geojson.Feature;
import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.geojson.FeatureCollection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class DisasterDetectionService {

    @Value("${earthquake.api.url}")
    private String earthquakeApiUrl;

    @Value("${earthquake.api.min.magnitude:4.0}")
    private double minMagnitude;

    private final UserService userService;
    private final AiService aiService;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;
    private final EarthquakeMapper earthquakeMapper;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public DisasterDetectionService(
            UserService userService,
            AiService aiService,
            NotificationService notificationService,
            RestTemplate restTemplate,
            EarthquakeMapper earthquakeMapper) {
        this.userService = userService;
        this.aiService = aiService;
        this.notificationService = notificationService;
        this.restTemplate = restTemplate;
        this.earthquakeMapper = earthquakeMapper;
    }

    @Scheduled(fixedRateString = "${earthquake.api.polling.interval}")
    public void pollEarthquakeApi() {
        log.info("Polling earthquake API at: {}", LocalDateTime.now());

        try {
            // Get the current time in UTC for the updatedafter parameter
            LocalDateTime utcNow = LocalDateTime.now();
            // Go back 1 minute to ensure we don't miss any earthquakes
            LocalDateTime updateAfterTime = utcNow.minusMinutes(1);
            String updateAfterParam = updateAfterTime.format(ISO_FORMATTER).substring(0, 19);

            // Build the URL with parameters
            String url = UriComponentsBuilder.fromUriString(earthquakeApiUrl)
                    .queryParam("format", "geojson")
                    .queryParam("updatedafter", updateAfterParam)
                    .queryParam("minmagnitude", minMagnitude)
                    .build()
                    .toUriString();

            log.info("Calling earthquake API with URL: {}", url);

            // Call the earthquake API to get the latest earthquake data in GeoJSON format
            FeatureCollection featureCollection = restTemplate.getForObject(url, FeatureCollection.class);

            // Check if there are any earthquakes
            if (featureCollection != null && featureCollection.getFeatures() != null && !featureCollection.getFeatures().isEmpty()) {
                log.info("Received {} earthquakes from API", featureCollection.getFeatures().size());

                // Process each earthquake
                for (Feature feature : featureCollection.getFeatures()) {
                    // Convert GeoJSON feature to our internal EarthquakeData model
                    EarthquakeData earthquakeData = convertFeatureToEarthquakeData(feature);

                    if (earthquakeData != null) {
                        log.info("Earthquake detected: {}", earthquakeData);

                        // Store earthquake data in the database
                        storeEarthquakeData(earthquakeData);

                        // Process the earthquake data
                        processEarthquake(earthquakeData);
                    }
                }
            } else {
                log.info("No earthquakes found in the API response");
            }
        } catch (Exception e) {
            log.error("Error polling earthquake API", e);
        }
    }

    /**
     * Convert a GeoJSON Feature to our internal EarthquakeData model
     * 
     * @param feature The GeoJSON Feature
     * @return The converted EarthquakeData
     */
    private EarthquakeData convertFeatureToEarthquakeData(Feature feature) {
        try {
            if (feature == null || feature.getProperties() == null || feature.getGeometry() == null) {
                return null;
            }

            EarthquakeData earthquakeData = new EarthquakeData();

            // Set ID
            earthquakeData.setId(feature.getId());

            // Set properties
            earthquakeData.setMagnitude(feature.getProperties().getMag());
            earthquakeData.setLocation(feature.getProperties().getPlace());

            // Convert epoch time to LocalDateTime
            if (feature.getProperties().getTime() != null) {
                Instant instant = Instant.ofEpochMilli(feature.getProperties().getTime());
                earthquakeData.setTime(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            }

            // Set coordinates
            if (feature.getGeometry().getCoordinates() != null && feature.getGeometry().getCoordinates().length >= 3) {
                earthquakeData.setLongitude(feature.getGeometry().getCoordinates()[0]);
                earthquakeData.setLatitude(feature.getGeometry().getCoordinates()[1]);
                earthquakeData.setDepth(feature.getGeometry().getCoordinates()[2]);
            }

            // Set active status based on status property
            earthquakeData.setActive(!"deleted".equals(feature.getProperties().getStatus()));

            return earthquakeData;
        } catch (Exception e) {
            log.error("Error converting Feature to EarthquakeData", e);
            return null;
        }
    }

    /**
     * Store earthquake data in the database
     * 
     * @param earthquakeData The earthquake data to store
     */
    private void storeEarthquakeData(EarthquakeData earthquakeData) {
        try {
            // Check if earthquake already exists in the database
            Earthquake existingEarthquake = earthquakeMapper.findById(earthquakeData.getId());

            if (existingEarthquake == null) {
                // Convert EarthquakeData to Earthquake entity
                Earthquake earthquake = convertToEarthquakeEntity(earthquakeData);

                // Insert earthquake into the database
                earthquakeMapper.insert(earthquake);
                log.info("Stored earthquake in database: {}", earthquakeData.getId());
            } else {
                // Update active status if needed
                if (existingEarthquake.getActive() != earthquakeData.getActive()) {
                    earthquakeMapper.updateActiveStatus(earthquakeData.getId(), earthquakeData.isActive());
                    log.info("Updated earthquake active status in database: {}", earthquakeData.getId());
                }
            }
        } catch (Exception e) {
            log.error("Error storing earthquake data in database", e);
        }
    }

    /**
     * Convert EarthquakeData to Earthquake entity
     * 
     * @param earthquakeData The earthquake data to convert
     * @return The converted Earthquake entity
     */
    private Earthquake convertToEarthquakeEntity(EarthquakeData earthquakeData) {
        return Earthquake.builder()
                .id(earthquakeData.getId())
                .time(earthquakeData.getTime())
                .magnitude(earthquakeData.getMagnitude())
                .latitude(earthquakeData.getLatitude())
                .longitude(earthquakeData.getLongitude())
                .depth(earthquakeData.getDepth())
                .location(earthquakeData.getLocation())
                .active(earthquakeData.isActive())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void processEarthquake(EarthquakeData earthquakeData) {
        // Get all users
        List<User> users = userService.getAllUsers();

        // For each user, send their information along with earthquake data to Ai API asynchronously
        for (User user : users) {
            // Send data to Ai API asynchronously
            aiService.processEarthquakeForUserAsync(earthquakeData, user)
                .thenAccept(aiResponse -> {
                    // Send notification to the user with Ai's response when it's ready
                    notificationService.sendNotification(user, aiResponse);
                })
                .exceptionally(ex -> {
                    log.error("Error processing earthquake data for user: {}", user.getId(), ex);
                    notificationService.sendNotification(user, 
                        "Error processing earthquake data. Please stay safe and follow general earthquake safety guidelines.");
                    return null;
                });
        }
    }

    @Data
    public static class EarthquakeData {
        private String id;
        private LocalDateTime time;
        private Double magnitude;
        private Double latitude;
        private Double longitude;
        private Double depth;
        private String location;
        private Boolean active;

        public boolean isActive() {
            return active != null && active;
        }
    }
}