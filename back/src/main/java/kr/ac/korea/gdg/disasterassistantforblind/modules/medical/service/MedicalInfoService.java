package kr.ac.korea.gdg.disasterassistantforblind.modules.medical.service;

import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.mapper.MedicalInfoMapper;
import kr.ac.korea.gdg.disasterassistantforblind.modules.medical.model.MedicalInfo;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalInfoService {

    private final MedicalInfoMapper medicalInfoMapper;

    public MedicalInfoService(MedicalInfoMapper medicalInfoMapper) {
        this.medicalInfoMapper = medicalInfoMapper;
    }

    public MedicalInfo getMedicalInfoByUserId(String userId) {
        return medicalInfoMapper.findByUserId(userId);
    }

    @Transactional
    public void updateMedicalInfo(MedicalInfo medicalInfo) {
        medicalInfoMapper.update(medicalInfo);
    }

    /**
     * Create or update medical information for a user
     * 
     * @param userId The user ID
     * @param medicalInfo The medical information to save
     */
    @Transactional
    public void saveOrUpdateMedicalInfo(String userId, MedicalInfo medicalInfo) {
        MedicalInfo existingMedicalInfo = medicalInfoMapper.findByUserId(userId);
        
        if (existingMedicalInfo == null) {
            // Create new medical info
            medicalInfo.setUserId(userId);
        } else {
            // Update existing medical info
            medicalInfo.setId(existingMedicalInfo.getId());
            medicalInfo.setUserId(userId);
            updateMedicalInfo(medicalInfo);
        }
    }
}