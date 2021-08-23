package com.hardware.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Getter
@Setter
@EqualsAndHashCode(exclude = "localDateTimeSupplier")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String news;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @JsonIgnore
    @Transient
    @Builder.Default
    protected transient Supplier<LocalDateTime> localDateTimeSupplier = LocalDateTime::now;

    public News updated() {
        this.updatedAt = localDateTimeSupplier.get();
        return this;
    }
}
