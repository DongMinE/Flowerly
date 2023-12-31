package com.ssafy.flowerly.entity;

import com.ssafy.flowerly.chatting.dto.ChattingDto;
import com.ssafy.flowerly.chatting.service.ChattingService;
import com.ssafy.flowerly.entity.common.BaseCreatedTimeEntity;
import com.ssafy.flowerly.entity.common.BaseTimeEntity;
import com.ssafy.flowerly.entity.type.ChattingType;
import com.ssafy.flowerly.member.MemberRole;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate //update시, 실제 값이 변경되는 컬럼만 update 쿼리로 생성
@Builder
@ToString
public class Chatting extends BaseCreatedTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chattingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flly_participation_id")
    private FllyParticipation fllyParticipation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flly_id")
    private Flly flly;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_id")
    private Member consumer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Member seller;

    private String lastChattingMessage;
    private LocalDateTime lastChattingTime;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isRemovedConsumer;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isRemovedSeller;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer unreadCntConsumer;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer unreadCntSeller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'NORMAL'")
    private ChattingType chattingStatus;  // 채팅 상태 추가-NORMAL, CANCELED, COMPLETED

    public void updateChatting(String chattingMessage, Date chattingTime) {
        ZonedDateTime zdt = chattingTime.toInstant().atZone(ZoneId.of("Asia/Seoul"));

        this.lastChattingTime = zdt.toLocalDateTime();
        this.lastChattingMessage = chattingMessage;
    }

    public void deleteChatting(MemberRole role) {
        if(role.equals(MemberRole.USER)) this.isRemovedConsumer = true;
        else if(role.equals(MemberRole.SELLER)) this.isRemovedSeller = true;
    }

    public void readChatting(Member member) {
        if(member.getRole().equals(MemberRole.USER)) {
            this.unreadCntConsumer = 0;
        } else if(member.getRole().equals(MemberRole.SELLER)) {
            this.unreadCntSeller = 0;
        }
    }

    public void updateUnreadCntConsumer() {
        this.unreadCntConsumer++;
    }

    public void updateUnreadSeller() {
        this.unreadCntSeller++;
    }

    public void updateChattingStatus(ChattingType state){
        this.chattingStatus = state;
    }

    public Chatting toEntity(Flly flly, FllyParticipation fllyParticipation, Member consumer, Member seller) {
        return Chatting.builder()
                .flly(flly)
                .fllyParticipation(fllyParticipation)
                .consumer(consumer)
                .seller(seller)
                .isRemovedConsumer(false)
                .isRemovedSeller(false)
                .unreadCntConsumer(0)
                .unreadCntSeller(0)
                .chattingStatus(ChattingType.NORMAL)
                .build();
    }

    public void updateStatus(ChattingType chattingType) {
        this.chattingStatus = chattingType;
    }
}
