package com.geecommerce.core.customer.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.customer.account.annotation.LoginMethod;
import com.geemodule.api.ModuleLoader;

public class LoginHelper {
    private static final Logger log = LogManager.getLogger(LoginHelper.class);

    @SuppressWarnings("unchecked")
    public static List<AbstractLoginMethod> locateLoginMethods() {
        long startx = System.currentTimeMillis();

        if (log.isTraceEnabled()) {
            log.trace("ENTER locateLoginMethods()");
        }

        App app = App.get();

        List<AbstractLoginMethod> loginMethods = new ArrayList<>();

        ApplicationContext appCtx = app.context();

        if (appCtx != null) {
            // Find login methods in modules
            ModuleLoader loader = app.moduleLoader();

            Class<AbstractLoginMethod>[] types = (Class<AbstractLoginMethod>[]) loader
                .findAllTypesAnnotatedWith(LoginMethod.class, false);

            for (Class<AbstractLoginMethod> type : types) {
                try {
                    loginMethods.add(type.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            // Sort login methods according to the sort-index
            if (loginMethods != null && loginMethods.size() > 0) {
                Collections.sort(loginMethods, new Comparator<AbstractLoginMethod>() {
                    @Override
                    public int compare(AbstractLoginMethod o1, AbstractLoginMethod o2) {
                        return (o1.getSortIndex() < o2.getSortIndex() ? -1
                            : (o1.getSortIndex() > o2.getSortIndex() ? 1 : 0));
                    }
                });
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("EXIT locateLoginMethods() - " + (System.currentTimeMillis() - startx));
        }

        return loginMethods;
    }

    public static AbstractLoginMethod findLoginMethodByCode(String loginMethodCode) {
        if (loginMethodCode == null)
            return null;

        List<AbstractLoginMethod> loginMethods = locateLoginMethods();

        AbstractLoginMethod foundLoginMethod = null;

        for (AbstractLoginMethod loginMethod : loginMethods) {
            if (loginMethodCode.equals(loginMethod.getCode())) {
                foundLoginMethod = loginMethod;
                break;
            }
        }

        return foundLoginMethod;
    }
}
