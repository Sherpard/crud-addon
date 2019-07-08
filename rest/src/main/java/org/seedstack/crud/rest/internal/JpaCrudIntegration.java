/**
 * 
 */
package org.seedstack.crud.rest.internal;

import java.util.List;

import org.seedstack.crud.rest.CreateResource;
import org.seedstack.crud.rest.DeleteResource;
import org.seedstack.crud.rest.ReadResource;
import org.seedstack.crud.rest.UpdateResource;
import org.seedstack.shed.ClassLoaders;
import org.seedstack.shed.reflect.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class JpaCrudIntegration {

    public static final String JPA_UNIT_ANNOTATION = "org.seedstack.jpa.JpaUnit";

    private static final boolean JPA_AVAILABLE = Classes.optional(JPA_UNIT_ANNOTATION).isPresent();

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaCrudIntegration.class);

    private final ClassPool cp;
    private final ClassLoader classLoader;

    private JpaCrudIntegration() {
        classLoader = ClassLoaders.findMostCompleteClassLoader(JpaCrudIntegration.class);
        cp = new ClassPool(false);
        cp.appendClassPath(new LoaderClassPath(classLoader));

    }

    private void enable() {

        List<Class<?>> classes = Lists.newArrayList(ReadResource.class, UpdateResource.class,
                DeleteResource.class, CreateResource.class);
        try {
            for (Class<?> resource : classes) {
                CtClass ctResource = cp.get(resource.getName());
                addSupportAtMethods(ctResource);

                showClass(cp.get(getResourceName(resource.getName())));

            }

        } catch (NotFoundException | CannotCompileException | ClassNotFoundException e) {
            LOGGER.warn("Could not configure JPA Support", e);
        }
    }

    public static String getResourceName(Class<?> clazz) {
        return getResourceName(clazz.getName());
    }

    private static String getResourceName(String baseName) {
        if (JPA_AVAILABLE) {
            return String.format("%s$JPA", baseName);
        }
        return baseName;
    }

    public static void attemp() {
        if (JPA_AVAILABLE) {
            LOGGER.debug("JPA Plugin detected, Adding support for generated classes");
            new JpaCrudIntegration().enable();
        } else {
            LOGGER.debug("JPA Plugin not detected, Skipping Jpa Support");
        }
    }

    private static void addAnnotation(CtClass clazz, CtMethod method, String annotationName,
            String value) {
        ConstPool constPool = clazz.getClassFile().getConstPool();
        MethodInfo methodInfo = method.getMethodInfo();

        AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo
                .getAttribute(AnnotationsAttribute.visibleTag);

        if (attr == null) {
            attr = new AnnotationsAttribute(constPool,
                    AnnotationsAttribute.visibleTag);
        }
        Annotation annot = new Annotation(annotationName, constPool);
        if (!value.isEmpty()) {
            annot.addMemberValue("value", new StringMemberValue(value, constPool));
        }
        attr.addAnnotation(annot);

    }

    private void addSupportAtMethods(CtClass resource) throws CannotCompileException {

        for (CtMethod method : resource.getDeclaredMethods()) {
            // TODO: configurable value
            addAnnotation(resource, method, JPA_UNIT_ANNOTATION, "");
        }

        resource.setName(getResourceName(resource.getName()));
        resource.toClass(
                classLoader,
                CrudResourceGenerator.class.getProtectionDomain());

    }

    private static void showClass(CtClass ctClass) throws ClassNotFoundException {
        // Methods
        CtMethod[] methods = ctClass.getDeclaredMethods();

        LOGGER.debug("Class: {}", ctClass.getName());
        for (CtMethod method : methods) {
            Object[] annotations = method.getAnnotations();

            for (Object annotation : annotations) {
                LOGGER.debug("Method {} annotated with: {}", method.getName(), annotation);
            }

        }

    }

}
