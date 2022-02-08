package dev.alnat.todoit.model;

import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.model.converter.TaskStatusConverter;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Entity
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "task", schema = "todoit")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updated;

    @NotNull
    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private LocalDateTime planned;

    @EqualsAndHashCode.Include
    @NotNull
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Convert(converter = TaskStatusConverter.class)
    @Column(nullable = false)
    private TaskStatus status;

    /**
     * Цвет фона\задачи (#FFFFFF)
     */
    @Column(columnDefinition = "varchar(7)")
    private String color;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private List<Attachment> attachmentList;

}
