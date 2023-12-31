import { useRouter } from "next/router";
import React from "react";
import style from "./ShopInfoMain.module.css";
import ShopLocation from "@/components/list/listBuyer/shopInfo/ShopLocation";
import ShopImg from "@/components/list/listBuyer/shopInfo/ShopImg";
import ShopReview from "@/components/list/listBuyer/shopInfo/ShopReview";
import { GetServerSideProps } from "next";
import { useQuery } from "react-query";
import { AxiosError } from "axios";
import { tokenHttp } from "@/api/tokenHttp";
import Image from "next/image";
import { ToastErrorMessage } from "@/model/toastMessageJHM";

const ShopInfoMain = () => {
  const router = useRouter();

  const { data } = useQuery<shopInfo, AxiosError>(
    ["ShopInfoMainQuery"],
    async () => {
      const res = await tokenHttp.get(`/flly/store/${router.query.shopId}`);
      return res.data.data;
    },
    {
      onError: (error) => {
        if (error?.response?.status === 403) {
          ToastErrorMessage("로그인 만료되어 로그인 화면으로 이동합니다.");
          router.push("/fllylogin");
        }
      },
      retry: false,
    },
  );

  return (
    <>
      <div className={style.ShopInfoBack}>
        <div className={style.ShopInfoHeader}>
          <div className={style.headerTitle}>
            <Image
              src="/img/btn/left-btn.png"
              alt="뒤로가기"
              width={13}
              height={20}
              onClick={() => {
                router.back();
              }}
            />
            <div>가게정보</div>
          </div>
        </div>
        <div className={style.ShopInfoMain}>
          {data && (
            <>
              <ShopLocation ShopInfoDetail={data?.store} />
              <ShopImg shopImg={data?.store.images} />
              <ShopReview review={data?.review.content} />
            </>
          )}
        </div>
      </div>
    </>
  );
};

export default ShopInfoMain;

export const getServerSideProps: GetServerSideProps = async (context: any) => {
  // context.params를 통해 URL 파라미터에 접근할 수 있습니다.

  const { shopId } = context.params;

  // 필요한 데이터를 props로 페이지에 전달할 수 있습니다.
  return {
    props: { shopId },
  };
};
