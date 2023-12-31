package com.ssafy.flowerly.seller.model;

import com.ssafy.flowerly.entity.Flly;
import com.ssafy.flowerly.entity.Member;
import com.ssafy.flowerly.entity.type.ProgressType;
import com.ssafy.flowerly.mypage.dto.BuyerFllyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FllyRepository extends JpaRepository<Flly, Long> {
    Optional<Flly> findByFllyId (Long fllyId);

    @Query("SELECT f FROM Flly f " +
            "WHERE f.fllyId = :fllyId AND f.isCanceled = false ")
    Optional<Flly> findByFllyIdAndActivate (@Param("fllyId") Long fllyId);

    @Query("select f from Flly f " +
            " where f.consumer.memberId = :memberId " +
            " and f.progress not in :process " +
            " order by f.createdAt desc ")
    Page<Flly> findFllyByConsumerMemberIdNotLikeFinish(Pageable pageable, @Param("memberId") Long memberId,
                                                       @Param("process")List<ProgressType> pt);


    List<Flly> findByConsumerMemberId(Long memberId);
}
