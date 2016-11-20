package com.geecommerce.core.service;

public class QueryMetadata {
    private final Long count;

    public QueryMetadata(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long count;

        public Builder count(Long count) {
            this.count = count;
            return this;
        }

        public QueryMetadata build() {
            return new QueryMetadata(count);
        }
    }
}
