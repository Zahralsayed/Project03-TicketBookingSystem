package com.ga.TicketSystem.service;


import com.ga.TicketSystem.enums.Role;
import com.ga.TicketSystem.model.User;
import com.ga.TicketSystem.model.UserProfile;
import com.ga.TicketSystem.model.request.UserProfileUpdateRequest;
import com.ga.TicketSystem.repository.UserProfileRepository;
import com.ga.TicketSystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class UserProfileService {
    private final Path root = Paths.get("src/main/profile-pics");
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage folder", e);
        }
    }

    public String uploadImage(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }
        String filename = user.getUsername() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();


        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }
        UserProfile profile = user.getProfile();
        profile.setProfileImage(filename);
        userRepository.save(user);

        return filename;
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Transactional
    public UserProfile updateFullProfile(String email, UserProfileUpdateRequest request) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        if (request.phone() != null && !request.phone().isBlank()) profile.setPhone(request.phone());
        if (request.address() != null && !request.address().isBlank()) profile.setAddress(request.address());
        if (request.dateOfBirth() != null) profile.setDateOfBirth(request.dateOfBirth());

        MultipartFile file = request.file();
        if (file != null && !file.isEmpty()) {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            if (profile.getProfileImage() != null) {
                try {
                    Path oldPath = root.resolve(profile.getProfileImage());
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    System.err.println("Could not delete old image: " + e.getMessage());
                }
            }

            String newFilename = user.getUsername() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);

            profile.setProfileImage(newFilename);
        }

        return userProfileRepository.save(profile);
    }




    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll()
                .stream()
                .filter(userProfile -> userProfile.getUser().getRole() == Role.CUSTOMER)
                .toList();
    }
}

