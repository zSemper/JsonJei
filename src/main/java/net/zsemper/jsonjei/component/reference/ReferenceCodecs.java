package net.zsemper.jsonjei.component.reference;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ExtraCodecs;

public class ReferenceCodecs {
    private ReferenceCodecs() {}

    public static final Codec<ReferenceValue<Integer>> INT_CODEC = create(Codec.INT, 'i');
    public static final Codec<ReferenceValue<Double>> DOUBLE_CODEC = create(Codec.DOUBLE, 'd');
    public static final Codec<ReferenceValue<Boolean>> BOOLEAN_CODEC = create(Codec.BOOL, 'b');
    public static final Codec<ReferenceValue<String>> STRING_CODEC = create(Codec.STRING, 's');

    public static final Codec<ReferenceValue<Integer>> POSITIVE_INT_CODEC = create(ExtraCodecs.POSITIVE_INT, 'i');
    public static final Codec<ReferenceValue<Integer>> NON_NEGATIVE_INT_CODEC = create(ExtraCodecs.NON_NEGATIVE_INT, 'i');

    /**
     * Creates a reference codec that can either be a value, or a reference key
     * to a value
     * <p>
     * The structure for reference keys are {@code $([type]:[key])}, where the {@code type}
     * describes the type of the value (e.g.: 'i' for integer, 'b' for boolean) and the {@code key}
     * describing the name to the reference of the value
     *
     * @param baseCodec     The codec of the value of the reference
     * @param referenceType The type of the reference codec
     * @return              The reference codec of the specified type
     * @param <T>           The type of value the reference codec will encode/decode
     */
    public static <T> Codec<ReferenceValue<T>> create(Codec<T> baseCodec, char referenceType) {
        return new Codec<>() {
            @Override
            public <T2> DataResult<Pair<ReferenceValue<T>, T2>> decode(DynamicOps<T2> ops, T2 input) {
                DataResult<T> literalResult = baseCodec.parse(ops, input);

                if (literalResult.result().isPresent()) {
                    if (literalResult.result().get() instanceof String string) {
                        if (!string.startsWith("$(") && !string.endsWith(")")) {
                            return DataResult.success(Pair.of(new ReferenceValue.Literal<>(literalResult.result().get()), input));
                        }
                    } else {
                        return DataResult.success(Pair.of(new ReferenceValue.Literal<>(literalResult.result().get()), input));
                    }
                }
                return Codec.STRING.parse(ops, input)
                        .flatMap(string -> ReferenceCodecs.<T>parseReference(string, referenceType))
                        .map(reference -> Pair.of(reference, input));
            }

            @Override
            public <T2> DataResult<T2> encode(ReferenceValue<T> input, DynamicOps<T2> ops, T2 prefix) {
                if (input instanceof ReferenceValue.Literal<T>(T value)) {
                    return baseCodec.encode(value, ops, prefix);
                } else if (input instanceof ReferenceValue.Reference<T>(char type, String key)) {
                    String encoded = "$(" + type + ":" + key + ")";
                    return Codec.STRING.encode(encoded, ops, prefix);
                } else {
                    return DataResult.error(() -> "Unknown value type");
                }
            }
        };
    }

    private static <T> DataResult<ReferenceValue<T>> parseReference(String providedValue, char expectedType) {
        if (!providedValue.startsWith("$(") || !providedValue.endsWith(")")) {
            return DataResult.error(() -> "Expected reference $(" + expectedType + ":[key])");
        }

        String content = providedValue.substring(2, providedValue.length() - 1);
        if (content.length() < 3 || content.charAt(1) != ':') {
            return DataResult.error(() -> "Invalid reference: " + providedValue);
        }

        char type = content.charAt(0);
        String key = content.substring(2);

        if (type != expectedType) {
            return DataResult.error(() -> "Expected reference type '" + providedValue + "', got '" + type + "'");
        }

        if (key.isBlank()) {
            return DataResult.error(() -> "Reference key cannot be empty");
        }

        return DataResult.success(new ReferenceValue.Reference<>(type, key));
    }
}
