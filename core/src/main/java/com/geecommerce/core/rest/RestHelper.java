package com.geecommerce.core.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import org.jactiveresource.Inflector;

import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.util.DateTimes;

@Profile
public class RestHelper {
    private static final List<String> booleanValues = new ArrayList<>();

    static {
        booleanValues.add("1");
        booleanValues.add("0");
        booleanValues.add("true");
        booleanValues.add("false");
    }

    public static <T extends Model> String getPluralName(T model) {
        if (model == null)
            return null;

        return getPluralName(model.getClass());
    }

    public static <T extends Model> String getPluralName(List<T> models) {
        if (models == null || models.size() == 0)
            return null;

        return getPluralName(models.get(0).getClass());
    }

    public static <T extends Model> String getPluralName(Class<T> model) {
        if (model == null)
            return null;

        String name = getName(model);

        return name == null ? null
            : Inflector.pluralize(
                new StringBuilder(name.substring(0, 1).toLowerCase()).append(name.substring(1)).toString());
    }

    public static <T extends Model> String getName(T model) {
        if (model == null)
            return null;

        return getName(model.getClass());
    }

    public static <T extends Model> String getName(Class<T> model) {
        if (model == null)
            return null;

        String name = null;

        XmlRootElement xmlRootElement = model.getAnnotation(XmlRootElement.class);

        if (xmlRootElement != null) {
            name = xmlRootElement.name();
        }

        if (name == null) {
            Class<? extends Model> modelInterface = Reflect.getModelInterface(model);

            if (modelInterface != null) {
                name = modelInterface.getSimpleName();

                if (name != null && name.length() > 0)
                    name = name.substring(0, 1).toLowerCase() + name.substring(1);
            }

            if (name == null) {
                name = model.getClass().getSimpleName();
                name = name.replaceFirst("Default", "");
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
            }
        }

        return name;
    }

    public static Object guessType(String value) {
        if (value == null)
            return null;

        Object ret = null;

        try {
            ret = Integer.parseInt(value);
        } catch (Throwable t) {
        }

        if (ret == null) {
            try {
                ret = Long.parseLong(value);
            } catch (Throwable t) {
            }
        }

        if (ret == null) {
            try {
                ret = Float.parseFloat(value);
            } catch (Throwable t) {
            }
        }

        if (ret == null) {
            try {
                ret = Double.parseDouble(value);
            } catch (Throwable t) {
            }
        }

        if (ret == null) {
            try {
                if (booleanValues.contains(value.trim().toLowerCase())) {
                    ret = Boolean.parseBoolean(value);
                }
            } catch (Throwable t) {
            }
        }

        if (ret == null) {
            try {
                ret = DateTimes.parseDate(value);
            } catch (Throwable t) {
            }
        }

        if (ret == null) {
            try {
                ret = UUID.fromString(value);
            } catch (Throwable t) {
            }
        }

        if (ret == null) {
            ret = value;
        }

        return ret;
    }
}
