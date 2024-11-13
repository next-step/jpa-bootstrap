# jpa-bootstrap

## 프로그래밍 요구 사항

- 모든 로직에 단위 테스트를 구현한다. 단, 테스트하기 어려운 UI 로직은 제외
- 자바 코드 컨벤션을 지키면서 프로그래밍한다.
- 객체지향설계의 규칙을 다 지켜본다.

## 과제 진행 요구 사항

- 기능을 구현하기 전 `README.md`에 구현할 기능 목록을 정리해 추가한다.
- Git의 커밋 단위는 앞 단계에서 `README.md`에 정리한 기능 목록 단위로 추가한다.

## 프로그래밍 요구 사항

### 🚀 1단계 - Metadata

- [x] 요구사항 1 - @Entity 엔터티 어노테이션이 있는 클래스만 가져오기
- [x] 요구사항 2 - scanner 로 찾은 Entity Class 정보를 통해 MetamodelImpl 에 데이터를 채워넣어보자
- [x] 요구사항 3 - Metamodel 을 활용해 리팩터링을 진행해보자

### 🚀 2단계 - SessionFactory

- [x] 요구 사항 1 - EntityManagerFactory 를 만들어 보기
- [x] 요구 사항 2 - EntityManagerFactory 를 적용하여 리팩터링을 해보자

### 🚀 3단계 - Event

- [ ] 요구 사항 1 - EventType 을 활용해 리팩터링 해보기
- [ ] 요구 사항 2 - Queue 를 활용해 데이터베이스 반영 시 최적화 해보기
