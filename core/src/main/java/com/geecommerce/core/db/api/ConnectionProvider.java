package com.geecommerce.core.db.api;

import java.util.Map;

public interface ConnectionProvider {
    public String group();

    public void init(Map<String, String> properties);

    public Object provide();

    public void close();

    public void destroy();
}
