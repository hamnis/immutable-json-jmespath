package net.hamnaberg.json.jmespath;

import io.burt.jmespath.Adapter;
import io.burt.jmespath.JmesPathRuntimeTest;
import net.hamnaberg.json.Json;
import net.hamnaberg.json.jackson.JacksonStreamingParser;

public class ImmutableJsonJacksonTest extends JmesPathRuntimeTest<Json.JValue> {
    private Adapter<Json.JValue> runtime = new ImmutableJsonRuntime(new JacksonStreamingParser());

    @Override
    protected Adapter<Json.JValue> runtime() {
        return runtime;
    }
}
