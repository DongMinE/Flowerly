package com.ssafy.flowerly.seller.model;


import com.ssafy.flowerly.FCM.service.FCMService;
import com.ssafy.flowerly.address.repository.DongRepository;
import com.ssafy.flowerly.address.repository.SigunguRepository;
import com.ssafy.flowerly.chatting.repository.ChattingRepository;
import com.ssafy.flowerly.chatting.repository.RequestDeliveryInfoRepository;
import com.ssafy.flowerly.entity.*;
import com.ssafy.flowerly.entity.type.ChattingType;
import com.ssafy.flowerly.entity.type.OrderType;
import com.ssafy.flowerly.entity.type.ProgressType;
import com.ssafy.flowerly.entity.type.UploadType;
import com.ssafy.flowerly.exception.CustomException;
import com.ssafy.flowerly.exception.ErrorCode;
import com.ssafy.flowerly.member.MemberRole;
import com.ssafy.flowerly.member.model.MemberRepository;
import com.ssafy.flowerly.member.model.StoreInfoRepository;
import com.ssafy.flowerly.s3.model.S3Service;
import com.ssafy.flowerly.seller.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final FllyRepository fellyRepository;
    private final RequestRepository requestRepository;
    private final FllyParticipationRepository fllyParticipationRepository;
    private final MemberRepository memberRepository;
    private final StoreDeliveryRegionRepository storeDeliveryRegionRepository;
    private final FllyDeliveryRegionRepository fllyDeliveryRegionRepository;
    private final StoreInfoRepository storeInfoRepository;
    private final DongRepository dongRepository;
    private final SigunguRepository sigunguRepository;
    private final FllyPickupRegionRepository fllyPickupRegionRepository;
    private final RequestDeliveryInfoRepository requestDeliveryInfoRepository;
    private final S3Service s3Service;
    private final FCMService fcmService;
    private final ChattingRepository chattingRepository;



    /*
        플리 정보 조회 및 검증 ( 없거나 마감시간이 지났거나 취소되지 않은 플리 )
     */

    public Flly getFllyInfo(Long fllyId){
        Flly fllyInfo = fellyRepository.findByFllyIdAndActivate(fllyId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLLY_NOT_FOUND));
        return fllyInfo;
    }

    /*
        유저 정보 조회 및 검증 (현재 가입된 유저중 - Role이 DELETE 아닌 사람 중)
     */
    public Member getMemberInfo(Long memberId, MemberRole memberRole){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        //유저가 판매자가 아니라면?
        if(member.getRole() != memberRole){
            throw new CustomException(ErrorCode.MEMBER_NOT_SELLER);
        }
        return member;
    }

    /*
        의뢰 상세서 내용  (제안)
     */
    public FllyRequestDto getRequestLetter(Long fllyId) {
        //여기선 flly 검증 x
        FllyRequestDto fllyRequest = fellyRepository.findByFllyId(fllyId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLLY_NOT_FOUND)).toFllyRequestDto();
        //배달 일때만 주소 세팅
        if(fllyRequest.getOrderType().equals(OrderType.DELIVERY.getTitle())) {
            FllyDeliveryRegion fllyDelivery = fllyDeliveryRegionRepository
                    .findByFllyFllyId(fllyId).orElseThrow(() -> new CustomException(ErrorCode.FLLY_DELIVERYREGION_NOT_FOUND));
            fllyRequest.setRequestAddress(fllyDelivery.getSido().getSidoName() + " " + fllyDelivery.getSigungu().getSigunguName());
        }

        return fllyRequest;
    }

    /*
       의뢰 상세서 내용 ( 제안 + 의뢰 ) - 판매자
     */
    public ParticipationRequestDto getFllyRequestInfo(Long memberId, Long fllyId){

        ParticipationRequestDto result = new ParticipationRequestDto();
        //의사 상세서 제안 가져오기
        FllyRequestDto fllyRequestDto = getRequestLetter(fllyId);
        result.setFllyRequestDto(fllyRequestDto);
        //의뢰 조회 (여기선 검증을하면안되다! )
        FllyResponeDto fllyResponeDto = fllyParticipationRepository.findByFllyFllyIdAndSellerMemberId(fllyId, memberId)
                .map(FllyParticipation::toFllyResponeDto).orElseThrow();
        result.setFllyResponeDto(fllyResponeDto);

        return result;
    }
    /*
     의뢰 상세서 내용 ( 제안 + 의뢰 ) - 구매자  // 결제가 된 이후
   */
    public ParticipationRequestDto getFllyBuyerRequestInfo(Long memberId, long fllyId) {

        Flly fllyInfo = fellyRepository.findByFllyId(fllyId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLLY_NOT_FOUND));
        if(!fllyInfo.getConsumer().getMemberId().equals(memberId)){
            throw new CustomException(ErrorCode.NOT_CREATED_FLLY_MEMBER);
        }

        Request requestInfo = requestRepository.
                findByFllyFllyIdAndIsPaidTrue(fllyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        ParticipationRequestDto resultBuyerRequest = getFllyRequestInfo(requestInfo.getSeller().getMemberId(), fllyId);

        return resultBuyerRequest;
    }
    

    /*
        판매자가 해당 플리경매에 참여했는가 검증
     */
    public void checkSellerParticipationFlly(Long memberId, Long fllyId){
        fllyParticipationRepository.findByFllyFllyIdAndSellerMemberId(fllyId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_PARTICIPATE));
    }
    /*
        판매자가 발행한 주문서가 맞는지 검증
     */
    public void checkSellerRequestFlly(Long memberId, Long fllyId){
        requestRepository.findBySellerMemberIdAndFllyFllyId(memberId, fllyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_REQUEST));
    }


    /*
        채택된 주문 리스트
     */

    public Page<OrderSelectSimpleDto> getOrderSelect(Long mamberId, Pageable pageable) {

        //완료되지 않은거
        Page<OrderSelectSimpleDto> oderBySelect =
                requestRepository.findBySellerMemberIdOrderByDeliveryPickupTime(mamberId, pageable)
                        .map(OrderRequestDto::toOrderSelectSimpleDto);
        //채택된 주문이 없을경우
        if(oderBySelect.getContent().isEmpty()){
            throw new CustomException(ErrorCode.ORDERLIST_NOT_FOUND);
        }

        return oderBySelect;
    }

    /*
        채택된 주문 완료하기
     */
    @Transactional
    public String UpdateProgressType(Long mamberId, Long fllyId) {

        String responseProgress = null;
        //내가 참여한 주문서인지 확인용
        checkSellerRequestFlly(mamberId, fllyId);
        //꽃 정보 받아오기
        Flly fllyInfo = getFllyInfo(fllyId);
        Member buyer = memberRepository.findByMemberId(fllyInfo.getConsumer().getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(fllyInfo.getProgress() == ProgressType.FINISH_ORDER){
            fllyInfo.UpdateFllyProgress(ProgressType.FINISH_MAKING);
            if(fllyInfo.getOrderType().equals(OrderType.DELIVERY)) {
                try {
                    fcmService.sendPushMessage(buyer.getMemberId(), "꽃다발 제작이 완료되었습니다.", "배달이 시작될 예정입니다!");
                } catch (Exception e) {
                    log.info("제작 완료 중 알림 전송 오류");
                }
            } else {
                try {
                    fcmService.sendPushMessage(buyer.getMemberId(), "꽃다발 제작이 완료되었습니다.", "픽업 시간에 맞춰 찾아가 주세요!");
                } catch (Exception e) {
                    log.info("제작 완료 중 알림 전송 오류");
                }
            }
        }
        else if(fllyInfo.getProgress() == ProgressType.FINISH_MAKING) {
            fllyInfo.UpdateFllyProgress(ProgressType.FINISH_DELIVERY);

            //fllyId와 판매자Id롤 참여Id를 불러옴
            FllyParticipation fllyParticipation = fllyParticipationRepository
                    .findByFllyFllyIdAndSellerMemberId(fllyId, mamberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.FLLY_PARTICIPATION_NOT_FOUND));
            //참여한 id로 채팅정보 불러옴
            Chatting chattingInfo  = chattingRepository.findByFllyParticipationFllyParticipationId(fllyParticipation.getFllyParticipationId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CHATTING_NOT_FOUND));

            chattingInfo.updateChattingStatus(ChattingType.COMPLETED);

            chattingRepository.save(chattingInfo);

            if(fllyInfo.getOrderType().equals(OrderType.DELIVERY)) {
                try {
                    fcmService.sendPushMessage(buyer.getMemberId(), "꽃다발 배달이 완료되었습니다.", "꽃다발에 대한 리뷰를 작성해 주세요.");
                } catch (Exception e) {
                    log.info("배달 완료 중 알림 전송 오류");
                }
            } else {
                try {
                    fcmService.sendPushMessage(buyer.getMemberId(), "꽃다발 픽업이 완료되었습니다.", "꽃다발에 대한 리뷰를 작성해 주세요.");
                } catch (Exception e) {
                    log.info("픽업 완료 중 알림 전송 오류");
                }
            }
        }

        Flly updateInfo = fellyRepository.save(fllyInfo);
        
        return updateInfo.getProgress().getTitle();
    }

    /*
        참여한 플리
     */
    public Page<OrderParticipationDto> getParticipation(Long memberId ,Pageable pageable){
        //입찰인지 조율이지 + 경매마감시간이 안지난것만 보여져야한다(X)
        Page<OrderParticipationDto> orderParticipation =
                fllyParticipationRepository.findBySellerMemberIdParticipationDto(memberId, pageable)
                        .map(FllyParticipation::toOrderParticipationDto);

        if(orderParticipation.getContent().isEmpty()){
            throw new CustomException(ErrorCode.FLLY_PARTICIPATION_NOT_FOUND);
        }
        return orderParticipation;
    }
    
    

    /*
        플리 참여하기
     */
    @Transactional
    public void sellerFllyParticipate(Long memberId, MultipartFile file, RequestFllyParticipateDto data) {

        //flly가 있는으면서 활성화가 되어있는가 ?
        Flly fllyInfo = getFllyInfo(data.getFllyId());
        //유저가 있는가 ?
        Member member = getMemberInfo(memberId, MemberRole.SELLER);

        //이미 해당 유저가 참여한 플리라면(이미 참가하신 플리입니다)
        fllyParticipationRepository
                .findByFllyFllyIdAndSellerMemberId(data.getFllyId(), memberId)
                .ifPresent(fllyParticipation -> {
                    throw new CustomException(ErrorCode.SELLER_ALREADY_PARTICIPATE);
                });

        String imgUrl = s3Service.uploadOneImage(file, UploadType.ORDER);

        if(imgUrl.isEmpty()){
            throw new CustomException(ErrorCode.INVALID_UPLOAD_FILE);
        }

        try {

            FllyParticipation fllyParticipation = fllyParticipationRepository
                    .save(FllyParticipation.builder()
                            .flly(fllyInfo)
                            .seller(member)
                            .imageUrl(imgUrl)
                            .offerPrice(data.getOfferPrice())
                            .content(data.getContent())
                            .build());

            Member buyer = memberRepository.findByMemberId(fllyInfo.getConsumer().getMemberId())
                            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            try {
                fcmService.sendPushMessage(buyer.getMemberId(), "꽃집이 플리에 참여했습니다.", "진행중인 플리의 플리스트에서 확인해 보세요!");
            } catch (Exception e) {
                log.info("sellerFllyParticipate 알림 전송 중 에러 발생");
            }
        }catch (Exception e){
            throw new CustomException(ErrorCode.SELLER_PARTICIPATE_FAIL);
        }



    }
    
    /*
        주변 플리 정보 불러오기(빼달)
     */
    public Page<FllyNearDto> getNearFllyDeliverylist(Long memberId, Pageable pageable) {

        //유저가 있는가 ? (판매자)
        Member member = getMemberInfo(memberId, MemberRole.SELLER);

        /* 이런 방법도 가능하다!! 바로 DTO 만들어서 하기..
        List<AddressSimpleDto> storeDeliveryRegion = storeDeliveryRegionRepository.findBySellerMemberId(memberId)
                .map(regions -> regions.stream()
                        .map(StoreDeliveryRegion::toAddressSimpleDto)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_SELLER_DELIVERY_REGION));
         */

        //판매자가 배달 가능한 지역인가?
        //1. 판매자가 설정한 배달 정보를 가져와야한다.
        List<StoreDeliveryRegion> storeDeliveryRegions = storeDeliveryRegionRepository.findBySellerMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_SELLER_DELIVERY_REGION));

        //배달일경우는 전체를 한다면 (충청북도)로 찾아야하기에 세개다 비교를 해야함
        List<Sido> deliverySido = new ArrayList<>();
        List<Sigungu> deliverySigugun = new ArrayList<>();
        List<Dong> deliveryDong = new ArrayList<>();

        //시 구군 동 세팅 (동부터 돌면서 전체가 있나 없나 비교)
        for(StoreDeliveryRegion tmp : storeDeliveryRegions){
            if(tmp.getDong().getDongName().equals("전체")){
                if(tmp.getSigungu().getSigunguName().equals("전체")) deliverySido.add(tmp.getSido());
                else deliverySigugun.add(tmp.getSigungu());
            }
            else deliveryDong.add(tmp.getDong());
        }

        // 해당 배달지역으로 가지고있는 것을 Flly번호를 찾아온다.
        Page<FllyNearDto> deliveryAbleList = fllyDeliveryRegionRepository
                .getSellerDeliverAbleList(deliverySido, deliverySigugun, deliveryDong, pageable, memberId)
                .map(FllyDeliveryRegion::toDeliveryFllyNearDto);

        if(deliveryAbleList.getContent().size() <= 0){
            throw new CustomException(ErrorCode.NOT_SELLER_SEARCH_NEAR);
        }

        return deliveryAbleList;
    }

    /*
        주변 플리 정보 불러오기(픽업)
     */
    public Page<FllyNearDto> getNearFllyPickuplist(Long memberId, Pageable pageable) {

        Page<FllyNearDto> pickupAbleList = null;
        //유저가 있는가 ? (판매자)
        Member member = getMemberInfo(memberId, MemberRole.SELLER);

        //2 픽업 가능한지 찾아야한다!
        //2-1 판매자 가게의 주소
        //없다고 화면에 출력이 안되는게 아니기때문에 에러발생 X
        StoreInfo store = storeInfoRepository.findBySellerMemberId(memberId). orElse(null);

        //나의 주소를 가지고 전체 값을 찾아야한다! (시를 보내 구군의 전체를 찾고 / 시구군을 보내 동에서 전체를 찾는다 )
        if(store != null){
            Sigungu sigunguAll = sigunguRepository.findBysigunguCodeAllCode(store.getSido());

            List<Dong> dongAll = dongRepository.findByDongCodeAllCode(store.getSido());
            //픽업일경우에는 전체 + 내 주소만 보면되기때문
            List<Sigungu> pickupSigugun = new ArrayList<>();
            pickupSigugun.add(sigunguAll);
            dongAll.add(store.getDong());

            //2-2 가게의 시 군 구 와 전체 시군구 와 전체 동을 가지고 flly픽업정보에서 찾는다
//            pickupAbleList = fllyPickupRegionRepository
//                    .getSellerPickupAbleList(pickupSigugun, dongAll, pageable, memberId)
//                    .map(FllyPickupRegion::toPickupFllyNearDto);
            pickupAbleList = fllyPickupRegionRepository
                    .getSellerPickupAbleList(pickupSigugun, dongAll, pageable, memberId)
                    .map(Flly::toPickupFllyNearDto);

        }

        if(pickupAbleList == null || pickupAbleList.getContent().size() <= 0){
            throw new CustomException(ErrorCode.NOT_SELLER_SEARCH_NEAR);
        }
        return pickupAbleList;
    }


    /*
        플리 주문서 보기
     */
    public Map<String, Object> getFllyOrder(Long memberId, Long fllyId){

        Map<String, Object> result = new HashMap<>();
        FllyRequestSimpleDto fllyRequest = fellyRepository.findByFllyId(fllyId)
                .orElseThrow(() -> new CustomException(ErrorCode.FLLY_NOT_FOUND)).toFllyRequestSimpleDto();

        FllyOrderInfoDto fllyOrderInfo = requestRepository.findByFllyFllyIdAndIsPaidTrue(fllyId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_REQUEST)).toFllyOrderInfoDto();

        String responseUrl = fllyParticipationRepository.findByFllyFllyIdAndSellerMemberId(fllyId, fllyOrderInfo.getSellerId()).orElseThrow(
                () -> new CustomException(ErrorCode.FLLY_PARTICIPATION_NOT_FOUND)).getImageUrl();

        FllyDeliveryInfoDto deliveryInfo = null;

        if(fllyOrderInfo.getOrderType().equals(OrderType.DELIVERY.getTitle())) {
            deliveryInfo = requestDeliveryInfoRepository
                    .findByRequestRequestId(fllyOrderInfo.getRequestId())
                    .orElseThrow(() -> new CustomException(ErrorCode.REQUEST_DELIVERY_NOT_FOUND)).toFllyDeliveryInfoDto();
        }
        fllyOrderInfo.setResponseImgUrl(responseUrl);

        result.put("reqestInfo", fllyRequest);
        result.put("orderInfo", fllyOrderInfo);
        result.put("deliveryInfo",deliveryInfo);

        return result;
    }

    
}
