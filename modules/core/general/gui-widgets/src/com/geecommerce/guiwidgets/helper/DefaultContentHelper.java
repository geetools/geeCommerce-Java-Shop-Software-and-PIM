package com.geecommerce.guiwidgets.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.web.api.WidgetContext;

@Helper
public class DefaultContentHelper implements ContentHelper {

    public static List<String> MARGIN_STYLES = Arrays.asList(new String[] { "css_margin_top", "css_margin_bottom", "css_margin_left", "css_margin_right" });

    public static List<String> FONT_STYLES = Arrays.asList(new String[] { "css_font_size", "css_color" });

    public static List<String> BACKGROUND_STYLES = Arrays.asList(new String[] { "css_background_color" });

    public static List<String> DIMENSION_STYLES = Arrays.asList(new String[] { "css_height", "css_width" });

    protected static List<String> PIXEL_POSTFIX = Arrays
        .asList(new String[] { "css_font_size", "css_height", "css_width", "css_margin_top", "css_margin_bottom", "css_margin_left", "css_margin_right" });

    @Override
    public String generateStyle(WidgetContext widgetContext, List<String> styles) {
        if (styles == null) {
            styles = new ArrayList<>();
            styles.addAll(MARGIN_STYLES);
            styles.addAll(FONT_STYLES);
            styles.addAll(BACKGROUND_STYLES);
            styles.addAll(DIMENSION_STYLES);
        }

        String resultStyle = "";
        for (String styleKey : styles) {
            String styleValue = widgetContext.getParam(styleKey);
            if (styleValue != null && !styleValue.isEmpty()) {
                String style = styleKey.substring(4).replace("_", "-");

                if (PIXEL_POSTFIX.contains(styleKey) && !styleValue.contains("%")) {
                    styleValue += "px";
                }

                resultStyle += style + ":" + styleValue + ";";
            }
        }

        return resultStyle;
    }
}
