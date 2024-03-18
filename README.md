# jpa-bootstrap

# 🚀 1단계 - Metadata

## 요구사항 1 - @Entity 엔터티 어노테이션이 있는 클래스만 가져오기
- [x] ComponentScanner를 통해서 Entity만 가져온다.

## 요구사항 2 - scanner 로 찾은 Entity Class 정보를 통해 MetamodelImpl 에 데이터를 채워넣어보자
- [x] MetaModelImpl에 EntityClass 정보를 주입한다.

## 요구사항 3 - Metamodel 을 활용해 리팩터링을 진행해보자
- [x] MetaModel을 통해 리팩터링을 진행한다.

# 🚀 2단계 - SessionFactory

EntityManagerFactory 역할은 Hibernate 설정 로드
매핑 정보와 메타데이터 로딩
Session 연결(EntityManager)
SessionFactory로 부터 Session을 생성하면 DB 세션을 얻게된다.
CurrentSessionContext는 Hibernate에서 현재 세션을 관리하는 데 사용되는 중요한 인터페이스입니다.
CurrentSessionContext는 현재 스레드에 연결된 세션을 추적하고 제공하는 역할을 합니다.


## 요구사항 1 - EntityManagerFactory 를 만들어 보기
- [x] EntityManagerFactory를 통해 entityManager를 생성한다.
- [x] EntityManagerFactory를 통해 entityManager를 불러온다.

## 요구사항 2 - EntityManagerFactory 를 적용하여 리팩터링을 해보자
- [x] entityManager를 생성할 때 metaModel을 주입한다.
