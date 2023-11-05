import React from "react";
import style from "./PaymentInfoBox.module.css";

const PaymentInfoBox = () => {
  return (
    <>
      <div className={style.paymentInfoBack}>
        <div className={style.detailTitle}>결제 정보</div>
        <div className={style.detailInfoBox}>
          <div className={style.detailInfo}>
            <div>결제금액</div>
            <div>
              33000
              <span>원</span>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default PaymentInfoBox;
