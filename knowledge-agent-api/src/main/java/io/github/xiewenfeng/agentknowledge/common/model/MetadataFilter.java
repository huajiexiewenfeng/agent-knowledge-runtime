package io.github.xiewenfeng.agentknowledge.common.model;

import java.util.Map;

public record MetadataFilter(Map<String, String> values) {
    public static MetadataFilter empty() {
        return new MetadataFilter(Map.of());
    }

    public boolean isEmpty() {
        return values == null || values.isEmpty();
    }

    public Map<String, String> safeValues() {
        return values == null ? Map.of() : values;
    }
}
