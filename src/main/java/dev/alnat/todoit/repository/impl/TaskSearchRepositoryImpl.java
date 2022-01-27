package dev.alnat.todoit.repository.impl;

import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.model.Task;
import dev.alnat.todoit.repository.TaskSearchRepository;
import dev.alnat.todoit.common.Sorting;
import dev.alnat.todoit.search.TaskSearchRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ручная реализация поискового репозитория для совмещения со SpringData
 *
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@RequiredArgsConstructor
@Repository
public class TaskSearchRepositoryImpl implements TaskSearchRepository {

    private static final String PLANNED = "planned"; // Имя переменной в Task

    @PersistenceContext
    private final EntityManager em;
    private final List<TaskStatus> hideStatusList;

    @Override
    public List<Task> findByParam(final TaskSearchRequest filter) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = cb.createQuery(Task.class);
        final Root<Task> root = criteriaQuery.from(Task.class); // Откуда будут браться данные
        final List<Predicate> conditions = new ArrayList<>(); // Набор условий
        final List<Order> orderList = new ArrayList<>(); // Набор для сортировки

        // Принудительно скрываем статусы которые не нужно показывать
        conditions.add(cb.not(root.<TaskStatus>get("status").in(hideStatusList)));

        // Статус задачи
        if (filter.getStatusList() != null) {
            conditions.add(root.<TaskStatus>get("status").in(filter.getStatusList()));
        }

        // Дата начала выборки
        if (filter.getPlannedFrom() != null) {
            conditions.add(cb.greaterThanOrEqualTo(root.get(PLANNED), filter.getPlannedFrom()));
        }

        // Дата окончания выборки
        if (filter.getPlannedTo() != null) {
            conditions.add(cb.lessThanOrEqualTo(root.get(PLANNED), filter.getPlannedTo()));
        }

        // Сортируем
        if (filter.getSorting() != null && !filter.getSorting().isEmpty()) {
            for (Sorting sorting : filter.getSorting()) {
                final Order order;
                if (!StringUtils.hasText(sorting.getSortBy())) {
                    continue;
                }
                final Expression<?> orderExpression = root.get(sorting.getSortBy());

                // По-умолчания по возрастанию
                if (sorting.getSortOrder() == null || sorting.getSortOrder() == Sorting.SortOrder.ASC) {
                    order = cb.asc(orderExpression);
                } else {
                    order = cb.desc(orderExpression);
                }

                orderList.add(order);
            }
        } else {
            // По умолчанию сортируем по планируемой дате
            orderList.add(cb.asc(root.get(PLANNED)));
        }

        criteriaQuery = criteriaQuery
                .where(cb.and(conditions.toArray(new Predicate[0])))
                .orderBy(orderList).orderBy();

        return em
                .unwrap(Session.class)
                .createQuery(criteriaQuery)
                .setReadOnly(true)
                .setFirstResult(filter.getOffset())
                .setMaxResults(filter.getLimit() + 1) // Для флага hasMore
                .getResultList();
    }

}
