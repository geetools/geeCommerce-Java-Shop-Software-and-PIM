package com.geecommerce.core.web.stripes;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.App;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.controller.DefaultActionBeanPropertyBinder;
import net.sourceforge.stripes.controller.ParameterName;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMetadata;

/**
 * This ActionBeanPropertyBinder globally makes sure that only characters
 * allowed within a configured character-set have been posted. This ensures that
 * we do not deal with characters that third party systems may not understand.
 * Whilst we are at it, we also remove any duplicate validation error messages.
 */
public class ActionBeanPropertyBinder extends DefaultActionBeanPropertyBinder {
    @Inject
    protected App app;

    private static final String VALID_INPUT_CHARSET_CONFIG_KEY = "core/web/form/valid_input_charset";

    protected void doPostConversionValidations(ActionBean bean, Map<ParameterName, List<Object>> convertedValues,
        ValidationErrors errors) {
        super.doPostConversionValidations(bean, convertedValues, errors);

        // Do not bother with further checks if validation errors already exist.
        if (errors.size() > 0) {
            removeDuplicates(errors);
            return;
        }

        String validInputCharset = app.cpStr_(VALID_INPUT_CHARSET_CONFIG_KEY);

        // Do not do any checking if no charset has been configured.
        if (validInputCharset != null) {
            Map<String, ValidationMetadata> validationInfos = super.getConfiguration().getValidationMetadataProvider()
                .getValidationMetadata(bean.getClass());

            for (Map.Entry<ParameterName, List<Object>> entry : convertedValues.entrySet()) {
                // Sort out what we need to validate this field
                ParameterName name = entry.getKey();
                List<Object> values = entry.getValue();
                ValidationMetadata validationInfo = validationInfos.get(name.getStrippedName());

                if (values.size() == 0 || validationInfo == null)
                    continue;

                for (Object value : values) {
                    // If the value is a string and a specific input-charset has
                    // been configured,
                    // we make sure that the value is valid for this charset.
                    if (value instanceof String) {
                        String string = (String) value;

                        boolean isValidCharset = Charset.forName(validInputCharset).newEncoder().canEncode(string);

                        if (!isValidCharset) {
                            ValidationError error = new ScopedLocalizableError("converter.charset", "invalidCharset",
                                validInputCharset);
                            error.setFieldValue(String.valueOf(value));
                            errors.add(name.getName(), error);
                        }
                    }
                }
            }
        }

        if (errors.size() > 0)
            removeDuplicates(errors);
    }

    /**
     * Sometimes error messages can be the same even although different fields
     * are involved. Also, if 2 different types of validations occur on one
     * field, then stripes will display them both. We want neither, so here we
     * attempts to remove any duplicates.
     * 
     * @param validationErrors
     */
    protected void removeDuplicates(ValidationErrors validationErrors) {
        List<String> allErrorMessages = new ArrayList<String>();

        if (validationErrors.size() > 0) {
            Set<String> keys = validationErrors.keySet();

            for (String field : keys) {
                List<ValidationError> errors = validationErrors.get(field);
                // Remember errors we want to remove.
                List<ValidationError> errorsToRemove = new ArrayList<>();
                // Remember errors that we want to add in place of the removed
                // ones.
                Set<ValidationError> errorsToAdd = new HashSet<>();

                int count = 0;
                for (ValidationError error : errors) {
                    // If a field has more than one error, we remove all of the
                    // rest. We only want to show one error at
                    // a time per field.
                    if (errors.size() > 0 && count > 0) {
                        errorsToRemove.add(error);
                        continue;
                    }

                    // Set the bean class or strips end up with a NullPointer.
                    // error.setBeanclass(app.getActionBean().getClass()); //
                    // Not needed in Geemvc.

                    String message = error.getMessage(app.getCurrentLocale());

                    // If this message already exists, we remove it from the
                    // list of errors.
                    if (allErrorMessages.contains(message)) {
                        errorsToRemove.add(error);
                        // Errors that we remove need to be replaced with some
                        // empty error or stripes will not
                        // re-populate the input-field.
                        errorsToAdd.add(new SimpleError("", null, error.getFieldValue()));
                    } else {
                        // Remember the error message in the global list so that
                        // we can check for duplicates in other
                        // fields.
                        allErrorMessages.add(message);
                    }

                    count++;
                }

                if (errorsToRemove.size() > 0) {
                    errors.removeAll(errorsToRemove);
                    errors.addAll(errorsToAdd);
                }
            }
        }
    }
}
