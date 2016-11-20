package com.geecommerce.core.proxy;

public class ProxyPath {
    private String vendorName = null;
    private String moduleName = null;
    private String className = null;
    private String methodName = null;

    private static final char COLON = ':';
    private static final String ARROW = "->";
    private static final String PARENTHESIS = "()";

    public ProxyPath(String path) {
        int colonPos1 = path.indexOf(COLON);
        int colonPos2 = path.lastIndexOf(COLON);
        int arrowPos = path.indexOf(ARROW);
        int parenthesisPos = path.indexOf(PARENTHESIS);

        if (colonPos1 != -1 && colonPos2 != -1 && colonPos1 != colonPos2) {
            vendorName = path.substring(0, colonPos1);
            moduleName = path.substring(colonPos1 + 1, colonPos2);
            className = path.substring(colonPos2 + 1, (arrowPos == -1 ? path.length() : arrowPos));
        } else {
            moduleName = path.substring(0, colonPos1);
            className = path.substring(colonPos1 + 1, (arrowPos == -1 ? path.length() : arrowPos));
        }

        if (arrowPos != -1)
            methodName = path.substring(arrowPos + 2, parenthesisPos);
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "ProxyPath [vendorName=" + vendorName + ", moduleName=" + moduleName + ", className=" + className
            + ", methodName=" + methodName + "]";
    }
}
