import React, { useEffect, useState } from "react";
import style from "./style/FllySellerDetail.module.css";
import Image from "next/image";
import RequestDetail from "./fllySellerDetailComponent/RequestDetail";
import { useParams } from "next/navigation";
import { ToastErrorMessage } from "@/model/toastMessageJHM";
import { useRouter } from "next/router";
import { tokenHttp } from "@/api/tokenHttp";
import { useRecoilValue } from "recoil";
import { MemberInfo, memberInfoState } from "@/recoil/memberInfoRecoil";

interface flowerInfoType {
  flowerName: string;
  meaning: string;
}

interface fllyReqeustDeatilType {
  fllyId: number;
  flower1: flowerInfoType | null;
  flower2: flowerInfoType | null;
  flower3: flowerInfoType | null;
  imageUrl: string;
  orderType: string;
  progress: string;
  requestAddress: string;
  requestContent: string;
  situation: string;
  target: string;
  budget: number;
  color1: string | null;
  color2: string | null;
  color3: string | null;
  deadline: string;
  consumer: string;
}

const FllySellerDetail = () => {
  const [fllyRequestInfo, setFllyRequestInfo] = useState<fllyReqeustDeatilType>();
  const fllyId = useParams();
  const router = useRouter();
  const memberInfo = useRecoilValue<MemberInfo>(memberInfoState);

  useEffect(() => {
    tokenHttp
      .get("/seller/request/" + fllyId.fllyId)
      .then((res) => {
        const rsData = res.data;
        if (rsData.code == 200) {
          setFllyRequestInfo(rsData.data);
        } else {
          ToastErrorMessage(rsData.message);
        }
        if (res.headers.authorization) {
          localStorage.setItem("accessToken", res.headers.authorization);
        }
      })
      .catch((err) => {
        if (err.response.status === 403) {
          router.push("/fllylogin");
        }
      });
    /* eslint-disable-next-line */
  }, []);

  const pageMoveHandelr = () => {
    if (fllyRequestInfo) {
      router.push(
        {
          pathname: "/flly/create/[fllyId]",
          query: { fllyId: fllyRequestInfo.fllyId },
        },
        "/flly/create", // 이것은 브라우저 주소창에 표시될 URL입니다.
        { shallow: true },
      );
    } else {
      ToastErrorMessage("잠시후 다시 눌러주세요!");
    }
  };

  return (
    <>
      <div className={style.detailBack}>
        <div
          className={
            memberInfo.role === "SELLER" ? style.detailSellerHeader : style.detailBuyerHeader
          }
          style={{ backgroundImage: `url(${fllyRequestInfo?.imageUrl})` }}
        >
          <Image
            src="/img/btn/left-btn.png"
            alt="뒤로가기"
            width={15}
            height={25}
            className={style.backImg}
            onClick={() => {
              router.back();
            }}
          />
          {memberInfo.role === "SELLER" && (
            <Image
              className={style.partBtn}
              src="/img/btn/participate-btn2.png"
              alt="참여하기"
              width={80}
              height={100}
              onClick={pageMoveHandelr}
            />
          )}
        </div>
        <div className={style.detailMain}>
          {fllyRequestInfo && <RequestDetail $fllyRequestInfo={fllyRequestInfo} />}
        </div>
      </div>
    </>
  );
};

export default FllySellerDetail;
