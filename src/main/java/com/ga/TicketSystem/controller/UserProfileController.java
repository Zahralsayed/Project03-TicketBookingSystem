package com.ga.TicketSystem.controller;

import com.ga.TicketSystem.model.UserProfile;
import com.ga.TicketSystem.model.request.UserProfileUpdateRequest;
import com.ga.TicketSystem.service.UserProfileService;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

 @RestController
    @RequestMapping("api/user-profiles")
    public class UserProfileController {
        private final UserProfileService profileService;
        private final ServletContext servletContext;
        @Autowired
        public UserProfileController(UserProfileService profileService, ServletContext servletContext, ServletContext servletContext1) {
            this.profileService = profileService;
            this.servletContext = servletContext;
        }
        @PostMapping("/upload-image")
        public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, Principal principal) {
            try {
                String filename = profileService.uploadImage(principal.getName(), file);

                String fileDownloadUri = MvcUriComponentsBuilder
                        .fromMethodName(UserProfileController.class, "serveFile", filename)
                        .build().toUriString();
                return ResponseEntity.ok(Map.of(
                        "message", "Image uploaded successfully",
                        "imageUrl", fileDownloadUri
                ));
            } catch (IOException e) {
                return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
            }
        }

        @GetMapping("/images/{filename:.+}")
        public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
            Resource file = profileService.loadAsResource(filename);

            String contentType = "image/jpeg/png"; // Default
            try {
                contentType = servletContext.getMimeType(file.getFile().getAbsolutePath());
            } catch (IOException ex) {
                System.out.println("Could not determine file type.");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        }


     @PutMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public ResponseEntity<?> updateProfile(@ModelAttribute UserProfileUpdateRequest request,
                                            Principal principal) throws IOException {
         UserProfile updated = profileService.updateFullProfile(principal.getName(), request);
         return ResponseEntity.ok(updated);
     }

        @GetMapping("/all-profiles")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<UserProfile>> getAllProfiles() {
            return ResponseEntity.ok(profileService.getAllProfiles());
        }

    }



