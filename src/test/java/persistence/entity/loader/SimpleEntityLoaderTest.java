package persistence.entity.loader;

import entity.*;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Nested
@DisplayName("SimpleEntityLoader 클래스의")
public class SimpleEntityLoaderTest extends DatabaseTest {
    private final EntityAttributes entityAttributes = new EntityAttributes();

    public SimpleEntityLoaderTest() throws SQLException {
    }

    @Nested
    @DisplayName("load 메소드는")
    class load {
        @Nested
        @DisplayName("클래스정보와 아이디가 주어지면")
        public class withInstance {
            @Test
            @DisplayName("객체를 찾아온다.")
            void returnData() {
                //given
                setUpFixtureTable(SampleOneWithValidAnnotation.class, new H2SqlConverter());
                SampleOneWithValidAnnotation sample
                        = new SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);

                SampleOneWithValidAnnotation inserted = simpleEntityPersister.insert(sample);

                SimpleEntityLoader simpleEntityLoader = new SimpleEntityLoader(jdbcTemplate);

                EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(SampleOneWithValidAnnotation.class);

                //when
                SampleOneWithValidAnnotation retrieved =
                        simpleEntityLoader.load(entityAttribute, "id", inserted.getId().toString());

                //then
                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }

        @Nested
        @DisplayName("@OneToMany(fetch = FetchType.EAGER)가 붙은 클래스정보와 아이디가 주어지면")
        public class test {
            @Test
            @DisplayName("연관관계가 매핑된 객체를 찾아온다.")
            void returnData() throws SQLException {
                //given
                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()));
                OrderItem orderItem = new OrderItem("티비", 1, 1L);
                OrderItem orderItem2 = new OrderItem("세탁기", 2, 1L);
                Order order = new Order("1324", List.of(orderItem, orderItem2));

                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);

                setUpFixtureTable(OrderItem.class, new H2SqlConverter());
                setUpFixtureTable(Order.class, new H2SqlConverter());

                simpleEntityPersister.insert(orderItem);
                simpleEntityPersister.insert(orderItem2);
                simpleEntityPersister.insert(order);

                //when
                Order retrievedOrder = simpleEntityPersister.load(Order.class, "1");

                //then
                assertThat(retrievedOrder.toString())
                        .isEqualTo("Order{id=1, orderNumber='1324', orderItems=[OrderItem{id=1, product='티비', quantity=1, orderId=1}, OrderItem{id=1, product='세탁기', quantity=2, orderId=1}]}");
            }
        }

        @Nested
        @DisplayName("@OneToMany(fetch = FetchType.LAZY)가 붙은 클래스정보와 아이디가 주어지면")
        public class withLazyProxy {
            @Test
            @DisplayName("연관관계가 매핑된 객체를 찾아온다.")
            void returnData() throws SQLException {
                //given
                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()));

                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);

                setUpFixtureTable(Member.class, new H2SqlConverter());
                setUpFixtureTable(Team.class, new H2SqlConverter());

                Member member1 = new Member("사람1", 1L);
                Member member2 = new Member("사람2", 1L);
                Member insertedMember1 = simpleEntityPersister.insert(member1);
                Member insertedMember2 = simpleEntityPersister.insert(member2);

                Team team = new Team(List.of(insertedMember1, insertedMember2));

                simpleEntityPersister.insert(team);

                //when
                Team loadedTeam = simpleEntityPersister.load(Team.class, "1");

                //then
                assertThat(loadedTeam.toString())
                        .isEqualTo("Team{id=1, members=[Member{id=1, name='사람1', teamId=1}, Member{id=2, name='사람2', teamId=1}]}");
            }
        }
    }
}
