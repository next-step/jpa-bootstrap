package persistence.entity.context;

public interface PersistenceContext {
    <T> T getEntity(PersistentClass<T> persistentClass, Long id);

    /**
     * entity 객체 상태가 관리되기 전까지는, entity.getId() 값을 가지고 insert/update 여부를 추측한다.
     * <ul>
     * <li>entity 클래스의 @Id 필드에 GenerationType 가 정의돼 있는 경우는 id = null 인 경우 insert 로 본다.</li>
     * <li>그렇지 않은 경우에는 first level cache 에 없으면 insert 로 본다.
     *     <br>캐시에 없으면 insert 쿼리가 실행될텐데, 이때 database 에 이미 같은 id 의 row 가 있을 경우 예외가 발생하기를 기대한다.
     * </li>
     * </ul>
     */
    boolean guessEntityIsNewOrNot(Object entity);

    <T> void insertEntity(T entity);

    <T> void updateEntity(T entity);

    void removeEntity(Object entity);
}
