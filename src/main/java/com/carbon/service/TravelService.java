package com.carbon.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;
import com.carbon.repository.UserRepository;
import com.carbon.repository.EvidenceRepository;
import com.carbon.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

/**
 * Service layer for sustainable travel tracking.
 * Calculates and awards points based on distance traveled using eco-friendly transport.
 */
@Service
public class TravelService {

    private record TimeWindow(LocalDateTime start, LocalDateTime end) {}

    private static final String TRAVEL_TASK_PREFIX = "Travel Journey:";
    private static final double MAX_DISTANCE_WITHOUT_EVIDENCE = 20.0;
    // Repository for accessing and updating user data
    private final UserRepository userRepository;
    private final EvidenceRepository evidenceRepository;
    // Badge logic service
    private final BadgeService badgeService;
    // Points awarded per kilometer traveled
    private static final int POINTS_PER_KM = 5;
    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp"
    );

    // Constructor injection for UserRepository
    public TravelService(UserRepository userRepository, EvidenceRepository evidenceRepository, BadgeService badgeService) {
        this.userRepository = userRepository;
        this.evidenceRepository = evidenceRepository;
        this.badgeService = badgeService;
    }
    

    /**
     * Registers a sustainable travel journey and awards points to the user.
     * @param username - Username of the user submitting travel
     * @param travelType - Type of travel
     * @param distance - Distance traveled in kilometers
     * @return Map containing calculation details: travelType, distance, pointsPerKm, pointsEarned, totalPoints
     * @throws UsernameNotFoundException if user doesn't exist
     * 
     * Calculation:
     * - Points earned = distance (km) × 5 points/km
     * - Rounded down to nearest integer
     */
    public Map<String, Object> registerTravel(String username, String travelType, double distance, MultipartFile photo) throws IOException {
        // Validate user exists
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0.");
        }

        LocalDateTime now = LocalDateTime.now();
        TimeWindow todayWindow = getTodayWindow();
        List<EvidenceStatus> existingStatuses = evidenceRepository.findTravelStatusesInWindow(
            user.getId(),
            todayWindow.start(),
            todayWindow.end(),
            TRAVEL_TASK_PREFIX + "%"
        );
        if (!existingStatuses.isEmpty()) {
            throw new IllegalStateException(getRepeatMessage(existingStatuses.get(0)));
        }

        // Calculate points
        int pointsEarned = (int)(POINTS_PER_KM * distance);

        boolean requiresModeratorApproval = distance > MAX_DISTANCE_WITHOUT_EVIDENCE;
        if (requiresModeratorApproval) {
            validateEvidencePhoto(photo);
        }

        Evidence evidence = new Evidence();
        evidence.setUser(user);
        evidence.setChallenge(null);
        evidence.setTaskTitle(String.format("%s %s %.1fkm", TRAVEL_TASK_PREFIX, travelType, distance));
        evidence.setSubmittedAt(now);

        if (requiresModeratorApproval) {
            byte[] imageBytes = photo.getBytes();
            validateImageFile(imageBytes);
            evidence.setOriginalFilename(photo.getOriginalFilename() == null ? "travel-evidence" : photo.getOriginalFilename());
            evidence.setContentType(photo.getContentType());
            evidence.setSizeBytes(photo.getSize());
            evidence.setPhoto(imageBytes);
            evidence.setStatus(EvidenceStatus.PENDING);
        } else {
            evidence.setOriginalFilename("");
            evidence.setContentType("application/octet-stream");
            evidence.setSizeBytes(0);
            evidence.setPhoto(new byte[0]);
            evidence.setStatus(EvidenceStatus.ACCEPTED);

            user.setPoints(user.getPoints() + pointsEarned);
            userRepository.save(user);
            badgeService.evaluateAllBadgeMechanisms(user.getId());
        }

        evidenceRepository.save(evidence);
        
        // Build result map with calculation details for display
        Map<String, Object> result = new HashMap<>();
        result.put("travelType", travelType);
        result.put("distance", distance);
        result.put("pointsPerKm", POINTS_PER_KM);
        result.put("pointsEarned", pointsEarned);
        result.put("requiresModeratorApproval", requiresModeratorApproval);
        result.put("status", requiresModeratorApproval ? "PENDING" : "ACCEPTED");
        result.put("totalPoints", user.getPoints());
        
        return result;
    }

    private TimeWindow getTodayWindow() {
        LocalDateTime dayStart = LocalDate.now().atStartOfDay();
        return new TimeWindow(dayStart, dayStart.plusDays(1));
    }

    private String getRepeatMessage(EvidenceStatus status) {
        if (status == EvidenceStatus.PENDING) {
            return "You have already submitted a journey today and it is pending moderator review.";
        }
        return "You have already submitted a journey today. You can submit again tomorrow.";
    }

    private void validateEvidencePhoto(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Evidence photo is required for journeys above 20km.");
        }
        String contentType = photo.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only image uploads are allowed.");
        }
    }

    private void validateImageFile(byte[] uploadedBytes) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(uploadedBytes));
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Please submit a valid image file.");
        }
    }
}