package kr.ac.korea.gdg.disasterassistantforblind.modules.user.mapper;

import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User findById(String id);

    List<User> findAll();

    void insert(User user);

    void update(User user);

    User findByIdWithMedicalInfo(String id);
}