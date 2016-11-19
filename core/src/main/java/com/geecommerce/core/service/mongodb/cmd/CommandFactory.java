package com.geecommerce.core.service.mongodb.cmd;

import java.util.ArrayList;
import java.util.List;

public enum CommandFactory {
    INSTANCE;

    static final Character COLON = ':';

    static final List<Command> commands = new ArrayList<>();

    static {
        commands.add(new CtxObj());
        commands.add(new CtxObjRegexp());
        commands.add(new Attr());
        commands.add(new AttrOption());
        commands.add(new AttrXOption());
        commands.add(new Wildcard());
        commands.add(new Null());
        commands.add(new NotNull());
    }

    public Command get(String key, Object value) {
        if (key == null && value == null)
            return null;

        for (Command c : commands) {
            if (c.isOwner(key, value)) {
                return c;
            }
        }

        return null;
    }
}
