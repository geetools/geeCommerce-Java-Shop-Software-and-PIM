package com.geecommerce.core.elasticsearch.search;

public class FieldValue {
    private final Object raw;
    private final String slug;
    private final String hash;
    private final boolean isOption;

    public FieldValue(Object raw, String slug, String hash, boolean isOption) {
        super();
        this.raw = raw;
        this.slug = slug;
        this.hash = hash;
        this.isOption = isOption;
    }

    public Object getRaw() {
        return raw;
    }

    public String getSlug() {
        return slug;
    }

    public String getHash() {
        return hash;
    }

    public boolean isOption() {
        return isOption;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + (isOption ? 1231 : 1237);
        result = prime * result + ((raw == null) ? 0 : raw.hashCode());
        result = prime * result + ((slug == null) ? 0 : slug.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        FieldValue other = (FieldValue) obj;

        if (hash == null) {
            if (other.hash != null)
                return false;
        } else if (!hash.equals(other.hash))
            return false;

        if (isOption != other.isOption)
            return false;

        if (raw == null) {
            if (other.raw != null)
                return false;
        } else if (!raw.equals(other.raw))
            return false;

        if (slug == null) {
            if (other.slug != null)
                return false;
        } else if (!slug.equals(other.slug))
            return false;

        return true;
    }
}
