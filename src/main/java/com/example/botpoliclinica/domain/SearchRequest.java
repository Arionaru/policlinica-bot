package com.example.botpoliclinica.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "search_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "search_request_seq")
    @SequenceGenerator(name = "search_request_seq", sequenceName = "search_request_seq", allocationSize = 1)
    private Long id;

    @Column(name = "lpu_id", nullable = false)
    private Long lpuId;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "created_datetime", nullable = false)
    private LocalDateTime createdDatetime;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @PrePersist
    protected void onCreate() {
        if (createdDatetime == null) {
            createdDatetime = LocalDateTime.now();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        SearchRequest that = (SearchRequest) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
