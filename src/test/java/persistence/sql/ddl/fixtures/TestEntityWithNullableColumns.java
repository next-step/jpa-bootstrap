package persistence.sql.ddl.fixtures;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TestEntityWithNullableColumns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nullableColumn1;

    @Column(nullable = true)
    private String nullableColumn2;

    @Column(nullable = false)
    private String nonNullableColumn;

}
