package com.geecommerce.core.type.decorators;

import com.geecommerce.core.Char;

public class DefaultIncrementIdDecorator implements IncrementIdDecorator {
    @Override
    public String decorate(String incrementId) {
        return new StringBuilder(incrementId.substring(0, 1)).append(Char.MINUS).append(incrementId.substring(1, 3))
            .append(Char.MINUS).append(incrementId.substring(3)).toString();
    }
}
