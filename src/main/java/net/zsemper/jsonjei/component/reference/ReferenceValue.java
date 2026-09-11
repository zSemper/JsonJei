package net.zsemper.jsonjei.component.reference;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * A value that can be described as an actual value or as a reference to a value
 * @param <T> The type of the value
 * <p>
 * Example:
 * The first value is a static value, as it will always to 13. The second value
 * points to another field with the name "value2" and id of type int represented
 * by the 'i'
 * <pre>{@code
 * {
 *     "value1": 13,
 *     "value2": "$(i:value2)"
 * }
 * }</pre>
 * @see ReferenceCodecs
 */
public sealed interface ReferenceValue<T> permits ReferenceValue.Literal, ReferenceValue.Reference {

    /**
     * Resolves the reference value into an actual value
     *
     * @param lookup The lookup function. Example usage {@code Value#resolve(key -> [...]}
     * @return       The value of the reference
     */
    @ApiStatus.NonExtendable
    default /*final*/ T resolve(Function<String, T> lookup) {
        return switch (this) {
            case Literal<T> literal -> literal.value();
            case Reference<T> reference -> lookup.apply(reference.key());
        };
    }

    /**
     * The literal value of a reference value
     *
     * @param value The stored value
     */
    record Literal<T>(T value) implements ReferenceValue<T> {}

    /**
     * The reference value
     *
     * @param type The type of reference value (e.g. 'i' for int, 'b' for string)
     * @param key  The key name that points to the actual value
     */
    record Reference<T>(char type, String key) implements ReferenceValue<T> {}
}
