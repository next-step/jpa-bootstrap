package persistence.core;


import domain.FixtureAssociatedEntity;
import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.PersistenceException;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityMetadataTest {
    private Class<?> mockClass;

    @Test
    @DisplayName("Entity 클래스를 이용해 EntityMetadata 인스턴스를 생성할 수 있다.")
    void entityMetadataCreateTest() {
        mockClass = FixtureEntity.WithId.class;
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        assertSoftly(softly -> {
            softly.assertThat(entityMetadata).isNotNull();
            softly.assertThat(entityMetadata.getTableName()).isEqualTo("WithId");
            softly.assertThat(entityMetadata.getIdColumnName()).isEqualTo("id");
        });
    }

    @Test
    @DisplayName("Entity 클래스에 @Entity 가 붙어있지 않으면 인스턴스 생성에 실패해야한다.")
    void entityMetadataCreateFailureTest() {
        mockClass = FixtureEntity.WithoutEntity.class;
        assertThatThrownBy(() -> EntityMetadata.from(mockClass))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("Entity 클래스에 @Table 설정을 통해 tableName 을 설정해 인스턴스를 생성 할 수 있다.")
    void tableAnnotatedEntityMetadataCreateTest() {
        mockClass = FixtureEntity.WithTable.class;
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        assertSoftly(softly -> {
            softly.assertThat(entityMetadata).isNotNull();
            softly.assertThat(entityMetadata.getTableName()).isEqualTo("test_table");
            softly.assertThat(entityMetadata.getIdColumnName()).isEqualTo("id");
        });
    }

    @Test
    @DisplayName("Entity 클래스에 @Column(insertable) 설정을 통해 column insert 여부를 설정해 인스턴스를 생성 할 수 있다.")
    void withColumnNonInsertableEntityMetadataCreateTest() {
        mockClass = FixtureEntity.WithColumnNonInsertable.class;
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        assertSoftly(softly -> {
            softly.assertThat(entityMetadata).isNotNull();
            softly.assertThat(entityMetadata.getTableName()).isEqualTo("WithColumnNonInsertable");
            softly.assertThat(entityMetadata.getIdColumnName()).isEqualTo("id");
            softly.assertThat(entityMetadata.toInsertableColumnNames()).containsExactly("insertableColumn");
        });
    }

    @Test
    @DisplayName("getColumnNames 를 통해 column 들의 이름들을 반환 받을 수 있다.")
    void entityMetadataGetColumnNamesTest() {
        mockClass = FixtureEntity.WithColumn.class;
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        assertSoftly(softly -> {
            softly.assertThat(entityMetadata).isNotNull();
            softly.assertThat(entityMetadata.getTableName()).isEqualTo("WithColumn");
            softly.assertThat(entityMetadata.getIdColumnName()).isEqualTo("id");
            softly.assertThat(entityMetadata.toColumnNames()).containsExactly("id", "test_column", "notNullColumn");
        });
    }

    @Test
    @DisplayName("getColumnFieldNames 를 통해 column 들의 field 이름들을 반환 받을 수 있다.")
    void entityMetadataGetColumnFieldNamesTest() {
        mockClass = FixtureEntity.WithColumn.class;
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        assertSoftly(softly -> {
            softly.assertThat(entityMetadata).isNotNull();
            softly.assertThat(entityMetadata.getTableName()).isEqualTo("WithColumn");
            softly.assertThat(entityMetadata.getIdColumnName()).isEqualTo("id");
            softly.assertThat(entityMetadata.toColumnFieldNames()).containsExactly("id", "column", "notNullColumn");
        });
    }

    @Test
    @DisplayName("getIdColumn 를 통해 id column 을 반환 받을 수 있다.")
    void getIdColumnTest() throws NoSuchFieldException {
        mockClass = FixtureEntity.WithId.class;
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        final EntityColumn idColumn = new EntityIdColumn(mockClass.getDeclaredField("id"), "WithId");

        assertThat(entityMetadata.getIdColumn()).isEqualTo(idColumn);
    }

    @Test
    @DisplayName("getInsertableColumn 를 통해 insertable column 을 반환 받을 수 있다.")
    void getInsertableColumnTest() throws NoSuchFieldException {
        mockClass = FixtureEntity.WithColumnNonInsertable.class;
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        final EntityColumn insertableColumn = new EntityFieldColumn(mockClass.getDeclaredField("insertableColumn"), "WithColumnNonInsertable");

        assertThatIterable(entityMetadata.toInsertableColumn()).containsExactly(insertableColumn);
    }

    @Test
    @DisplayName("getOneToManyColumns 를 통해 OneToMany columns 를 반환 받을 수 있다.")
    void getOneToManyColumnsTest() throws Exception {
        mockClass = FixtureAssociatedEntity.WithOneToManyJoinColumn.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        final EntityOneToManyColumn oneToManyColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"), "WithOneToManyJoinColumn");

        assertThatIterable(entityMetadata.getOneToManyColumns()).containsExactly(oneToManyColumn);
    }

    @Test
    @DisplayName("hasAssociatedOf 를 통해 EntityMetadata 가 특정 EntityMetadata 를 associated 하고 있는지 여부를 반환 받을 수 있다.")
    void hasAssociatedOfTest() {
        final EntityMetadata<?> entityMetadata = EntityMetadata.from(FixtureAssociatedEntity.WithOneToManyJoinColumn.class);
        final EntityMetadata<?> associatedEntityMetadata = EntityMetadata.from(FixtureAssociatedEntity.WithId.class);
        final EntityMetadata<?> notAssociatedEntityMetadata = EntityMetadata.from(FixtureEntity.WithIdAndColumn.class);

        assertSoftly(softly -> {
            softly.assertThat(entityMetadata.hasOneToManyAssociatedOf(associatedEntityMetadata)).isTrue();
            softly.assertThat(entityMetadata.hasOneToManyAssociatedOf(notAssociatedEntityMetadata)).isFalse();
        });
    }

    @Test
    @DisplayName("getIdType 를 통해 EntityMetadata 의 Id Column Class Type 을 반환 받을 수 있다.")
    void getIdTypeTest() {
        mockClass = FixtureAssociatedEntity.WithId.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);

        assertThat(entityMetadata.getIdType()).isEqualTo(Long.class);
    }

    @Test
    @DisplayName("getIdName 를 통해 EntityMetadata 의 Id Column Name 을 반환 받을 수 있다.")
    void getIdNameTest() {
        mockClass = FixtureAssociatedEntity.WithId.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);

        assertThat(entityMetadata.getIdName()).isEqualTo("id");
    }

    @Test
    @DisplayName("getColumnNamesWithAlias 를 통해 EntityMetadata with OneToMany 의 Column Name 들을 Alias 와 함께 반환 받을 수 있다.")
    void getColumnNamesWithAliasOneToManyTest() {
        mockClass = FixtureAssociatedEntity.Order.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);

        assertThat(entityMetadata.getColumnNamesWithAlias()).containsExactly(
                "orders.id",
                "orders.orderNumber",
                "order_items.id",
                "order_items.product",
                "order_items.quantity"
        );
    }

    @Test
    @DisplayName("getColumnNamesWithAlias 를 통해 EntityMetadata with ManyToOne 의 Column Name 들을 Alias 와 함께 반환 받을 수 있다.")
    void getColumnNamesWithAliasManyToOneTest() {
        mockClass = FixtureAssociatedEntity.City.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);

        assertThat(entityMetadata.getColumnNamesWithAlias()).containsExactly(
                "city.id",
                "city.name",
                "country.id",
                "country.name"
        );
    }


    @Test
    @DisplayName("getLazyOneToManyColumns 를 통해 해당 Entity 의 OneToMany(Lazy) 컬럼들을 반환 받을 수 있다.")
    void getLazyOneToManyColumnsTest() {
        mockClass = FixtureAssociatedEntity.Order.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        final EntityMetadata<?> withOneToManyEntityMetadata = EntityMetadata.from(FixtureAssociatedEntity.WithOneToMany.class);

        assertSoftly(softly -> {
            softly.assertThat(entityMetadata.getLazyOneToManyColumns()).hasSize(0);
            softly.assertThat(withOneToManyEntityMetadata.getLazyOneToManyColumns()).hasSize(1);
        });
    }

    @Test
    @DisplayName("getLazyOneToManyColumns 를 통해 해당 Entity 의 OneToMany(Eager) 컬럼들을 반환 받을 수 있다.")
    void getEagerOneToManyColumnsTest() {
        mockClass = FixtureAssociatedEntity.Order.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        final EntityMetadata<?> withOneToManyEntityMetadata = EntityMetadata.from(FixtureAssociatedEntity.WithOneToMany.class);

        assertSoftly(softly -> {
            softly.assertThat(entityMetadata.getEagerOneToManyColumns()).hasSize(1);
            softly.assertThat(withOneToManyEntityMetadata.getEagerOneToManyColumns()).hasSize(0);
        });
    }

    @Test
    @DisplayName("getLazyManyToOneColumns 를 통해 ManyToOne(Lazy) columns 를 반환 받을 수 있다.")
    void getLazyManyToOneColumnsTest() throws Exception {
        mockClass = FixtureAssociatedEntity.WithTwoManyToOneColumns.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        final EntityManyToOneColumn manyToOneColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("lazyWithId"), "WithTwoManyToOneColumns");

        assertThatIterable(entityMetadata.getLazyManyToOneColumns()).containsExactly(manyToOneColumn);
    }

    @Test
    @DisplayName("getEagerManyToOneColumns 를 통해 ManyToOne(Eager) columns 를 반환 받을 수 있다.")
    void getEagerManyToOneColumnsTest() throws Exception {
        mockClass = FixtureAssociatedEntity.WithTwoManyToOneColumns.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);
        final EntityManyToOneColumn manyToOneColumn = new EntityManyToOneColumn(mockClass.getDeclaredField("eagerWithId"), "WithTwoManyToOneColumns");

        assertThatIterable(entityMetadata.getEagerManyToOneColumns()).containsExactly(manyToOneColumn);
    }

    @Test
    @DisplayName("getColumnFieldNames 을 이용해 entity 의 필드 이름들을 반환 받을 수 있다.")
    void getColumnFieldNamesTest() {
        mockClass = FixtureAssociatedEntity.Order.class;

        final EntityMetadata<?> entityMetadata = EntityMetadata.from(mockClass);

        assertThatIterable(entityMetadata.getColumnFieldNames()).containsExactly("id", "orderNumber", "orderItems");
    }

}
