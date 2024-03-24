# jpa-bootstrap

## 할 일들

### 🚀 0단계 - 기본 코드 준비

### 🚀 1단계 - Metadata

- [x] 요구사항 1 - @Entity 엔터티 어노테이션이 있는 클래스만 가져오기
- [x] 요구사항 2 - scanner 로 찾은 Entity Class 정보를 통해 MetamodelImpl 에 데이터를 채워넣어보자
- [x] 요구사항 3 - Metamodel 을 활용해 리팩터링을 진행해보자

### 🚀 2단계 - SessionFactory

- [x] 요구사항 1 - EntityManagerFactory 를 만들어 보기
- [x] 요구사항 2 - EntityManagerFactory 를 적용하여 리팩터링을 해보자


- [x] 제안 주신 구조 적용
- [x] 쿼리 빌더를 Entity* 로
- [x] getRowId 정리 (metadata, PersistentClass 로)
- [x] 전역에서 사용중이던 PersistentClass.from 를 전부 metadata 에서 가져오도록 수정
- [x] 전역에서 사용중이던 MetadataImpl.INSTANCE 사용 제거
- [x] rowmapper 를 PersistentClass 에 미리 생성해둠
- [x] EntityKey 를 metadata로
