import React, { useState } from "react";
import style from "./MypageReviewCard.module.css";
import Image from "next/image";
import { useRecoilValue } from "recoil";
import { MemberInfo, memberInfoState } from "@/recoil/memberInfoRecoil";
import { tokenHttp } from "@/api/tokenHttp";
import Router from "next/router";
import { ToastSuccessMessage } from "@/model/toastMessageJHM";

interface BaseReviewType {
  reviewId: number;
  content: string;
  createdAt: string;
}

interface BuyerReviewType extends BaseReviewType {
  requestId: number;
  storeName: string;
}

interface SellerReviewType extends BaseReviewType {
  consumerNickName: string;
}

type ReviewType = BuyerReviewType | SellerReviewType;

interface Props {
  ModalChangeHandler: () => void;
  SelectIdChangeHandler: (reviewId: number, index: number) => void;
  $requestIndex: number;
  $reviewInfo: ReviewType;
}

const MypageReviewCard = ({
  ModalChangeHandler,
  SelectIdChangeHandler,
  $requestIndex,
  $reviewInfo,
}: Props) => {
  const memberInfo = useRecoilValue<MemberInfo>(memberInfoState);

  const DeleteBtnHandler = () => {
    tokenHttp
      .delete(`review/delete/${$reviewInfo.reviewId}`)
      .then((res) => {
        if (res.data.code === 200) {
          ModalChangeHandler();
          SelectIdChangeHandler($reviewInfo.reviewId, $requestIndex);
          ToastSuccessMessage("리뷰가 삭제되었습니다.");

          if (res.headers.authorization) {
            localStorage.setItem("accessToken", res.headers.authorization);
          }
        }
      })
      .catch((err) => {
        if (err.response.status === 403) {
          Router.push("/fllylogin");
        }
      });
  };

  const isBuyerReview = "storeName" in $reviewInfo;

  return (
    <>
      <div className={style.ReviewCardBack}>
        {isBuyerReview ? (
          <div className={style.BuyerReviewCardHeader}>
            <div>
              {$reviewInfo.storeName}
              <span>
                <Image src="/img/btn/right-btn.png" width={10} height={15} alt="이동"></Image>
              </span>
            </div>
            <div className={style.BuyerReviewDelete} onClick={() => DeleteBtnHandler()}>
              삭제
            </div>
          </div>
        ) : (
          <div className={style.SellerReviewCardHeader}>
            <div>{$reviewInfo.consumerNickName}</div>
          </div>
        )}
        <div className={style.ReviewCardMain}>{$reviewInfo.content}</div>
        <div className={style.ReviewCardFooter}>
          <div>{$reviewInfo.createdAt}</div>
        </div>
      </div>
    </>
  );
};

export default MypageReviewCard;
