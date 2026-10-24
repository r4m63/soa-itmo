package com.itmo.soa.query.jpa;

import com.itmo.soa.query.Direction;
import com.itmo.soa.query.FieldType;
import com.itmo.soa.query.Filter;
import com.itmo.soa.query.Operator;
import com.itmo.soa.query.Page;
import com.itmo.soa.query.Query;
import com.itmo.soa.query.QueryField;
import com.itmo.soa.query.Sort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Выполняет {@link Query} над JPA-сущностью: условия, сортировка, страница и общее количество.
 *
 * <p>Семантика сравнения:
 * <ul>
 *     <li>contains / startsWith / endsWith — без учёта регистра, остальные сравнения строк — с учётом;</li>
 *     <li>gt / gte / lt / lte и сортировка для enum — по порядку объявления, не по строке в базе;</li>
 *     <li>если значение поля null, проходят только isNull и ne.</li>
 * </ul>
 */
public final class JpaQueryExecutor {

    private static final char LIKE_ESCAPE = '\\';

    private JpaQueryExecutor() {
    }

    public static <T> Page<T> execute(EntityManager em, Class<T> entityType, Query query) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityType);
        countQuery.select(cb.count(countRoot)).where(where(cb, countRoot, query));
        long total = em.createQuery(countQuery).getSingleResult();

        long offset = query.page().offset();
        if (total == 0 || offset >= total) {
            return Page.of(List.of(), query.page(), total);
        }

        CriteriaQuery<T> select = cb.createQuery(entityType);
        Root<T> root = select.from(entityType);
        select.select(root).where(where(cb, root, query)).orderBy(orders(cb, root, query.effectiveSort()));
        List<T> items = em.createQuery(select)
                .setFirstResult((int) offset)
                .setMaxResults(query.page().size())
                .getResultList();
        return Page.of(items, query.page(), total);
    }

    private static Predicate where(CriteriaBuilder cb, Root<?> root, Query query) {
        List<Predicate> groups = new ArrayList<>();
        for (List<Filter> group : query.filterGroups()) {
            Predicate[] alternatives = group.stream().map(f -> predicate(cb, root, f)).toArray(Predicate[]::new);
            groups.add(alternatives.length == 1 ? alternatives[0] : cb.or(alternatives));
        }
        return cb.and(groups.toArray(Predicate[]::new));
    }

    private static List<Order> orders(CriteriaBuilder cb, Root<?> root, List<Sort> sorts) {
        return sorts.stream().map(sort -> {
            Expression<?> expression = sortExpression(cb, root, sort.field());
            return sort.direction() == Direction.ASC ? cb.asc(expression) : cb.desc(expression);
        }).toList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Predicate predicate(CriteriaBuilder cb, Root<?> root, Filter filter) {
        QueryField field = filter.field();
        if (field.type() == FieldType.OBJECT) {
            Path<?> presence = path(root, field.presencePath());
            return filter.operator() == Operator.IS_NULL ? cb.isNull(presence) : cb.isNotNull(presence);
        }

        Path<?> path = path(root, field.path());
        Object value = filter.value();
        return switch (filter.operator()) {
            case IS_NULL -> cb.isNull(path);
            case NOT_NULL -> cb.isNotNull(path);
            case EQ -> cb.equal(path, value);
            case NE -> cb.or(cb.isNull(path), cb.notEqual(path, value));
            case GT, GTE, LT, LTE -> field.type() == FieldType.ENUM
                    ? enumComparison(cb, path, field, (Enum<?>) value, filter.operator())
                    : comparison(cb, (Expression<Comparable>) path, (Comparable) value, filter.operator());
            case CONTAINS -> like(cb, (Expression<String>) path, "%" + escape((String) value) + "%");
            case STARTS_WITH -> like(cb, (Expression<String>) path, escape((String) value) + "%");
            case ENDS_WITH -> like(cb, (Expression<String>) path, "%" + escape((String) value));
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Predicate comparison(CriteriaBuilder cb, Expression<Comparable> path, Comparable value,
                                        Operator operator) {
        return switch (operator) {
            case GT -> cb.greaterThan(path, value);
            case GTE -> cb.greaterThanOrEqualTo(path, value);
            case LT -> cb.lessThan(path, value);
            case LTE -> cb.lessThanOrEqualTo(path, value);
            default -> throw new IllegalArgumentException("Не оператор сравнения: " + operator);
        };
    }

    private static Predicate enumComparison(CriteriaBuilder cb, Path<?> path, QueryField field, Enum<?> value,
                                            Operator operator) {
        int ordinal = value.ordinal();
        List<Enum<?>> matching = Arrays.stream(field.enumConstants()).filter(c -> switch (operator) {
            case GT -> c.ordinal() > ordinal;
            case GTE -> c.ordinal() >= ordinal;
            case LT -> c.ordinal() < ordinal;
            case LTE -> c.ordinal() <= ordinal;
            default -> throw new IllegalArgumentException("Не оператор сравнения: " + operator);
        }).toList();
        return matching.isEmpty() ? cb.disjunction() : path.in(matching);
    }

    private static Predicate like(CriteriaBuilder cb, Expression<String> path, String pattern) {
        return cb.like(cb.lower(path), pattern.toLowerCase(Locale.ROOT), LIKE_ESCAPE);
    }

    private static String escape(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            if (c == LIKE_ESCAPE || c == '%' || c == '_') {
                sb.append(LIKE_ESCAPE);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static Expression<?> sortExpression(CriteriaBuilder cb, Root<?> root, QueryField field) {
        Path<?> path = path(root, field.path());
        if (field.type() != FieldType.ENUM) {
            return path;
        }
        CriteriaBuilder.Case<Integer> byOrdinal = cb.selectCase();
        for (Enum<?> constant : field.enumConstants()) {
            byOrdinal.when(cb.equal(path, constant), constant.ordinal());
        }
        return byOrdinal;
    }

    private static Path<?> path(Root<?> root, String dotted) {
        Path<?> path = root;
        for (String part : dotted.split("\\.")) {
            path = path.get(part);
        }
        return path;
    }
}
