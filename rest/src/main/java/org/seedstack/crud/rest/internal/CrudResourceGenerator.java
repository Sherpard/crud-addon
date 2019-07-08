/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.crud.rest.internal;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.seedstack.business.assembler.DtoOf;
import org.seedstack.business.domain.AggregateNotFoundException;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.internal.BusinessException;
import org.seedstack.business.internal.utils.BusinessUtils;
import org.seedstack.crud.rest.BaseResource;
import org.seedstack.crud.rest.CreateResource;
import org.seedstack.crud.rest.DeleteResource;
import org.seedstack.crud.rest.ReadResource;
import org.seedstack.crud.rest.RestCrud;
import org.seedstack.crud.rest.UpdateResource;
import org.seedstack.shed.ClassLoaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import javassist.util.proxy.ProxyFactory;

class CrudResourceGenerator {
    private static final String CONSTRUCTOR_TEMPLATE = "super(%s.class,%s.class,%s.class);";
    private static final String GENERATED_CLASS_NAME_TEMPLATE = "org.seedstack.crud.generated."
            + "resource.%sResource";

    private static final Logger LOGGER = LoggerFactory.getLogger(CrudResourceGenerator.class);

    // Regex debug: https://regex101.com/r/8XIg1I/2
    private static final String NAME_SUBSTITUTION_EXPRESSION = "(.*)(dto|representation)$";

    private static final String SUPER_CLASS_CONSTRUCTOR_TEMPLATE = "super(Class.forName(\"%s\")"
            + ",Class.forName(\"%s\")"
            + ",Class.forName(\"%s\"));";

    private final ClassLoader classLoader;

    private final Pattern classNameCleanupPattern;
    private final ClassPool cp;

    CrudResourceGenerator() {
        classLoader = ClassLoaders.findMostCompleteClassLoader(CrudResourceGenerator.class);
        cp = new ClassPool(false);
        cp.appendClassPath(new LoaderClassPath(classLoader));
        classNameCleanupPattern = Pattern.compile(NAME_SUBSTITUTION_EXPRESSION,
                Pattern.CASE_INSENSITIVE);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    Class<?> generate(Class<?> dtoClass) {
        validateClass(dtoClass);
        try {

            // TODO: Allow multi-aggregate Dto mapping
            Class<? extends AggregateRoot> aggregateRootClass = dtoClass.getAnnotation(DtoOf.class)
                    .value()[0];
            RestCrud restAnnotation = dtoClass.getAnnotation(RestCrud.class);
            Class<?> aggregateClassId = BusinessUtils.resolveAggregateIdClass(aggregateRootClass);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Building resource for");
                LOGGER.debug("\tDTO: {}", dtoClass.getSimpleName());
                LOGGER.debug("\tAggregateRoot: {}", aggregateRootClass.getName());
                LOGGER.debug("\tAggregateRootKey: {}", aggregateClassId.getName());
                LOGGER.debug("\tConfiguration: {}", restAnnotation);
            }

            CtClass resource = createClass(dtoClass);
            resource.setModifiers(Modifier.PUBLIC);
            resource.setSuperclass(cp.get(BaseResource.class.getName()));

            prepareImports();
            // Build a constructor that uses BaseResource to define the generated resource
            // class
            CtNewConstructor.make(null, null,
                    String.format(SUPER_CLASS_CONSTRUCTOR_TEMPLATE,
                            aggregateRootClass.getCanonicalName(),
                            aggregateClassId.getCanonicalName(),
                            dtoClass.getCanonicalName()),
                    resource);

            List<ClassType> interfaceClassTypes = new ArrayList<>();

            if (restAnnotation.create()) {
                resource.addInterface(cp
                        .getCtClass(JpaCrudIntegration.getResourceName(CreateResource.class)));
                interfaceClassTypes.add(buildClassType(CreateResource.class,
                        aggregateRootClass,
                        aggregateClassId,
                        dtoClass));
            }

            if (restAnnotation.delete()) {
                resource.addInterface(
                        cp.getCtClass(JpaCrudIntegration.getResourceName(DeleteResource.class)));
                interfaceClassTypes.add(buildClassType(DeleteResource.class,
                        aggregateRootClass,
                        aggregateClassId,
                        dtoClass));
            }

            if (restAnnotation.update()) {
                resource.addInterface(
                        cp.getCtClass(JpaCrudIntegration.getResourceName(UpdateResource.class)));
                interfaceClassTypes.add(buildClassType(UpdateResource.class,
                        aggregateRootClass,
                        aggregateClassId,
                        dtoClass));
            }

            if (restAnnotation.read()) {
                resource.addInterface(
                        cp.getCtClass(JpaCrudIntegration.getResourceName(ReadResource.class)));
                interfaceClassTypes.add(buildClassType(ReadResource.class,
                        aggregateRootClass,
                        aggregateClassId,
                        dtoClass));
            }

            SignatureAttribute.ClassSignature classSignature = new SignatureAttribute.ClassSignature(
                    null,
                    buildClassType(BaseResource.class, aggregateRootClass, aggregateClassId,
                            dtoClass),
                    interfaceClassTypes.toArray(new ClassType[interfaceClassTypes.size()]));
            resource.setGenericSignature(classSignature.encode());

            addPathAnnotation(resource, restAnnotation,
                    generateClassName(dtoClass).replace("Resource", ""));

            resource.addConstructor(createConstructor(resource.getClassFile().getConstPool(),
                    resource,
                    aggregateRootClass,
                    aggregateClassId,
                    dtoClass));

            return resource.toClass(
                    classLoader,
                    CrudResourceGenerator.class.getProtectionDomain());
        } catch (CannotCompileException | NotFoundException ex) {
            ex.printStackTrace();
            throw BusinessException.wrap(ex, CrudRestErrorCode.CRUD_REPOSITORY_GENERATION_FAILED)
                    .put("dtoClass", dtoClass);
        }
    }

    private void addInjectAnnotation(ConstPool constPool, CtConstructor cc) {
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool,
                AnnotationsAttribute.visibleTag);
        attribute.setAnnotation(createAnnotation(constPool, Inject.class));
        cc.getMethodInfo().addAttribute(attribute);
    }

    private void addPathAnnotation(CtClass resource, RestCrud configuration, String resourceName) {

        if (!configuration.value().isEmpty()) {
            resourceName = configuration.value();
        }
        ClassFile ccFile = resource.getClassFile();
        ConstPool constPool = ccFile.getConstPool();
        AnnotationsAttribute attr = createAnnotation(Path.class, resourceName, constPool);
        ccFile.addAttribute(attr);
    }

    private ClassType buildClassType(Class<?> rawClass, Class<?> aggregateRootClass,
            Class<?> aggregateClassId, Class<?> dtoClass) {
        return new ClassType(rawClass.getName(), new SignatureAttribute.TypeArgument[] {
                new SignatureAttribute.TypeArgument(new ClassType(aggregateRootClass.getName())),
                new SignatureAttribute.TypeArgument(new ClassType(aggregateClassId.getName())),
                new SignatureAttribute.TypeArgument(new ClassType(dtoClass.getName()))
        });
    }

    private AnnotationsAttribute createAnnotation(
            Class<? extends java.lang.annotation.Annotation> annotationClazz,
            String value, ConstPool constPool) {

        return this.createAnnotation(annotationClazz.getCanonicalName(), value, constPool);
    }

    private Annotation createAnnotation(ConstPool constPool,
            Class<? extends java.lang.annotation.Annotation> annotationType) {
        return new Annotation(
                annotationType.getName(),
                constPool);
    }

    private AnnotationsAttribute createAnnotation(String annotationClazz, String value,
            ConstPool constPool) {
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool,
                AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation(annotationClazz, constPool);
        annot.addMemberValue("value", new StringMemberValue(value, constPool));
        attr.addAnnotation(annot);
        return attr;
    }

    private CtClass createClass(Class<?> baseDto) {
        return cp.makeClass(generateFullClassName(baseDto));
    }

    @SuppressWarnings("rawtypes")
    private CtConstructor createConstructor(ConstPool constPool, CtClass declaringClass,
            Class<? extends AggregateRoot> aggregateRootClass, Class<?> aggregateClassId,
            Class<?> dtoClass) throws NotFoundException,
            CannotCompileException {
        CtConstructor cc = new CtConstructor(new CtClass[] {}, declaringClass);

        // Define the constructor behavior
        cc.setBody(String.format(CONSTRUCTOR_TEMPLATE,
                aggregateRootClass.getName(),
                aggregateClassId.getName(),
                dtoClass.getName()));
        cc.setModifiers(Modifier.PUBLIC);

        // Add the @Inject annotation to the constructor
        addInjectAnnotation(constPool, cc);

        return cc;
    }

    private String generateClassName(Class<?> baseDto) {
        String resourceName = baseDto.getSimpleName();
        Matcher regexMatcher = classNameCleanupPattern.matcher(resourceName);
        if (regexMatcher.matches() && regexMatcher.groupCount() > 1) {
            resourceName = regexMatcher.group(1);
        }
        return resourceName;
    }

    private String generateFullClassName(Class<?> baseDto) {
        // Adding uniqueness to the class allows the test suite to execute.
        // On a production/development environment should make no difference
        return String.format(GENERATED_CLASS_NAME_TEMPLATE,
                ProxyFactory.nameGenerator.get(generateClassName(baseDto)));
    }

    private void prepareImports() {
        cp.importPackage(AggregateNotFoundException.class.getPackage().getName());
        cp.importPackage(NotFoundException.class.getPackage().getName());

    }

    private void validateClass(Class<?> dtoClass) {
        if (!dtoClass.isAnnotationPresent(DtoOf.class)) {
            throw BusinessException
                    .wrap(new RuntimeException("Annotation DtoOf is not present"),
                            CrudRestErrorCode.DTO_ANNOTATION_NOT_FOUND)
                    .put("dtoClass", dtoClass.getName());

        }
        if (!dtoClass.isAnnotationPresent(RestCrud.class)) {
            throw BusinessException
                    .wrap(new RuntimeException("Annotation RestCrud is not present"),
                            CrudRestErrorCode.REST_CRUD_ANNOTATION_NOT_FOUND)
                    .put("dtoClass", dtoClass.getName());

        }
        if (dtoClass.getAnnotation(DtoOf.class).value().length > 1) {
            throw BusinessException
                    .wrap(new RuntimeException("Tuples are not supported by RestCrud Add-on"),
                            CrudRestErrorCode.TUPLES_NOT_SUPPORTED);
        }
        if (dtoClass.getAnnotation(DtoOf.class).value().length == 0) {
            throw BusinessException
                    .wrap(new RuntimeException("Could not find dto value"),
                            CrudRestErrorCode.TUPLES_NOT_SUPPORTED)
                    .put("dtoname", dtoClass.getName());
        }
    }

}
