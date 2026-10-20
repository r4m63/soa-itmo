package com.itmo.soa.contract.common;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Поле тела PATCH с тремя состояниями: не передано, передано со значением, передано как null.
 */
public final class PatchField<T> {

    private static final PatchField<?> ABSENT = new PatchField<>(false, null);

    private final boolean present;
    private final T value;

    private PatchField(boolean present, T value) {
        this.present = present;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static <T> PatchField<T> absent() {
        return (PatchField<T>) ABSENT;
    }

    public static <T> PatchField<T> of(T value) {
        return new PatchField<>(true, value);
    }

    public boolean isPresent() {
        return present;
    }

    public T value() {
        if (!present) {
            throw new IllegalStateException("Поле не передано");
        }
        return value;
    }

    public T orElse(T other) {
        return present ? value : other;
    }

    public void ifPresent(Consumer<? super T> action) {
        if (present) {
            action.accept(value);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PatchField<?> other && present == other.present && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(present, value);
    }

    @Override
    public String toString() {
        return present ? "PatchField[" + value + "]" : "PatchField[absent]";
    }
}
