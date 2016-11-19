package com.geecommerce.core.web.geemvc.inject;

import com.geecommerce.core.inject.ModuleInjector;
import com.geemvc.inject.InjectorProvider;
import com.google.inject.Injector;

public class ModuleInjectorProvider implements InjectorProvider {

    @Override
    public Injector provide() {
        return ModuleInjector.get();
    }
}
