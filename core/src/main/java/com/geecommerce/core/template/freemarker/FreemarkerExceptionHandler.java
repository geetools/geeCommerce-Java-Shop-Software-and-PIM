package com.geecommerce.core.template.freemarker;

import java.io.IOException;
import java.io.Writer;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerExceptionHandler implements TemplateExceptionHandler {
    /**
     * Only prints errors when the configuration-property
     * 'core/dev/print_errors' is set to true.
     */
    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
        try {
            // Make sure that the error message is not swallowed somewhere
            // unnoticed
            // in dev-mode. This way it is clearly visible in the console.
            if (false /* app.isDevPrintErrorMessages() */) {
                // Print stack trace if we are in debug mode.
                te.printStackTrace();
                // Print message to template if we are in debug mode.
                out.write("[ERROR: " + te.getMessage() + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // throw new TemplateException("Failed to print error message.
            // Cause: " + e, env);
        }
    }
}
