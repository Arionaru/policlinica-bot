package com.example.botpoliclinica.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "district")
@EqualsAndHashCode(of = "id")
public class District {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "district")
    private Set<Lpus> lpuses;
}
