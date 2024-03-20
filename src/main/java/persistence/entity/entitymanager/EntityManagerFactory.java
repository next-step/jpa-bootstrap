package persistence.entity.entitymanager;

import java.sql.Connection;
import java.sql.SQLException;

public interface EntityManagerFactory {

    EntityManager openSession();

}
