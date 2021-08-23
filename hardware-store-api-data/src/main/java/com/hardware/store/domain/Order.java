package com.hardware.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@Entity
@Table(name = "store_order")
@Getter
@Setter
@EqualsAndHashCode(exclude = "localDateTimeSupplier")
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private List<OrderItem> orderItems;

    private Double totalPrice;

    private LocalDateTime orderDateTime;

    @OneToOne(cascade = CascadeType.ALL)
    private Address shippingAddress;

    @JsonIgnore
    @Transient
    @Builder.Default
    protected transient Supplier<LocalDateTime> localDateTimeSupplier = LocalDateTime::now;

    public Order ordered() {
        this.orderDateTime = localDateTimeSupplier.get();
        return this;
    }

}
