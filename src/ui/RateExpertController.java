package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.Review;
import model.User;
import service.ReviewService;
import util.AlertHelper;
import util.SessionManager;

import java.util.Map;

public class RateExpertController extends BaseController {
    @FXML private Label expertInfoLabel;
    @FXML private Button star1;
    @FXML private Button star2;
    @FXML private Button star3;
    @FXML private Button star4;
    @FXML private Button star5;
    @FXML private TextArea commentArea;
    @FXML private Label charCounterLabel;
    @FXML private Label alreadyReviewedLabel;
    @FXML private Button submitButton;

    private final ReviewService reviewService = new ReviewService();
    private int rating = 0;
    private String appointmentID;
    private String expertID;

    @FXML
    public void initialize() {
        commentArea.textProperty().addListener((o, ov, nv) -> {
            if (nv != null && nv.length() > 500) {
                commentArea.setText(nv.substring(0, 500));
            }
            charCounterLabel.setText((commentArea.getText() == null ? 0 : commentArea.getText().length()) + "/500");
        });
    }

    public void setData(Object data) {
        if (!(data instanceof Map)) return;
        Map<?, ?> map = (Map<?, ?>) data;
        this.appointmentID = String.valueOf(map.get("appointmentID"));
        this.expertID = String.valueOf(map.get("expertID"));
        expertInfoLabel.setText("Expert ID: " + expertID + " | Appointment: " + appointmentID);

        Review existing = reviewService.getReviewByAppointment(appointmentID);
        if (existing != null) {
            alreadyReviewedLabel.setVisible(true);
            alreadyReviewedLabel.setText("Already reviewed: " + existing.getRating() + " stars");
            submitButton.setDisable(true);
            rating = existing.getRating();
            highlightStars();
            commentArea.setText(existing.getComment());
        }
    }

    @FXML private void onStar1() { rating = 1; highlightStars(); }
    @FXML private void onStar2() { rating = 2; highlightStars(); }
    @FXML private void onStar3() { rating = 3; highlightStars(); }
    @FXML private void onStar4() { rating = 4; highlightStars(); }
    @FXML private void onStar5() { rating = 5; highlightStars(); }

    @FXML
    private void onSubmit() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;
        String comment = commentArea.getText() == null ? "" : commentArea.getText().trim();
        if (rating < 1 || comment.isEmpty()) {
            AlertHelper.showError("Select rating and write a comment");
            return;
        }

        try {
            reviewService.submitReview(user.getUserID(), expertID, appointmentID, rating, comment);
            AlertHelper.showSuccess("Thank you for your review!");
            submitButton.setDisable(true);
        } catch (Exception ex) {
            AlertHelper.showError(ex.getMessage());
        }
    }

    private void highlightStars() {
        styleStar(star1, rating >= 1);
        styleStar(star2, rating >= 2);
        styleStar(star3, rating >= 3);
        styleStar(star4, rating >= 4);
        styleStar(star5, rating >= 5);
    }

    private void styleStar(Button b, boolean active) {
        b.setStyle(active ? "-fx-background-color:#FF9800; -fx-text-fill:white;" : "");
    }
}
