package entity;

import jakarta.persistence.*;

@Table(name = "entity_with_Integer_id")
@Entity
public class SampleEntityWithIntegerId {
    @Id
    @Column(name = "entity_with_integer_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @Column(name = "name", length = 200)
    String name;
    @Column(name = "age")
    Integer age;

    public SampleEntityWithIntegerId(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public SampleEntityWithIntegerId(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public SampleEntityWithIntegerId() {

    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "EntityWithIntegerId{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
