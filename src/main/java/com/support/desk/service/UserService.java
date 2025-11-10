package com.support.desk.service;

import com.support.desk.dto.UserDTO;
import com.support.desk.dto.UserRegistrationDTO;
import com.support.desk.exception.ResourceNotFoundException;
import com.support.desk.model.Role;
import com.support.desk.model.User;
import com.support.desk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO, String role) {
        validateNewUser(registrationDTO);

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setEmail(registrationDTO.getEmail());
        user.setFullName(registrationDTO.getFullName());
        user.setPhoneNumber(registrationDTO.getPhoneNumber());
        user.setAge(registrationDTO.getAge());
        user.setGender(registrationDTO.getGender());

        if (role.contains("ROLE_EMPLOYEE") || role.contains("ROLE_ADMIN")) {
            user.setEmployeeCode(registrationDTO.getEmployeeCode());
            user.setDepartment(registrationDTO.getDepartment());
        } else {
            user.setEmployeeCode("NA");
            user.setDepartment("NA");
        }

        Set<Role> roles = new HashSet<>();
        Role updatedRoles = new Role();
        updatedRoles.setRoleName(role);
        roles.add(updatedRoles);
        user.setRoles(roles);

        logger.info("Creating user: {}", user.getEmail());
        return userRepository.save(user);
    }

    private void validateNewUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            logger.warn("Username is already taken: {}", registrationDTO.getUsername());
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            logger.warn("Email is already in use: {}", registrationDTO.getEmail());
            throw new RuntimeException("Email is already in use!");
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    logger.info("Retrieved user by username: {}", username);
                    return user;
                })
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
    }

    public List<UserDTO> getAllEmployees() {
//        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
//                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//
//        return userRepository.findAll().stream()
//                .filter(user -> user.getRoles().contains(employeeRole))
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
        return null;
    }

    public List<UserDTO> getEmployeesByDepartment(String department) {

//        return userRepository.findAll();
        return null;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmployeeCode(user.getEmployeeCode());
        dto.setDepartment(user.getDepartment());

        return dto;
    }
}