package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "two")
public class SampleTwoWithValidAnnotation {
    @Id
    @Column(name = "two_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "nick_name", length = 200, nullable = false)
    String name;
    @Column
    Long age;

    public SampleTwoWithValidAnnotation() {

    }

    public SampleTwoWithValidAnnotation(long id, String nickName, Long age) {
        this.id = id;
        this.name = nickName;
        this.age = age;
    }

    @Override
    public String toString() {
        return "SampleTwoWithValidAnnotation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
