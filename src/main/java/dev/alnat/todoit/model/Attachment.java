package dev.alnat.todoit.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "attachment", schema = "todoit")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    @ToString.Include
    private LocalDateTime created;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(columnDefinition = "bytea", updatable = false, nullable = false)
    private byte[] data;

    @ToString.Include
    @Column(updatable = false, nullable = false)
    private Long size;

    @ToString.Include
    @Column(updatable = false, nullable = false)
    private String name;

    @ToString.Include
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Task task;

    public Attachment(Long id, LocalDateTime created, Long size, String name) {
        this.id = id;
        this.created = created;
        this.size = size;
        this.name = name;
    }

}
