package entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Departure {
    @Id
    @GeneratedValue
    Long id;

    @OneToMany
    @JoinColumn(name = "departure_id")
    List<Employee> employees;
}
