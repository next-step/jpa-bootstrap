package app.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "department")
public class TestDepartment {
    @Id
    @GeneratedValue
    Long id;

    @OneToMany
    @JoinColumn(name = "departure_id")
    List<TestEmployee> employees;
}
