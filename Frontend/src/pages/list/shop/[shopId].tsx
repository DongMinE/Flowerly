import { log } from "console";
import { useParams } from "next/navigation";
import { useRouter } from "next/router";
import React from "react";
import style from "./ShopInfoMain.module.css";
import ShopLocation from "@/components/list/listBuyer/shopInfo/ShopLocation";
import ShopImg from "@/components/list/listBuyer/shopInfo/ShopImg";
import ShopReview from "@/components/list/listBuyer/shopInfo/ShopReview";

const ShopInfoMain = () => {
  const router = useRouter();
  const shopInfo = {
    shopX: 123,
    shopY: 4342,
    shopName: "아름다운 꽃가게",
    shopLoc: "대전광역시 유성구 학하서로 11 (대전광역시 유성구 덕명동 600-1)",
  };
  return (
    <div className={style.ShopInfoBack}>
      <div className={style.ShopInfoHeader}>
        <div className={style.headerTitle}>가게정보</div>
      </div>
      <div className={style.ShopInfoMain}>
        <ShopLocation shopInfo={shopInfo} />
        <ShopImg />
        <ShopReview />
      </div>
    </div>
  );
};

export default ShopInfoMain;
