package com.example.botpoliclinica.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lpus")
public class Lpus {
    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    private String fullName;
    private String address;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Lpus lpus = (Lpus) o;
        return id != null && Objects.equals(id, lpus.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
