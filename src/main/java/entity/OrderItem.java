package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product")
    private String product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "order_id")
    private Long orderId;

    public OrderItem(String product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public OrderItem(String product, Integer quantity, Long orderId) {
        this.product = product;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    public OrderItem() {

    }

    public OrderItem(Long id, String product, Integer quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", orderId=" + orderId +
                '}';
    }
}
