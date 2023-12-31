import { useState, useEffect } from "react";
import style from "./style/FllyDetailModal.module.css";
import Image from "next/image";
import axios from "axios";

import { tokenHttp } from "@/api/tokenHttp";
import { useRouter } from "next/router";

type FllyDetailProps = {
  chattingId: number | undefined;
  modalHandler: Function;
};

type FllyDetail = {
  fllyDto: {
    buyerNickname: string;
    situation: string;
    target: string;
    colors: string[];
    flowers: string[];
    budget: number;
    deadline: string;
    orderType: string;
    address: string | null;
    requestContent: string;
  };
  participationDto: {
    buyerNickname: string;
    offerPrice: number;
    content: string;
    imageUrl: string;
  };
};

const FllyDetailModal: React.FC<FllyDetailProps> = ({ chattingId, modalHandler }) => {
  const router = useRouter();
  const [fllyDetail, setFllyDetail] = useState<FllyDetail>();
  useEffect(() => {
    tokenHttp
      .get(`/chatting/flly/detail/${chattingId}`)
      .then((response) => {
        if (response.data.code === 200) {
          const responseData = response.data.data;
          setFllyDetail(response.data.data);
          //요거 필수!! (엑세스 토큰 만료로 재발급 받았다면 바꿔줘!! )
          if (response.headers.authorization) {
            localStorage.setItem("accessToken", response.headers.authorization);
          }
        }
      })
      .catch((err) => {
        if (err.response.status === 403) {
          router.push("/fllylogin");
        }
      });
    /* eslint-disable-next-line */
  }, []);

  return (
    <>
      <div className={style.modalBg}>
        <div className={style.modalMain}>
          <div className={style.top}>
            <div className={style.title}>플리 정보</div>
            <div
              className={style.closeBtn}
              onClick={() => {
                modalHandler("FLLY", false);
              }}
            >
              <Image
                className={style.icon}
                src="/img/icon/close.png"
                width={17}
                height={17}
                alt="닫기 버튼"
              />
            </div>
          </div>
          <div className={style.contents}>
            <div className={style.contentItem}>
              <div className={style.subTitle}>의뢰 내용</div>
              <table className={style.wantedTb}>
                <tbody>
                  <tr>
                    <th>의뢰인</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.buyerNickname}</td>
                  </tr>
                  <tr>
                    <th>상황</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.situation}</td>
                  </tr>
                  <tr>
                    <th>대상</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.target}</td>
                  </tr>
                  <tr>
                    <th>색상</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.colors.join(", ")}</td>
                  </tr>
                  <tr>
                    <th>선택꽃</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.flowers.join(", ")}</td>
                  </tr>
                  <tr>
                    <th>예산</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.budget.toLocaleString()} 원</td>
                  </tr>
                  <tr>
                    <th>마감시간</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.deadline}</td>
                  </tr>
                  <tr>
                    <th>주문유형</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.orderType}</td>
                  </tr>
                  {fllyDetail?.fllyDto.address && (
                    <tr>
                      <th>주소</th>
                      <td>{fllyDetail && fllyDetail.fllyDto.address}</td>
                    </tr>
                  )}
                  <tr>
                    <th>요청사항</th>
                    <td>{fllyDetail && fllyDetail.fllyDto.requestContent}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div className={style.contentItem}>
              <div className={style.subTitle}>제안 내용</div>
              <div className={style.partDiv}>
                <div
                  className={style.imgDiv}
                  style={{ backgroundImage: `url(${fllyDetail?.participationDto.imageUrl})` }}
                ></div>
                <div className={style.textDiv}>
                  <div id={style.price}>
                    {fllyDetail && fllyDetail.participationDto.offerPrice.toLocaleString()} 원
                  </div>
                  <div>{fllyDetail && fllyDetail.participationDto.content}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default FllyDetailModal;
