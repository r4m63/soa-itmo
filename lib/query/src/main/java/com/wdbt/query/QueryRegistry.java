package com.wdbt.query;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class QueryRegistry {

    private final Map<Class<?>, QuerySchema> schemas;

    private QueryRegistry(Map<Class<?>, QuerySchema> schemas) {
        this.schemas = Collections.unmodifiableMap(schemas);
    }

    public static QueryRegistry of(Class<?>... entityTypes) {
        Map<Class<?>, QuerySchema> schemas = new LinkedHashMap<>();
        for (Class<?> entityType : entityTypes) {
            if (schemas.putIfAbsent(entityType, buildSchema(entityType)) != null) {
                throw new IllegalArgumentException("Сущность " + entityType.getName() + " уже зарегистрирована");
            }
        }
        return new QueryRegistry(schemas);
    }

    public QuerySchema schema(Class<?> entityType) {
        QuerySchema schema = schemas.get(entityType);
        if (schema == null) {
            throw new IllegalArgumentException("Query-сущность " + entityType.getName() + " не зарегистрирована");
        }
        return schema;
    }

    private static QuerySchema buildSchema(Class<?> entityType) {
        QueryEntity entity = entityType.getAnnotation(QueryEntity.class);
        if (entity == null) {
            throw new IllegalArgumentException("На классе " + entityType.getName() + " нет @QueryEntity");
        }

        QuerySchema.Builder builder = QuerySchema.builder().pageSize(entity.defaultPageSize(), entity.maxPageSize());
        addFields(entityType, "", builder);
        for (String sort : entity.defaultSort()) {
            builder.defaultSort(sortField(sort, entityType), sortDirection(sort, entityType));
        }
        if (!entity.tieBreaker().isBlank()) {
            builder.tieBreaker(entity.tieBreaker(), entity.tieBreakerDirection());
        }
        return builder.build();
    }

    private static void addFields(Class<?> type, String prefix, QuerySchema.Builder builder) {
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            hierarchy.addFirst(current);
        }
        for (Class<?> current : hierarchy) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String javaPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();
                QueryAttribute attribute = field.getAnnotation(QueryAttribute.class);
                if (attribute != null) {
                    builder.field(toQueryField(field, javaPath, attribute));
                }
                if (hasQueryAttributes(field.getType())) {
                    addFields(field.getType(), javaPath, builder);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static QueryField toQueryField(Field field, String javaPath, QueryAttribute attribute) {
        FieldType type = resolveType(field.getType(), attribute.type());
        String name = attribute.name().isBlank() ? javaPath : attribute.name();
        String path = attribute.path().isBlank() ? javaPath : attribute.path();
        boolean nullable = attribute.nullable() && !field.getType().isPrimitive();
        boolean sortable = attribute.sortable() && type != FieldType.OBJECT;
        Class<? extends Enum<?>> enumType = type == FieldType.ENUM
                ? (Class<? extends Enum<?>>) (Class<?>) field.getType()
                : null;
        Set<Operator> operators = operators(type, nullable, attribute.operators(), name);
        String presencePath = attribute.presencePath().isBlank() ? path : attribute.presencePath();
        return new QueryField(name, type, enumType, nullable, sortable, path, presencePath, operators);
    }

    private static FieldType resolveType(Class<?> javaType, FieldType requested) {
        if (requested != FieldType.AUTO) {
            if (requested == FieldType.ENUM && !javaType.isEnum()) {
                throw new IllegalArgumentException("ENUM допустим только для Java enum: " + javaType.getName());
            }
            return requested;
        }
        if (javaType == Integer.class || javaType == int.class) return FieldType.INTEGER;
        if (javaType == Long.class || javaType == long.class) return FieldType.LONG;
        if (javaType == LocalDate.class) return FieldType.DATE;
        if (javaType == String.class || CharSequence.class.isAssignableFrom(javaType)) return FieldType.STRING;
        if (javaType.isEnum()) return FieldType.ENUM;
        return FieldType.OBJECT;
    }

    private static Set<Operator> operators(FieldType type, boolean nullable, Operator[] requested, String name) {
        EnumSet<Operator> result = requested.length == 0
                ? defaultOperators(type)
                : EnumSet.copyOf(List.of(requested));
        for (Operator operator : result) {
            boolean nullOperator = operator == Operator.IS_NULL || operator == Operator.NOT_NULL;
            if (nullOperator ? !nullable : !type.operators().contains(operator)) {
                throw new IllegalArgumentException("Оператор " + operator.token() + " недопустим для поля " + name);
            }
        }
        if (nullable) {
            result.add(Operator.IS_NULL);
            result.add(Operator.NOT_NULL);
        }
        return Collections.unmodifiableSet(result);
    }

    private static EnumSet<Operator> defaultOperators(FieldType type) {
        return type.operators().isEmpty() ? EnumSet.noneOf(Operator.class) : EnumSet.copyOf(type.operators());
    }

    private static boolean hasQueryAttributes(Class<?> type) {
        if (type.isPrimitive() || type.isEnum() || type.getName().startsWith("java.")) {
            return false;
        }
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(QueryAttribute.class)) {
                return true;
            }
        }
        return false;
    }

    private static String sortField(String raw, Class<?> entityType) {
        String[] parts = raw.split(";", -1);
        if (parts.length != 2 || parts[0].isBlank()) {
            throw new IllegalArgumentException("Некорректная defaultSort у " + entityType.getName() + ": " + raw);
        }
        return parts[0];
    }

    private static Direction sortDirection(String raw, Class<?> entityType) {
        String[] parts = raw.split(";", -1);
        return parts.length == 2 ? Direction.fromToken(parts[1]).orElseThrow(
                () -> new IllegalArgumentException("Некорректное направление defaultSort у " + entityType.getName() + ": " + raw))
                : Direction.ASC;
    }
}
