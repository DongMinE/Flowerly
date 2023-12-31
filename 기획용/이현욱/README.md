
## 현욱

### 1. 치료안해주면 귀신될거야 - 게이미피케이션~~(근데 119, 112 전화하면 끝인 것 같기도.)~~
or 반대로 귀신을 치료해줘서 성불 시키기(픽픽같은 화면 수집욕구 - 귀여운 일러)

1. 동민이형이 찾은 의료데이터나 시나리오에 기반해 일상속 사고에 따른 응급처치 제공
2. 요즘 같이 흉흉한 시대에 사고를 당했을 때 도움을 줄 수 있겠,,?

### 주요기능

1. 응급처치 정보제공
    - 응급처치에 실패하면 귀신이 등장한다(생성형 AI를 사용해 귀여운 이미지 느낌으로)
    
    
2. 음성기반 응급치료 정보 제공
3. 일상에서 발견할 수 있는 응급처치 정보 제공

### 문제점

1. 요즘 사고가 발생해도 사회적인 이슈 때문에 도와주는 것을 꺼려하는 문화…

---

### 피드백 

#### F1. 게임성을 어디서 찾을건지 고민해보기
- 어떻게 해야 재밋게할가, 무엇이 유저들에게 재미라는 요소를 줄 수 있을까

#### F2. 너무 한정적인 컨텐츠의양 컨텐츠 확장가능성 고려해보기
- 만약 컨텐츠를 유저가 직접 생산단하면..? 관리자가 검수하는지, 유저가 자연스럽게 올리는지 알고 싶다.

#### F3. 내손안에 작은 응급실.. 과연 삶에 도움을 줄까..?
1. 응급치료 교육이라는 목적 달성을 위한 상한선은 어디?
2. 의료가 목적이라면 어느정도의 러닝커브를 가지게 구현할 것인가
3. 위기탈출 넘버원을 앱으로 옮긴 느낌... 
    - 라운드 형식으로 스테이지를 깨면서 배우는가?
    - 응급대응법을 알려주는 것은 어떤지

#### F4. 프로젝트 당위성을 어필할 요소 찾기
- 사실 과연 필요한가, 필요하다면 왜 필요한가? Why를 질문으로 5번까지 깊게 고민해보자

#### F5. 컨설턴트님 피드백(사견)
- 좋은 SW 개발자가 환경을 만들어주고 사용자들이 자체적인 생태계를 만들어 나가는게 좋은 것 같음..
- 가장 큰 문제는 **교육**vs**게임**vs**운동** 무엇을 잡고자하는지 알 수 없음..

결론 : 아이디어는 참신하고 좋지만, 방향을 잘못잡으면 어중이 떠중이가 될 가능성이 다소 잇따.


===

# 10.17 아이디어 선정 및 기능 도출
## 맞춤 꽃다발 제작 서비스

- 사용자의 개인적인 취향, 이벤트 유형 등을 기반으로 맞춤 꽃다발 디자인을 생성하고 주문할 수 있는 서비스

- 사용자의 취향(선호하는 색상, 분위기? 등) + 이벤트 유형(생일, 결혼, 기념일, 추모 등) 을 입력하면, ( + 꽃말 )
- 그에 맞는 꽃다발 디자인을 추천해준다.
    
    내부적으로 두 단계로 AI 동작
    
    1. 입력한 정보를 기반으로 그에 맞는 꽃 종류 추출
    2. 생성형 AI를 통해 해당 꽃들을 포함하는 꽃다발 이미지 생성
    
    ![Karlo - 빨간 카네이션, 파란 장미, 해바라기를 포함하는 꽃다발 생성](/uploads/d987648c98eefb71a7e1994bf827913e/image.png)
    
    Karlo - 빨간 카네이션, 파란 장미, 해바라기를 포함하는 꽃다발 생성
    
- 사용자는 꽃다발 이미지와 거기에 포함된 꽃들의 정보(이름, 꽃말 등)을 확인할 수 있다.
- 꽃다발 이미지 중 마음에 드는 이미지를 골라 
주변 꽃집에 이 디자인으로 꽃다발을 제작해달라고 주문할 수 있다.


## 개발자를 위한 취업 사이트

- 알고리즘 게임
    - 두 사용자가 알고리즘 빨리 풀기 게임을 함
    - 중간에 CS 퀴즈가 뜨고 먼저 맞추는 사람에게 베네핏이 주어짐
        
        1. 상대방의 코드를 한 줄 지울 수 있음 
        
        2. ChatGPT 찬스 (GPT가 코드 짜줌)
        

- 개발자 채용 공고를 모아볼 수 있음
- **개발자 짤을 모아볼 수 있음**


# 이벤츄(가제)

## 기능적 요구사항

- 생성 및 주문
    - 주문 시작 전 필요사항
        - 사용자는 검색할 지역의 주소를 입력한다.
        - 주소 입력 후 꽃다발 생성으로 넘어간다.
    - 꽃다발 생성하기
        - 사용자는 상황, 대상, 색상 3가지를  제시 해준 사항 중  고른다
        - 꽃다발에 사용할 꽃의 종류를 선택한다.
        - 선택된 꽃을 기준으로 생성형 AI에게 꽃다발 의뢰를 한다.
        - 마음에 들지 않는다면 꽃을 재선택하거나 선택된 꽃으로 다시 재생성 한다.
        - AI는 4가지의 추천 꽃다발을 보여준다.
        - 사용자는 4가지중 1택을 한다.
        - 선택된 꽃을 갖고 의뢰하기로 넘어간다
    - 의뢰글 남기기
        - 선택된 꽃과 함께 사용자가 역경매를 할 간단 내용과 상세 주문 요청사항 같은 내용을 기입한다.
        - 판매자는 판매하려는 꽃의 예시 상품(완제품 OR 사진)을 의뢰글에 남긴다.
    - 사용자의 꽃집과 꽃다발 선택하기
        - 사용자는 꽃집에서 제시한 꽃의 디자인, 가격, 꽃집의 위치를 고려하여 최종 선택을 한다.
        - 사용자와 꽃집을 연결해준다.
    - 사용자와 꽃집의 접촉
        - 꽃집과 연결된 사용자는 최종 사용자의 요구사항이나 꽃다발의 디테일한 수정부분, 요청사항들을 합의한다.
        - 두 사용자의 동의가 확인되면 결제창으로 넘어간다.
    - 결제하기 **(까지 가능인가?) - 이현욱하고싶어요**
        - 카카오와 연동하여 꽃집에게 금액을 전송한다.
        - 주문 완료
- 사용자
    - 마이 페이지
- 판매자
    - 매장 관리
- 최신 트렌드 꽃
    
    혹은 계절에 따른 꽃 추천
    
- 공유하기?
- 리뷰 및 신고
- 꽃과 꽃말

## 비기능적 요구사항

**[사용자 입력값]**

- 색상 종류
    - 빨강, 분홍, 주황, 노랑, 파랑, 보라, 흰색
- 이벤트 상황 종류
    - 생일, 입학식/졸업식, 기념일, 기타
- 이벤트 대상 종류
    - 연인, 친구, 부모님, 형제/자매, 동료, 기타

**[꽃다발 생성]**

- 색상, 상황, 대상에 맞는 꽃말을 가진 꽃을 DB에서 조회하여 사용자에게 보여준다. (보여주는 개수는 최대 20개)
- 꽃다발을 만들기 위해 사용자가 선택할 수 있는 꽃의 개수는 최대 3개

**[역경매]**

- 사용자 입력 주소 기준 5km 이내에 있는 꽃집만 역경매에 참여할 수 있다.
- 역경매에 올릴 때 사용자는 제한 시간을 설정할 수 있다. (1시간~2일)

## DB

[꽃 테이블] 

- 판매량이라는 가중치에 따라 선별(https://flower.at.or.kr/yfmc/)
- 꽃 이름, 색상 꽃말, 이미지 전처리 필요
    - 색상과 꽃말이 다른 꽃의 경우 별도의 컬럼으로 지정
        - EX) 파란장비 노란장미, 수국 등
- 데이터 전처리 후 생성형 한테 넘기기 (꽃말, 이미지 등)