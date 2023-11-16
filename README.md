# jpa-bootstrap

## 1단계 - Metadata
### 요구사항 1 - @Entity 엔터티 어노테이션이 있는 클래스만 가져오기
- AnnotationBinder
  - 상위패키지에 있는 @Entity가 달린 클래스를 모두 scan한다.
  - 패키지에 클래스를 찾을 수 없는 경우 빈 list를 반환한다.
  - 패키지에 entity 클래스를 찾을 수 없는 경우 빈 list를 반환한다.

### 요구사항 2 - scanner 로 찾은 Entity Class 정보를 통해 MetamodelImpl 에 데이터를 채워넣어보자
- MetaModel
  - AnnotationBinder를 통해 값을 세팅할 수 있다.
    - Map<Class<?>, EntityClass> entityClassMap = new ConcurrentHashMap<>();
    - Map<Class<?>, EntityPersister<?>> entityPersisterMap;
    - Map<Class<?>, EntityLoader<?>> entityLoaderMap;
  - 특정 Class를 받아 Entity 어노테이션을 통해 만든 map의 value를 반환한다.
    - 없는 EntityClass인 경우 예외가 발생한다.

## 2단계 - SessionFactory
### 요구사항 1 - EntityManagerFactory 를 만들어 보기
- CurrentSessionContext
  - currentSession()
    - 현재 스레드에 연결된 EntityManager를 반환한다.
    - 현재 열린 EntityManager가 없는 경우 null을 반환한다.
- EntityManagerFactory
  - BasicMetaModel
    - EntityClass Map을 가지고 MetaModel을 만들 수 있도록 기초공사역할을 한다.
    - 기존 MetaModelImple에서 Map<Class<?>, EntityClass<?>> 를 가져간다
  - MetaModelImpl
    - BasicMetaModel과 jdbcTemplate을 받아 EntityManager에 주입할 MetaModel 생성
  - EntityManagerFactoryImpl
    - BasicMetaModel을 통해 새로운 EntityManager를 생성한다.
    - 이미 현재 스레드에 EntityManager를 만들었는데 생성하려하는 경우 예외가 발생한다.
    - 현재 CurrentSessionContext에 보유중인 EntityManager를 반환한다.
    - 현재 열린 EntityManager가 없는데 반환하려하는 경우 예외가 발생한다.

## 3단계 - Event
### 요구사항 1 - EventType 을 활용해 리팩터링 해보기

- EventType
  - 어떤 이벤트인지 이름과 interface Listener를 가지고 있는다.
  - 이후 EventListenerRegistry에 저장할 때 해당 interface listener를 상속받은 구현체만 저장할 수 있도록 한다.
- EventListenerRegistry
  - 모든 EventType을 받아서 각 타입에 맞는 Listener의 구현체를 받아 저장하고 있는다.
- EventListener
  - LoadEventListener
    - entityId와 해당 entity를 가져올 수 있는 Loader를 담은 LoadEvent를 받아 처리한다.
    - load된 entity를 반환한다.
  - PersistEventListener
    - entity object와 해당 object를 저장할 수 있는 persister가 담긴 PersistEvent를 받아 처리한다.
    - PersistEvent안에 있는 entity를 저장하며, 해당 entity는 id가 없는 경우 id가 담길 수 있다.
  - MergeEventListener
    - entity object와 해당 object를 업데이트할 수 있는 persister가 담긴 MergeEvent를 받아 처리한다.
  - DeleteEventListener
    - entity object와 해당 object를 제거할 수 있는 persister가 담긴 DeleteEvent를 받아 처리한다.
  - 각 Listener는 Event객체를 받아 처리하고, Event 결과를 반환하거나 리턴하지 않는다.
