
#### 요구사항1 @Entity 엔터티 어노테이션이 있는 클래스만 가져오기
1. componentScanner 로 @Entity 달린 클래스다 가져오기
   Annotation Binder 에서
    - Component Scan한 클래스를로 넣어주기
    - 각 Entity에 대한 맵핑
        - Persister
        - MetaEntities
    - MetaDataImpl 객체에 해당 Persister, MetaEntities를 넣어주기


#### 요구사항2 scanner로 찾은 Entity class 정보를 통해 metamodelImpl 데이터 채워넣기
2. 가져와서 persister 만들기, metaEntity도 다 만들어두기
   MetaDataImpl
    - Persister
    - MetaEntities
    - Scanner (클래스 정보들 읽어와서 persister, entities 만들기)


#### 요구사항3 Metamodel을 활용해 리팩터링을 진행
3. EntityManager에서 해당 Impl 받아서 persister, entity 뽑아서 쓰면 된다.
    - 생성자에 추가


**객체 설계**
AnnotationBinder
- componentScanner - class scan 역할
- persister, metaEntity 만드는 역할
- 해당 값들을 MetaModelImpl에 넣어주는 역할
- hibernate 에서는 객체간 관계도 여기서 하는걸로 보임
  --> 와 이거 전 단계에서 어떻게 해야할지 고민을 많이했는데 여기있었구나

MetaModelImpl
- Class 에 따른 persister 와 metaEntity를 반환하는 역할
- persister 만드는데 ;;; connection 객체를 받는데;; 잘못만든것같다.
- persister는 connection정보 없이 구성이 되야되는데;;


Metamodel


아쉬운점:

1. hibernate 읽을때 class 기록해둘걸 그랬다. 일단 이번꺼는 조금 기록해둔다.
   https://github.com/valotas/hibernate-core/blob/master/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
1. bindClass

질문사항:
1. connection 을 어떻게 리팩토링 해볼까요?? 흠...
   entityManager, Loader, Persister, Binder도 connection을 가지고 있는데
   나중에 제거할 수 있는 기회가 오겠죠??

