# jpa-bootstrap

## 요구사항
### 1단계 - Metamodel & Metadata
- [X] scanner 로 찾은 Entity Class 정보를 통해 MetamodelImpl 에 데이터를 채워넣는다.

### 2단계 - SessionFactory
- [X] EntityManagerFactory를 이용하여 EntityManager를 초기화 한다..

### 3단계 - Event
- [X] EventListenerRegistry에 각 리스너를 저장한다.
- [X] Listener를 생성하여 ActionQueue에 동작들을 담는다.
- [X] EntityManager flush시 ActionQueue에 동작을 일괄 실행한다.

