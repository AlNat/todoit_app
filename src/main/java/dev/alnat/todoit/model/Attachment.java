package dev.alnat.todoit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "attachment", schema = "todoit")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @Lob
    @Column(columnDefinition = "blob", updatable = false, nullable = false)
    private byte[] data;

    @Column(updatable = false, nullable = false)
    private Long size;

    @Column(updatable = false, nullable = false)
    private String name;

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
