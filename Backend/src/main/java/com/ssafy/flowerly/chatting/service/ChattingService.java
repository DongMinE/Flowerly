package com.ssafy.flowerly.chatting.service;

import com.ssafy.flowerly.chatting.dto.*;
import com.ssafy.flowerly.chatting.repository.RequestDeliveryInfoRepository;
import com.ssafy.flowerly.entity.*;
import com.ssafy.flowerly.chatting.repository.ChattingMessageRepository;
import com.ssafy.flowerly.chatting.repository.ChattingRepository;
import com.ssafy.flowerly.entity.type.OrderType;
import com.ssafy.flowerly.exception.CustomException;
import com.ssafy.flowerly.exception.ErrorCode;
import com.ssafy.flowerly.member.MemberRole;
import com.ssafy.flowerly.member.model.MemberRepository;
import com.ssafy.flowerly.member.model.StoreInfoRepository;
import com.ssafy.flowerly.seller.model.FllyDeliveryRegionRepository;
import com.ssafy.flowerly.seller.model.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChattingService {
    private final MemberRepository memberRepository;
    private final StoreInfoRepository storeInfoRepository;
    private final ChattingRepository chattingRepository;
    private final ChattingMessageRepository chattingMessageRepository;
    private final RequestRepository requestRepository;
    private final RequestDeliveryInfoRepository requestDeliveryInfoRepository;
    private final FllyDeliveryRegionRepository fllyDeliveryRegionRepository;

    public List<ChattingDto.BasicResponse> getChattingList(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        List<Chatting> chattingList = null;
        List<ChattingDto.BasicResponse> chattingDtoList = new ArrayList<>();

        if(member.getRole().equals(MemberRole.USER)) {  // 소비자 입장인 경우
            chattingList = chattingRepository.findAllByConsumerAndIsRemovedFalse(member);

            for(Chatting chatting : chattingList) {
                Member opponent = chatting.getSeller();
                String opponentName = storeInfoRepository.findStoreName(opponent);
                ChattingDto.BasicResponse chattingDto = ChattingDto.BasicResponse.of(chatting, opponent.getMemberId(), opponentName);
                chattingDtoList.add(chattingDto);
            }
        } else if(member.getRole().equals(MemberRole.SELLER)) {  // 판매자 입장인 경우
            chattingList = chattingRepository.findAllBySellerAndIsRemovedFalse(member);

            for(Chatting chatting : chattingList) {
                Member opponent = chatting.getConsumer();
                ChattingDto.BasicResponse chattingDto = ChattingDto.BasicResponse.of(chatting, opponent.getMemberId(), opponent.getNickName());
                chattingDtoList.add(chattingDto);
            }
        } else {
            // 예외 던지기
        }

        return chattingDtoList;
    }

    public ChattingDto.RoomResponse getChattingMessageList(Long memberId, Long chattingId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();
        Chatting chatting = chattingRepository.findById(chattingId).orElseThrow();

        Long opponentMemberId = null;
        String opponentName = null;
        if(member.getRole().equals(MemberRole.USER)) {
            Member opponent = chatting.getSeller();
            opponentMemberId = opponent.getMemberId();
            opponentName = storeInfoRepository.findStoreName(opponent);
        } else if(member.getRole().equals(MemberRole.SELLER)) {
            Member opponent = chatting.getConsumer();
            opponentMemberId = opponent.getMemberId();
            opponentName = opponent.getNickName();
        }

        List<ChattingMessage> messages = chattingMessageRepository.findAllByChattingIdOrderBySendTime(chatting.getChattingId());
        List<ChattingMessageDto.Response> messageDtos = new ArrayList<>();
        for(ChattingMessage message : messages) {
            messageDtos.add(ChattingMessageDto.Response.of(message));
        }

        return new ChattingDto.RoomResponse(chattingId, opponentMemberId, opponentName, messageDtos);
    }

    @Transactional
    public void saveChattingMessage(StompChatRequest messageDto) {
        // 채팅 메세지 MongoDB 저장
        ChattingMessage message = ChattingMessage.toEntity(messageDto);
        chattingMessageRepository.save(message);

        // 채팅방 마지막 메세지 업데이트
        Chatting chatting = chattingRepository.findById(messageDto.getChattingId())
                            .orElseThrow(() -> new CustomException(ErrorCode.CHATTING_NOT_FOUND));
        chatting.updateChatting(message.getContent(), message.getSendTime());
    }

    @Transactional
    public Long saveRequestInfo(RequestFromChattingDto requestDto, Long chattingId) {
        Chatting chatting = chattingRepository.findById(chattingId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATTING_NOT_FOUND));

        Request request = Request.builder()
                .flly(chatting.getFllyParticipation().getFlly())
                .seller(chatting.getSeller())
                .orderName(requestDto.getOrdererName())
                .phoneNumber(requestDto.getPhoneNumber())
                .orderType(requestDto.getOrderType().equals("DELIVERY") ? OrderType.DELIVERY : OrderType.PICKUP)
                .deliveryPickupTime(stringToTime(requestDto.getDeliveryPickupTime()))
                .requestContent(requestDto.getRequestContent())
                .price(-1)
                .build();
        request = requestRepository.save(request);

        if(requestDto.getOrderType().equals("DELIVERY")) {
            RequestDeliveryInfo deliveryInfo = RequestDeliveryInfo.builder()
                    .request(request)
                    .recipientName(requestDto.getRecipientName())
                    .phoneNumber(requestDto.getRecipientPhoneNumber())
                    .address(requestDto.getAddress())
                    .build();
            requestDeliveryInfoRepository.save(deliveryInfo);
        }

        return request.getRequestId();
    }

    public LocalDateTime stringToTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(timeStr, formatter);

        return dateTime;
    }

    @Transactional
    public void saveRequestInfo(Long requestId, Integer price) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));
        request.setRequestPrice(price);
    }

    public FllyFromChattingDto.Participation getParticipationInfo(Long chattingId) {
        Chatting chatting = chattingRepository.findById(chattingId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATTING_NOT_FOUND));
        return FllyFromChattingDto.Participation.of(chatting.getConsumer().getNickName(), chatting.getFllyParticipation());
    }

    public FllyFromChattingDto.FllyInfo getFllyInfo(Long chattingId) {
        Chatting chatting = chattingRepository.findById(chattingId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATTING_NOT_FOUND));

        Flly flly = chatting.getFllyParticipation().getFlly();
        FllyFromChattingDto.FllyInfo fllyDto = FllyFromChattingDto.FllyInfo.of(flly);
        if(flly.getOrderType().equals(OrderType.DELIVERY)) {
            FllyDeliveryRegion deliveryRegion = fllyDeliveryRegionRepository.findByFlly(flly)
                    .orElseThrow();
            fllyDto.setAddress(deliveryRegion.getSido().getSidoName() + " " + deliveryRegion.getSigungu().getSigunguName());
        }

        return fllyDto;
    }
}
