import { atom } from "recoil";
import { recoilPersist } from "recoil-persist";

export interface MemberInfo {
  id: number;
  socialId: string;
  nickName: string;
  email: string;
  role: string; // role 추가
  store?: StoreInfo | null;
  notification: boolean;
}

export interface ImageInfo {
  storeImageId: number;
  imageUrl: string;
}

export interface StoreInfo {
  storeInfoId: number;
  storeName: string;
  storeNumber: string;
  sellerName: string;
  phoneNumber: string;
  address: string;
  images: ImageInfo[];
}

export interface sellerAddressType {
  sido: string;
  sigungu: string;
  dong: string;
}

const isBrowser = typeof window !== "undefined";
const localStorage = isBrowser ? window.localStorage : undefined;

const { persistAtom } = recoilPersist({
  key: "fllyMemberInfo",
  storage: localStorage,
});

export const storeInfoState = atom<StoreInfo>({
  key: "storeInfoState",
  default: {
    storeInfoId: 0,
    storeName: "",
    storeNumber: "",
    sellerName: "",
    phoneNumber: "",
    address: "",
    images: [],
  },
  effects_UNSTABLE: [persistAtom],
});

export const memberInfoState = atom<MemberInfo>({
  key: "memberInfoState",
  default: {
    id: 0,
    socialId: "",
    nickName: "",
    email: "",
    role: "", // role 추가
    store: null,
    notification: false,
  },
  effects_UNSTABLE: [persistAtom],
});
