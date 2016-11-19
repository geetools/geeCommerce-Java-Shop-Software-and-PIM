package com.geecommerce.catalog.product.service;

public interface ContactFormService {
    public void sendSupportMail(String questionerEmail, String question, String article, String fullName);
}
