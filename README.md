
## 실행 시 참고사항
- DB관련 설정 
  - MySQL 8.0.29 버전을 사용하였습니다.
  - DB연결 관련 설정파일은 총 2개의 파일입니다. username, password, url 설정은 로컬 환경에 맞춰 수정해주시기 바랍니다.

    - kakaopysecTest/src/main/resources/META-INF/persistence.xml
      ![image](https://user-images.githubusercontent.com/20436113/192613621-20e564a7-f0be-48cb-8ef7-ac1749ad503a.png)

    - kakaopysecTest/src/main/resources/application.properties
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
1. Application 구동 시 DB 적재
  - 엑셀파일을 읽어 stock_info 테이블 적재 : 제공받은 SampleData를 기반으로 작성된 엑셀파일을 프로젝트 디렉토리에 추가한 후 Application 구동 시 해당 엑셀파일을 읽어와 stock_info(종목정보) 테이블에 저장
   
    [엑셀파일을 읽어 stock_info 엔티티 클래스로 반환 : ExcelPOIHelper.java]
    ![image](https://user-images.githubusercontent.com/20436113/192616095-dba83443-39e3-455b-85cf-4f0851287d9d.png)

  - 일일 거래가 랜덤값 생성 후 테이블에 적재 : 어제 거래가를 난수로 생성한 후 해당 거래가를 기준으로 증감률 30%이하로 제한하여 오늘 거래가 랜덤값을 생성한 후 stock_price(종목가격) 테이블에 저장
 
    [StockService.java]
    
    ![image](https://user-images.githubusercontent.com/20436113/192618351-d3eaf13e-331a-458f-8bfe-03e5aa11e28d.png)

  - 일일 거래량 랜덤값 생성 후 테이블에 적재 : 종목별 오늘 거래가를 조회한 후 생성할 row개수를 난수를 생성한 후 row별 거래량 난수를 생성 후 stock_trade(종목거래) 테이블에 저장
  
    [StockService.java]
    ![image](https://user-images.githubusercontent.com/20436113/192619248-61667fd5-24af-4680-8476-57a5d526f938.png)

  - 일일 조회수 랜덤값 생성 후 테이블에 적재 : 종목별 코드 조회 후 조회수를 난수로 생성하여 stock_hit(종목조회) 테이블에 저장
  
    [StockService.java]
   
    ![image](https://user-images.githubusercontent.com/20436113/192619692-11f5e278-d9cc-4439-8fa8-cfcc4ca0e84d.png)

2. 모든 주제의 상위 5건 조회 API
  - url : localhost:8080/top5 , method: get
  - 구현 방식
    - 각 주제별로 조회 쿼리를 분리하였고, size=5인 페이징을 사용하여 각 주제별 5개의 데이터만 조회하여 리턴
    - 각 주제별 조회 쿼리는 "주제별 조회 API" 에 사용한 쿼리를 재사용
    - 서비스 메소드에 대해 캐시 적용
   
     [StockController.java]
     
     ![image](https://user-images.githubusercontent.com/20436113/192621415-d52ade03-387e-454d-a255-4c61b426a582.png)

     [StockService.java]
     
     ![image](https://user-images.githubusercontent.com/20436113/192621532-abd97c44-7bcc-4ae3-8d40-0cbff6fda35a.png)

     [StockListRepository.java]
     
     ![image](https://user-images.githubusercontent.com/20436113/192621688-451ab518-d9d1-44b4-b14d-266529ade480.png)



3. 주제별 조회 API
  - "많이 본" (URL : localhost:8080/top100/hit , method: GET)
  - "많이 오른" (URL : localhost:8080/top100/increase , method: GET)
  - "많이 내린" (URL : localhost:8080/top100/decrease , method: GET)
  - "거래량 많은" (URL : localhost:8080/top100/trade , method: GET)
  
  - 구현 방식
    - JPA의 Pageable을 이용하여 페이징 적용
    - repository의 메소드 호출 시 파라미터로 (limit=100)을 전달하여 전체 데이터를 100개로 제한
    - 각 서비스 메소드에 대해 캐시 적용

      [StockController.java]
      ![image](https://user-images.githubusercontent.com/20436113/192623030-f123d851-996b-4592-aba9-60278eb55014.png)

      [StockService.java]
      ![image](https://user-images.githubusercontent.com/20436113/192623159-00248b43-eb45-4ae6-83cf-045194864ca4.png)


- 순위를 랜덤하게 변경할 수 있는 API
  - URL : localhost:8080/changeRank , method: POST
  - 구현 방식
    - 종목별로 1번의 랜덤값 생성 방식과 동일한 방식으로 거래가, 거래량, 조회수에 대해 난수를 생성한 후에 각각의 테이블에 update
    - 데이터가 변경되었으므로 캐시에 저장된 내용 모두 삭제
    
      [StockController.java]
      
      ![image](https://user-images.githubusercontent.com/20436113/192625745-eca9662a-0ee0-486c-b411-8aa689d0f016.png)

      [StockService.java]
      
      ![image](https://user-images.githubusercontent.com/20436113/192625841-edc274b2-61e5-4c64-b342-9ea6f6716a1b.png)


## 단위테스트





