package com.geecommerce.catalog.product.form;

public class ContactForm {
    private String questionerEmail;

    private String question;

    private String article;

    private String fullName;

    public String getQuestionerEmail() {
        return questionerEmail;
    }

    public void setQuestionerEmail(String questionerEmail) {
        this.questionerEmail = questionerEmail;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
