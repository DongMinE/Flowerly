import React, { useEffect } from "react";
import style from "@/components/flly/fllyUser/FllyBouquet.module.css";
import { useState } from "react";
import { useRecoilValue, useRecoilState } from "recoil";
import { bouquetsState, bouquetState, bouquetType } from "@/recoil/fllyRecoil";
import Image from "next/image";
import CheckModal from "@/components/flly/fllyUser/CheckModal";
import { ToastErrorMessage } from "@/model/toastMessageJHM";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

const settings = {
  slide: "div",
  dots: true,
  infinite: false,
  speed: 500,
  slidesToShow: 3,
  slidesToScroll: 1,
  autoplaySpeed: 1000,
  cssEase: "linear",
  arrows: false,
};

const FllyBouquet = () => {
  const [showPrevModal, setShowPrevModal] = useState<boolean>(false);
  const [showNextModal, setShowNextModal] = useState<boolean>(false);
  const [showAgainModal, setShowAgainModal] = useState<boolean>(false);
  const [selected, setSelected] = useState<bouquetType>();
  const [cnt, setCnt] = useState<number>(2);
  const bouquets = useRecoilValue(bouquetsState);
  const [bouquet, setBouquet] = useRecoilState(bouquetState);

  const handleSelect = (e: bouquetType) => {
    setSelected(e);
    setBouquet(e);
  };

  useEffect(() => {
    setCnt(3 - bouquets.length / 2);
    if (bouquets && bouquet === null) {
      handleSelect(bouquets[0]);
    } else if (bouquet) {
      setSelected(bouquet);
    }
    /* eslint-disable-next-line */
  }, [bouquets]);

  const handleNextClick = () => {
    if (bouquet !== null) setShowNextModal(true);
    else ToastErrorMessage("항목을 선택해 주세요.");
  };

  const handlePrevClick = () => {
    if (cnt > 0) setShowPrevModal(true);
    else ToastErrorMessage("남은 생성 횟수가 없습니다.");
  };

  const handleAgainClick = () => {
    if (cnt > 0) setShowAgainModal(true);
    else ToastErrorMessage("남은 생성 횟수가 없습니다.");
  };

  const prevBtnHandler = () => {
    setShowPrevModal(!showPrevModal);
  };

  const nextBtnHandler = () => {
    setShowNextModal(!showNextModal);
  };

  const againBtnHandler = () => {
    setShowAgainModal(!showAgainModal);
  };

  return (
    <>
      <div className={style.fllyBox}>
        {showPrevModal && (
          <CheckModal
            ModalChangeHandler={prevBtnHandler}
            question={"이전의 선택을 변경하시겠습니까?"}
            explain={"꽃 선택으로 이동합니다."}
            routerHref={"flower"}
          />
        )}
        {showNextModal && (
          <CheckModal
            ModalChangeHandler={nextBtnHandler}
            question={"선택한 시안으로 의뢰하시겠습니까?"}
            explain={"주문 의뢰서로 이동합니다."}
            routerHref={"request"}
          />
        )}
        {showAgainModal && (
          <CheckModal
            ModalChangeHandler={againBtnHandler}
            question={"꽃다발을 다시 생성하시겠습니까?"}
            explain={"이전의 꽃다발 이미지는 유지됩니다."}
            routerHref={"loading"}
          />
        )}
        <div className={style.contentBox}>
          <div className={style.headerTitle}>
            <div className={style.guide}>원하는 시안을 선택해주세요.</div>
          </div>
          <div className={style.selectAreaBox}>
            <div className={style.selectedImgBox}>
              <div
                className={style.imgBoxImg}
                style={{
                  backgroundImage: `url(${selected ? selected.url : "/img/etc/loading.gif"})`,
                }}
              />
            </div>
            <Slider {...settings} className={style.selectBox}>
              {bouquets.map((item, index) => (
                <div
                  key={index}
                  onClick={() => {
                    handleSelect(item);
                  }}
                >
                  <div
                    className={
                      selected == item ? `${style.selectImg} ${style.selectedImg}` : style.selectImg
                    }
                    style={{ backgroundImage: `url(${item.url})` }}
                  >
                    {selected == item && (
                      <>
                        <div />
                        <Image
                          src="/img/icon/select-check.png"
                          width={35}
                          height={30}
                          alt="check"
                        ></Image>
                      </>
                    )}
                  </div>
                </div>
              ))}
            </Slider>
            <div onClick={handleAgainClick} className={style.againBtn}>
              <Image src="/img/icon/again.png" width={40} height={40} alt="again"></Image>
            </div>
            <div className={style.btnCount}>남은 생성 횟수 : {cnt}/3</div>
          </div>
          <div className={style.btnBox}>
            <div onClick={handlePrevClick} className={style.prevBtn}>
              &lt;
            </div>
            <div onClick={handleNextClick} className={style.nextBtn}>
              다음
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default FllyBouquet;
