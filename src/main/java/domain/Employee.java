package domain;

import jakarta.persistence.*;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JoinColumn(name = "department_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Long departmentId;

    protected Employee() {}

    public Employee(String name, Long departmentId) {
        this.name = name;
        this.departmentId = departmentId;
    }
}
