import { atom } from "recoil";

export interface sellerInputType {
  storename: string;
  sellername: string;
  phonenumber: string;
  storenumber: string;
  address: string;
}

export interface storeDeliveryRegionType {
  sidoCode: number;
  sigunguCode: number;
  dongCode: number;
}

export const storeDeliveryRegionState = atom<storeDeliveryRegionType[]>({
  key: "storeDeliveryRegionState",
  default: [],
});

export const sellerDeliveryRegionState = atom<string[]>({
  key: "sellerDeliveryRegionState",
  default: [],
});

export const tempTokenState = atom({
  key: "tempTokenState",
  default: "",
});

export const buyerInputState = atom({
  key: "buyerInputState",
  default: {
    nickname: "",
  },
});

export const sellerInputState = atom<sellerInputType>({
  key: "sellerInputState",
  default: {
    storename: "",
    sellername: "",
    phonenumber: "",
    storenumber: "",
    address: "",
  },
});