package com.geecommerce.core.util;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;

public class Explain {
    protected static final String EXPLAIN_ENABLED_KEY = "explain.enabled";
    protected static final String EXPLAIN_LOG_KEY = "explain.log";

    public static void enable() {
        App.get().registryPut(EXPLAIN_ENABLED_KEY, Boolean.TRUE);
    }

    public static void disable() {
        App.get().registryPut(EXPLAIN_ENABLED_KEY, Boolean.FALSE);
    }

    public static boolean isEnabled() {
        Boolean isEnabled = App.get().registryGet(EXPLAIN_ENABLED_KEY);
        return isEnabled == null ? false : isEnabled.booleanValue();
    }

    public static void clear() {
        App.get().registryRemove(EXPLAIN_LOG_KEY);
    }

    public static final void log(String line) {
        log(line, true);
    }

    public static final void log(String line, boolean isAppend) {
        if (!isEnabled())
            return;

        List<String> explainLog = App.get().registryGet(EXPLAIN_LOG_KEY);

        if (explainLog == null) {
            explainLog = new ArrayList<>();
            App.get().registryPut(EXPLAIN_LOG_KEY, explainLog);
        }

        if (!isAppend && explainLog.isEmpty())
            explainLog.clear();

        explainLog.add(line);
    }

    public static final void newLine() {
        if (!isEnabled())
            return;

        log(Str.EMPTY);
    }

    public static final String text() {
        if (!isEnabled())
            return null;

        List<String> explainLog = App.get().registryGet(EXPLAIN_LOG_KEY);

        if (explainLog == null || explainLog.isEmpty())
            return null;

        StringBuilder sb = new StringBuilder();

        for (String line : explainLog) {
            sb.append(line).append(Char.NEWLINE);
        }

        return sb.toString();
    }
}
