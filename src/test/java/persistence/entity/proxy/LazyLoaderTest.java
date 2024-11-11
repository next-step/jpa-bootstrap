package persistence.entity.proxy;

import database.H2ConnectionFactory;
import domain.Order;
import domain.OrderLazy;
import jdbc.JdbcTemplate;
import jdbc.mapper.DefaultRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.loader.CollectionLoader;
import persistence.meta.EntityTable;
import persistence.sql.dml.SelectQuery;

import static org.assertj.core.api.Assertions.*;

class LazyLoaderTest {
    private JdbcTemplate jdbcTemplate;
    private SelectQuery selectQuery;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        selectQuery = new SelectQuery();
    }

    @Test
    @DisplayName("FetchType이 LAZY인 클래스로 인스턴스를 생성한다.")
    void constructor() {
        // given
        final EntityTable entityTable = new EntityTable(OrderLazy.class);
        final DefaultRowMapper rowMapper = new DefaultRowMapper(entityTable);
        final CollectionLoader collectionLoader = new CollectionLoader(entityTable, jdbcTemplate, selectQuery, rowMapper);

        // when
        final LazyLoader lazyLoader = new LazyLoader(entityTable, collectionLoader);

        // then
        assertThat(lazyLoader).isNotNull();
    }

    @Test
    @DisplayName("FetchType이 LAZY가 아닌 클래스로 인스턴스를 생성하면 예외를 발생한다.")
    void constructor_exception() {
        // given
        final EntityTable entityTable = new EntityTable(Order.class);
        final DefaultRowMapper rowMapper = new DefaultRowMapper(entityTable);
        final CollectionLoader collectionLoader = new CollectionLoader(entityTable, jdbcTemplate, selectQuery, rowMapper);

        // when & then
        assertThatThrownBy(() -> new LazyLoader(entityTable, collectionLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LazyLoader.NO_ONE_TO_ONE_LAZY_FAILED_MESSAGE);
    }
}
