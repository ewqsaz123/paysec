# paysec

## 실행 시 참고사항
- DB관련 설정 
-- MySQL 8.0.29 버전을 사용하였습니다.
-- DB연결 관련 설정파일은 총 2개의 파일입니다. username, password, url 설정은 로컬 환경에 맞춰 수정해주시기 바랍니다.

--- kakaopysecTest/src/main/resources/META-INF/persistence.xml
![image](https://user-images.githubusercontent.com/20436113/192613621-20e564a7-f0be-48cb-8ef7-ac1749ad503a.png)

--- kakaopysecTest/src/main/resources/application.properties
![image](https://user-images.githubusercontent.com/20436113/192613947-457c64fc-8153-43ac-85ff-1d52b5e0be22.png)



## 시스템 구성
![system_diagram](https://user-images.githubusercontent.com/20436113/192609113-100d1a42-d9f2-4e97-8909-5bb2baeb1176.png)

- Spring Web을 이용하여 MVC 구조의 웹 애플리케이션을 구현하였습니다.
- DB Mapping에 대한 편의성과 생산성을 위해 ORM(Object-Relational Mapping)기술인 JPA를 적용하였고, 각 주제별 상위 데이터를 조회 시 여러개의 Join과정이 포함되고 복잡한 쿼리 작성이 요구되는 API가 있기 때문에 해당 문제를 해결하기 위하여 QueryDSL 을 적용하였습니다.
- 다량의 트래픽 발생에 대비하기 위해 Spring의 로컬 캐싱 기능을 사용하여 Service 컴포넌트의 메소드 부분에 캐싱 기능을 적용하였고, 페이지가 동일한 API 요청 시에는 DB가 아닌 캐시에 저장된 쿼리결과를 재사용하도록 하여 빈번한 DB 접근 시 발생하는 오버헤드를 줄였습니다.
- 각 테이블 간의 연관성을 고려하여 관계형 DB인 MySQL을 사용하였습니다.


## DB 설계
![database_diagram](https://user-images.githubusercontent.com/20436113/192611180-9da12c68-6820-4ad3-b808-0da644391342.png)

- 주식 종목에 대한 기본정보를 저장하는 stock_info, 가격정보를 저장하는 stock_price, 거래량 및 거래가격을 저장하는 stock_trade, 종목에 대한 조회수를 저장하는 stock_hit로 총 5개의 테이블을 추가하였습니다.


## 구현
- Application 구동 시 DB 적재
-- 1. stock_info 테이블 적재 : 제공받은 SampleData를 기반으로 작성된 엑셀파일을 프로젝트 디렉토리에 추가한 후 Application 구동 시 해당 엑셀파일을 읽어와 stock_info(종목정보) 테이블에 저장
-- [엑셀파일을 읽어 stock_info 엔티티 클래스로 반환 : ExcelPOIHelper.java]
![image](https://user-images.githubusercontent.com/20436113/192616095-dba83443-39e3-455b-85cf-4f0851287d9d.png)

-- 2. 일일 거래가 랜덤값 생성 후 테이블에 적재 :



## 단위테스트





