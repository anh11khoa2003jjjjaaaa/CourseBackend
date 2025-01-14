package org.example.sellingcourese.Service;

import org.example.sellingcourese.Model.Review;
import org.example.sellingcourese.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // Lấy tất cả các đánh giá
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    // Lấy đánh giá theo người dùng
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
    //Lấy đánh giá theo khóa học
    public List<Review> getReviewsByCourseId(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }
    // Tạo mới một đánh giá
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    // Cập nhật đánh giá theo ID
    public Review updateReview(Long id, Review reviewDetails) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            if (reviewDetails.getRating() != null) {
                review.setRating(reviewDetails.getRating());
            }
            if (reviewDetails.getComment() != null) {
                review.setComment(reviewDetails.getComment());
            }
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("Review not found with ID: " + id);
        }
    }

    // Xóa đánh giá theo ID
    public void deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new RuntimeException("Review not found with ID: " + id);
        }
    }
}
