package com.geecommerce.navigation.web.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Directive;
import com.geecommerce.navigation.NavigationConstant;
import com.geecommerce.navigation.model.NavigationItem;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

@Directive("nav_is_active")
public class IsActiveDirective implements TemplateDirectiveModel {
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        TemplateModel pItem = (TemplateModel) params.get("item");
        SimpleScalar pVar = (SimpleScalar) params.get("var");

        App app = App.get();
        List<NavigationItem> navItems = app.registryGet(NavigationConstant.NAV_ITEMS);
        List<Id> navItemIds = app.registryGet(NavigationConstant.NAV_ITEM_IDS);

        boolean isActive = false;
        String var = null;

        if (pVar != null)
            var = pVar.getAsString();

        if (pItem != null && navItems != null) {
            Object navItem = null;

            if (pItem instanceof SimpleScalar) {
                navItem = ((SimpleScalar) pItem).getAsString();
            } else if (pItem instanceof SimpleNumber) {
                navItem = Id.valueOf(((SimpleNumber) pItem).getAsNumber());
            } else if (pItem instanceof StringModel) {
                navItem = ((StringModel) pItem).getWrappedObject();
            } else {
                navItem = DeepUnwrap.unwrap(pItem);
            }

            if (navItem instanceof String) {
                for (NavigationItem navigationItem : navItems) {
                    if ((navigationItem.getLabel() != null
                        && Str.trimEqualsIgnoreCase((String) navItem, navigationItem.getLabel().str()))
                        || Str.trimEqualsIgnoreCase((String) navItem, navigationItem.getDisplayURI())) {
                        isActive = true;
                        break;
                    }
                }
            } else if (navItem instanceof Id) {
                isActive = navItemIds.contains(navItem);
            } else if (navItem instanceof NavigationItem) {
                isActive = navItemIds.contains(((NavigationItem) navItem).getId());
            }
        }

        if (isActive && Str.isEmpty(var)) {
            body.render(env.getOut());
        } else {
            env.setVariable(var, DefaultObjectWrapper.getDefaultInstance().wrap(isActive));
        }
    }
}
