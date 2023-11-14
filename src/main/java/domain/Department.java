package domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany
    @JoinColumn(name = "department_id")
    private List<Employee> employees;

    protected Department() {}

    public Department(String name) {
        this.name = name;
    }
}
