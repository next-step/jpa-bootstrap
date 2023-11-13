package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "entity_name")
public class SampleOneWithValidAnnotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name", length = 200)
    String name;
    @Column(name = "old")
    Integer age;

    public SampleOneWithValidAnnotation() {
    }

    public SampleOneWithValidAnnotation(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public SampleOneWithValidAnnotation(long id, String nickName, int age) {
        this.id = id;
        this.name = nickName;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SampleOneWithValidAnnotation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
