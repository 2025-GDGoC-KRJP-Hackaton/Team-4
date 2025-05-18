package kr.ac.korea.gdg.disasterassistantforblind.modules.medical.mapper;

import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.model.MedicalInfo;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MedicalInfoMapper {

    MedicalInfo findByUserId(String userId);

    void update(MedicalInfo medicalInfo);
}