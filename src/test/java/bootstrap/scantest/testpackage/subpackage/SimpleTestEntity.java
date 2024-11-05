package bootstrap.scantest.testpackage.subpackage;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SimpleTestEntity {
    @Id
    private Long id;
}
