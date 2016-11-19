package com.geecommerce.core.instrument;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.ColumnInfo;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.api.Model;

public class ModelTransformer implements ClassFileTransformer {
    private static final String STATIC_FIELD_PREFIX = "__cb_";
    private static final String STRING_CLASS_NAME = "java.lang.String";

    public ModelTransformer() {
	super();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
	System.out.println("1----> " + classBeingRedefined + "  -  " + className + " - " + ("to/test/invokedynamic/TestClass".equals(className)));

	byte[] byteCode = classfileBuffer;

	ClassPool cp = ClassPool.getDefault();
	CtClass modelImplClass = null;
	CtClass modelClass = null;

	try {
	    modelClass = cp.get(Model.class.getName());
	    modelImplClass = cp.makeClass(new ByteArrayInputStream(byteCode));

	    if (modelImplClass == null) {
		System.out.println("CtClass is NULL!");
		return classfileBuffer;
	    }

	    boolean isModel = modelImplClass.hasAnnotation(com.geecommerce.core.service.annotation.Model.class) && modelImplClass.subtypeOf(modelClass);

	    System.out.println("==================> isModel: " + isModel);

	    if (isModel) {
		System.out.println("2----> " + className);

		// addStaticFields(modelImplClass);

		CtMethod m = CtNewMethod.make(buildMethodBody(modelImplClass), modelImplClass);
		modelImplClass.addMethod(m);

		byteCode = modelImplClass.toBytecode();
	    }
	} catch (Throwable t) {
	    t.printStackTrace();
	} finally {
	    if (modelImplClass != null) {
		modelImplClass.detach();
	    }
	}

	return byteCode;
    }

    private void addStaticFields(CtClass modelImplClass) {
	CtField[] fields = modelImplClass.getDeclaredFields();

	if (fields != null && fields.length > 0) {
	    for (CtField f : fields) {
		if (f.hasAnnotation(Column.class)) {
		    try {
			if (!Modifier.isFinal(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
			    ClassPool cp = ClassPool.getDefault();

			    CtField staticField = new CtField(cp.get(STRING_CLASS_NAME), STATIC_FIELD_PREFIX + f.getName(), modelImplClass);
			    staticField.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);

			    System.out.println("----- " + staticField.toString());

			    modelImplClass.addField(staticField, f.getName());
			}
		    } catch (Throwable t) {
			t.printStackTrace();
		    }
		}
	    }

	}

    }

    private String buildMethodBody(CtClass modelImplClass) {
	StringBuilder body = new StringBuilder("public void set(String field, Object value) {\n");

	// String className = modelImplClass.getName();
	// body.append("Class realClass = Class.forName(\"" + className + "\");\n");

	// classInitializerBody.append("Class realSuperClass = Class.forName(\"" + superClassName + "\");\n");

	// body.append("System.out.println(String.valueOf(realClass));\n");

	// body.append("java.util.List<com.geecommerce.core.service.ColumnInfo> columnInfos = com.geecommerce.core.service.Annotations.getColumns(modelClass);");
	// body.append("System.out.println(String.valueOf(columnInfos));\n");

	// System.out.println("### " + className);

	CtField[] fields = modelImplClass.getDeclaredFields();

	if (fields != null && fields.length > 0) {
	    int x = 0;

	    // body.append("switch(field) {\n");

	    for (CtField f : fields) {
		if (f.hasAnnotation(Column.class)) {
		    try {
			if (!Modifier.isFinal(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
			    // body.append("    case ").append(STATIC_FIELD_PREFIX + f.getName()).append(":\n");
			    // body.append("        ").append(buildSetter(f)).append("((").append(f.getType().getName()).append(")").append("value);\n");
			    // body.append("    break;\n");

			    if (x > 0)
				body.append("    else");

			    body.append(" if(field.equals(\"").append(f.getName()).append("\")) {\n");
			    body.append("        ").append(buildSetter(f)).append("((").append(f.getType().getName()).append(")").append("value);\n");
			    body.append("    }\n");

			    x++;
			}
		    } catch (Throwable t) {
			t.printStackTrace();
		    }

		}
	    }

	    // body.append("    }\n");
	}

	body.append("}\n");

	// "public void set(String field, Object value) { System.out.println(\"set(String field, Object value) IN NEW METHOD!! \"); }"

	System.out.println(body);

	return body.toString();
    }

    private String buildSetter(CtField field) {
	String fieldName = field.getName();

	StringBuilder setter = new StringBuilder("set").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));

	return setter.toString();
    }
}