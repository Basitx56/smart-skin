package service;

import model.Appointment;
import model.Review;
import repository.AppointmentRepository;
import repository.ReviewRepository;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Handles expert review and rating logic.
 * GRASP Information Expert: calculates expert average
 */
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final AppointmentRepository appointmentRepo;

    public ReviewService() {
        this.reviewRepo = new ReviewRepository();
        this.userRepo = new UserRepository();
        this.appointmentRepo = new AppointmentRepository();
    }

    /**
     * Submit review for completed appointment.
     */
    public Review submitReview(String userID,
                               String expertID, String appointmentID,
                               int rating, String comment) {
        Appointment appointment = appointmentRepo.findByID(appointmentID);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }
        if (!"completed".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalArgumentException("Appointment is not completed");
        }
        if (!appointment.getUserID().equals(userID) || !appointment.getExpertID().equals(expertID)) {
            throw new IllegalArgumentException("Appointment does not belong to provided user/expert");
        }
        if (reviewRepo.existsForAppointment(appointmentID)) {
            throw new IllegalStateException("Duplicate review for this appointment is not allowed");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = new Review(
            UUID.randomUUID().toString(),
            userID,
            expertID,
            appointmentID,
            rating,
            comment,
            LocalDateTime.now()
        );

        if (!reviewRepo.save(review)) {
            throw new IllegalStateException("Failed to save review");
        }

        updateExpertRating(expertID);
        return review;
    }

    /**
     * Recalculate and update expert average rating.
     * GRASP Information Expert: ReviewService knows
     * how to calculate averages from all reviews.
     */
    private void updateExpertRating(String expertID) {
        List<Review> reviews = reviewRepo.findByExpert(expertID);
        if (reviews.isEmpty()) {
            userRepo.updateExpertRating(expertID, 0.0);
            return;
        }

        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        double avg = sum / reviews.size();
        userRepo.updateExpertRating(expertID, avg);
    }

    public List<Review> getExpertReviews(String expertID) {
        return reviewRepo.findByExpert(expertID);
    }

    public Review getReviewByAppointment(String appointmentID) {
        return reviewRepo.findByAppointment(appointmentID);
    }
}
