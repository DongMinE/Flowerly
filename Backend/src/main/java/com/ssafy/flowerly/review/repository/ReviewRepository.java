package com.ssafy.flowerly.review.repository;

import com.ssafy.flowerly.entity.Member;
import com.ssafy.flowerly.entity.Request;
import com.ssafy.flowerly.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findReviewsBySeller_MemberIdAndIsRemovedFalse(Pageable pageable, Long sellerId);

    Page<Review> findByConsumerMemberIdAndIsRemovedFalse(Pageable pageable, Long consumerId); // 구매자 리뷰 목록 조회

    Boolean existsByRequestRequestId(Long requestId);

//    Optional<Review> findByConsumerMemberIdAndReviewIdAndIsRemovedFalse(Long reviewId, Long consumerId);

    Optional<Review> findByReviewIdAndIsRemovedFalse(Long reviewId);

    Optional<Review> findByRequestAndConsumerAndIsRemovedFalse(Request request, Member consumer);
    Optional<Review> findByConsumerMemberIdAndReviewIdAndIsRemovedFalse(Long reviewId, Long memberId);
}
