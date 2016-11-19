package com.geecommerce.core.script;

import java.util.Map;

public interface GroovyClass {
    public Object execute(Map<String, Object> bindings);
}
