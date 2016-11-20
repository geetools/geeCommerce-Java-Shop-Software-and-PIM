package com.geecommerce.core.proxy;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geemodule.api.ModuleLoader;

public class Proxy {
    @SuppressWarnings("unchecked")
    public static final ProxyModel sget(String modelPath, Id id) {
        ProxyPath pp = new ProxyPath(modelPath);

        App app = App.get();
        ModuleLoader loader = app.moduleLoader();

        Model m = null;

        try {
            Class<?> modelClass = loader.lookup(pp.getClassName(), pp.getModuleName(), pp.getVendorName());
            ProxyDao dao = app.inject(ProxyDao.class);
            m = dao.sFindById((Class<Model>) modelClass, id);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new ProxyModel(m);
    }

    @SuppressWarnings("unchecked")
    public static final ProxyModel mget(String modelPath, Id id) {
        ProxyPath pp = new ProxyPath(modelPath);

        App app = App.get();
        ModuleLoader loader = app.moduleLoader();

        Model m = null;

        try {
            Class<?> modelClass = loader.lookup(pp.getClassName(), pp.getModuleName(), pp.getVendorName());
            ProxyDao dao = app.inject(ProxyDao.class);
            m = dao.mFindById((Class<Model>) modelClass, id);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new ProxyModel(m);
    }
}
