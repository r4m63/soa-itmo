package com.itmo.soa.movie.infrastructure.exception;

import com.itmo.soa.contract.error.response.FieldError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<FieldError> errors = e.getConstraintViolations().stream()
                .map(ConstraintViolationExceptionMapper::toFieldError)
                .distinct()
                .sorted(Comparator.comparing(FieldError::getField, Comparator.nullsFirst(Comparator.naturalOrder())))
                .toList();
        boolean body = errors.stream().anyMatch(error -> FieldError.BODY.equals(error.getLocation()));
        return ErrorResponses.validation(body ? "Некорректное тело запроса" : "Некорректные параметры запроса", errors);
    }

    private static FieldError toFieldError(ConstraintViolation<?> violation) {
        List<String> path = properties(violation);
        Field field = path.isEmpty() ? null : field(violation.getLeafBean(), path.getLast());
        if (field == null) {
            return FieldError.body(null, null, violation.getMessage());
        }
        Object rejected = violation.getInvalidValue();
        if (field.isAnnotationPresent(PathParam.class)) {
            return FieldError.path(field.getAnnotation(PathParam.class).value(), rejected, violation.getMessage());
        }
        if (field.isAnnotationPresent(QueryParam.class)) {
            return FieldError.query(field.getAnnotation(QueryParam.class).value(), rejected, violation.getMessage());
        }
        return FieldError.body(String.join(".", path), rejected, violation.getMessage());
    }

    private static List<String> properties(ConstraintViolation<?> violation) {
        List<String> names = new ArrayList<>();
        for (Path.Node node : violation.getPropertyPath()) {
            if (node.getKind() == ElementKind.PROPERTY) {
                names.add(node.getName());
            }
        }
        return names;
    }

    private static Field field(Object bean, String name) {
        try {
            return bean.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
