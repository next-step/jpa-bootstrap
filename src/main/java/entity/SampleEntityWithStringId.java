package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "entity_with_string_id")
@Entity
public class SampleEntityWithStringId {
    @Id
    String id;
    @Column(name = "name", length = 200)
    String name;
    @Column(name = "age")
    Integer age;

    public SampleEntityWithStringId() {
    }

    public SampleEntityWithStringId(String id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
