package com.geecommerce.core.payment;

import java.util.Map;

import com.geecommerce.core.Constant;
import com.geecommerce.core.utils.Filenames;

public abstract class AbstractPaymentMethod {
    public abstract int getSortIndex();

    public abstract String getProvider();

    public abstract String getCode();

    public abstract String getLabel();

    public abstract String getName();

    public abstract String getFormFieldPrefix();

    public abstract boolean isFormDataValid(Map<String, Object> formData);

    public abstract PaymentResponse processPayment(Map<String, Object> formData, Object... data);

    public abstract PaymentResponse authorizePayment(Map<String, Object> formData, Object... data);

    public abstract PaymentResponse refundPayment(Map<String, Object> formData, Object... data);

    public abstract PaymentResponse partiallyRefundPayment(Map<String, Object> formData, Object... data);

    public abstract PaymentResponse capturePayment(Map<String, Object> formData, Object... data);

    public abstract PaymentResponse voidPaymentPayment(Map<String, Object> formData, Object... data);

    public abstract boolean supportAuthorization();

    public abstract boolean supportRefund();

    public abstract boolean supportPartiallyRefund();

    public abstract boolean supportCapture();

    public abstract boolean supportVoid();

    public abstract boolean isEnabled();

    public double getRate() {
        return 0.0;
    };

    public String getFrontendFormPath() {
        StringBuilder sb = new StringBuilder();

        sb.append(Constant.PAYMENT_METHOD_FRONTEND_FORMS_BASE_PATH).append("/")
            .append(Filenames.ensureSafeName(getProvider(), true)).append("/")
            .append(Filenames.ensureSafeName(getName(), true)).append("/")
            .append(Constant.PAYMENT_METHOD_FRONTEND_FORMS_FILE_NAME);

        return sb.toString();
    }
}
