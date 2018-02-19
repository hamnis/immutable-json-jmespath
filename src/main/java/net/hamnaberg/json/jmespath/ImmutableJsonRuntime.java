package net.hamnaberg.json.jmespath;

import io.burt.jmespath.BaseRuntime;
import io.burt.jmespath.JmesPathType;
import io.burt.jmespath.function.FunctionRegistry;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import net.hamnaberg.json.Json;
import net.hamnaberg.json.io.JsonParser;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ImmutableJsonRuntime extends BaseRuntime<Json.JValue> {
    private final JsonParser parser;

    public ImmutableJsonRuntime(JsonParser parser) {
        this(FunctionRegistry.defaultRegistry(), parser);
    }

    public ImmutableJsonRuntime(FunctionRegistry functionRegistry, JsonParser parser) {
        super(functionRegistry);
        this.parser = parser;
    }

    public Json.JValue parseString(String s) {
        return parser.parseUnsafe(s);
    }

    public List<Json.JValue> toList(Json.JValue jValue) {
        return jValue.fold(
                Function1.constant(Collections.emptyList()),
                Function1.constant(Collections.emptyList()),
                Function1.constant(Collections.emptyList()),
                obj -> obj.getValue().values().toJavaList(),
                arr -> arr.value.toJavaList(),
                Collections::emptyList
        );
    }

    public String toString(Json.JValue jValue) {
        return jValue.asString().getOrElse(jValue.nospaces());
    }

    public Number toNumber(Json.JValue jValue) {
        return jValue.asJsonNumber().map(Json.JNumber::getValue).getOrNull();
    }

    public boolean isTruthy(Json.JValue jValue) {
        return jValue.fold(
                s -> !s.value.isEmpty(),
                s -> s.value,
                s -> true,
                obj -> !obj.isEmpty(),
                arr -> arr.value.nonEmpty(),
                () -> false
        );
    }

    public JmesPathType typeOf(Json.JValue jValue) {
        return jValue.fold(
                Function1.constant(JmesPathType.STRING),
                Function1.constant(JmesPathType.BOOLEAN),
                Function1.constant(JmesPathType.NUMBER),
                Function1.constant(JmesPathType.OBJECT),
                Function1.constant(JmesPathType.ARRAY),
                () -> JmesPathType.NULL
        );
    }

    public Json.JValue getProperty(Json.JValue jValue, Json.JValue name) {
        return jValue.asJsonObjectOrEmpty().get(stringOrThrow(name)).getOrElse(Json.jNull());
    }

    public Collection<Json.JValue> getPropertyNames(Json.JValue jValue) {
        return jValue.asJsonObjectOrEmpty().keySet().map(this::createString).toJavaList();
    }

    public Json.JValue createNull() {
        return Json.jNull();
    }

    public Json.JValue createArray(Collection<Json.JValue> collection) {
        return Json.jArray(collection);
    }

    public Json.JValue createString(String s) {
        return Json.jString(s);
    }

    public Json.JValue createBoolean(boolean b) {
        return Json.jBoolean(b);
    }

    private String stringOrThrow(Json.JValue value) {
        return value.asString().getOrElseThrow(() -> new IllegalArgumentException("Not a string"));
    }

    public Json.JValue createObject(Map<Json.JValue, Json.JValue> map) {
        HashMap<String, Json.JValue> value = map.entrySet().stream().map(e ->
                Tuple.of(stringOrThrow(e.getKey()), e.getValue())
        ).collect(HashMap.collector());

        return Json.jObject(value);
    }

    public Json.JValue createNumber(double value) {
        return Json.jNumber(value);
    }

    public Json.JValue createNumber(long value) {
        return Json.jNumber(value);
    }
}
