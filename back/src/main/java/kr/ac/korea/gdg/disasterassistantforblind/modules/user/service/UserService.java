package kr.ac.korea.gdg.disasterassistantforblind.modules.user.service;

import kr.ac.korea.gdg.disasterassistantforblind.modules.user.mapper.UserMapper;
import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userMapper.findById(id);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(), // Use ID as the username
                user.getPassword(),
                user.getEnabled(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * Register a new user with basic information only
     * Medical information will be added separately
     */
    @Transactional
    public User registerUser(User user) {
        // Check if user already exists
        if (userMapper.findById(user.getId()) != null) {
            throw new RuntimeException("Id already in use");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default values
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnabled(true);

        // Save user
        userMapper.insert(user);

        return user;
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    public User getUserById(String id) {
        return userMapper.findById(id);
    }

    public User getUserByIdWithMedicalInfo(String id) {
        return userMapper.findByIdWithMedicalInfo(id);
    }
}