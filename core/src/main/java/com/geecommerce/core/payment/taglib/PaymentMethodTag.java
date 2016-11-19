package com.geecommerce.core.payment.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.geecommerce.core.payment.AbstractPaymentMethod;

public class PaymentMethodTag extends SimpleTagSupport {
    private AbstractPaymentMethod paymentMethod = null;

    @Override
    public void doTag() throws JspException, IOException {

    }

    public AbstractPaymentMethod getPaymentMethod() {
	return paymentMethod;
    }

    public void setPaymentMethod(AbstractPaymentMethod paymentMethod) {
	this.paymentMethod = paymentMethod;
    }

}
