package kr.ac.korea.gdg.disasterassistantforblind.modules.medical.mapper;

import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.model.MedicalInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MedicalInfoMapper {

    MedicalInfo findById(Long id);

    MedicalInfo findByUserId(String userId);

    void update(MedicalInfo medicalInfo);
}