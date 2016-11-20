package com.geecommerce.test.interceptor;

public class TestMethodObject {
    private int testInt;
    private String testString = null;

    public TestMethodObject(int testInt, String testString) {
        super();
        this.testInt = testInt;
        this.testString = testString;
    }

    public int getTestInt() {
        return testInt;
    }

    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }
}
