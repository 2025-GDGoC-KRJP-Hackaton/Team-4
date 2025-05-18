package kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.mapper;

import kr.ac.korea.gdg.disasterassistantforblind.modules.disaster.model.Earthquake;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EarthquakeMapper {

    Earthquake findById(String id);

    List<Earthquake> findRecentEarthquakes(LocalDateTime since);

    void insert(Earthquake earthquake);

    void updateActiveStatus(String id, Boolean active);
}