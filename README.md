# jpa-bootstrap

## Metadata n Event

### 1단계 - Metadata
```java
public List<Class<?>> scan(String basePackage) throws IOException, ClassNotFoundException {
    List<Class<?>> classes = new ArrayList<>();
    String path = basePackage.replace(".", "/");
    File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());

    if (baseDir.exists() && baseDir.isDirectory()) {
        for (File file : baseDir.listFiles()) {
            if (file.isDirectory()) {
                classes.addAll(scan(basePackage + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + "." + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
    }
    return classes;
}
```
- 요구사항 1 - @Entity 엔터티 어노테이션이 있는 클래스만 가져오기
- [x] EntityScanner 를 통해 @Entity 가 붙은 클래스 정보 가져오기
- [x] 해당 클래스 정보 저장하기
- 요구사항 2 - scanner 로 찾은 Entity Class 정보를 통해 MetamodelImpl 에 데이터를 채워넣어보자
- [x] EntityPersisters 를 가지고 있다.
- [x] EntityLoaders 를 가지고 있다.
- [x] EntityMetadataProvider 역할을 위임받는다.
- 요구사항 3 - Metamodel 을 활용해 리팩터링을 진행해보자
- [x] 밑의 두 클래스 적용
- [x] EntityMetadataProvider 대신 Metamodel 적용
```java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    @OneToMany
    @JoinColumn(name = "department_id")
    private List<Employee> employees;
    
    // getter, setter, constructors, 등등
}

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // getter, setter, constructors, 등등
}
```

### 2단계 - SessionFactory
- 요구사항 1 - EntityManagerFactory 를 만들어 보기
```java
public class EntityManagerFactoryImpl {
    private final CurrentSessionContext currentSessionContext;
    // 메서드 및 책임을 자유롭게 추가해도 됩니다.

    public EntityManagerFactoryImpl(적절히) {
        // 구현해보기
    }
    public EntityManager openSession(적절히) {
        // 구현해보기
    }
}
```
- [x] EntityManagerFactory 를 이용해 EntityManager 를 생성할 수 있다.
- [x] EntityManagerFactory 를 이용해 생성할때 이미 생성 되어진것이 있다면 해당 객체를 반환한다.
- [x] CurrentSessionContext 를 이용해 각 Thread 마다의 생성된 EntityManager를 관리할 수 있다.

- 요구사항 2 - EntityManagerFactory 를 적용하여 리팩터링을 해보자
- [x] 기존 openSession 에서 항상 EntityManager 를 생성하는게 아닌 CurrentSessionContext 에 있는 EntityManager 를 먼저 사용할 수 있다.
- [x] EntityManager 가 할일을 마치고 close 될때 CurrentSessionContext 도 cleanup 할 수 있다.

### 3단계 - Event
- 요구사항 1 - EventType 을 활용해 리팩터링 해보기
```java
public class EventType<T> {
    public static final EventType<LoadEventListener> LOAD = create("load", LoadEventListener.class);
    ...
    
    private final String eventName;
    private final Class<T> baseListenerInterface;

    private EventType(String eventName, Class<T> baseListenerInterface) {
    	this.eventName = eventName;
    	this.baseListenerInterface = baseListenerInterface;
    }
    
    private static <T> EventType<T> create(String name, Class<T> listenerRole) {
        return new EventType<>( name, listenerRole);
    }
}
public interface LoadEventListener {
    public void onLoad(파마리터들..);
    ...
}
public class DefaultLoadEventListener implements LoadEventListener {
    public void onLoad(파마리터들..) {

    }
}
    private final Map<EventType<?>, EventListener> listeners;
```
- [ ] EntityManager 에서 바로 db 와 소통하던 부분을 Event 기반으로 전환한다.
- [x] Insert Event 관련 처리
- [x] Update Event 관련 처리
- [x] Delete Event 관련 처리
- [ ] Load Event 관련 처리
- [ ] 해당 Event 들을 하나로 그룹화 해보기
