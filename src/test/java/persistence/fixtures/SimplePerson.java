package persistence.fixtures;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SimplePerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public SimplePerson() {
    }

    public SimplePerson(String name) {
        this.name = name;
    }

    public SimplePerson(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
