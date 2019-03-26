package com.birdidi.android.aptlib.process;

import com.birdidi.android.aptlib.annotation.InjectView;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * @author xuyu.chen
 * @date 2019/03/25
 * @email xuyu.chen@ucarinc.com
 * @desc
 */
public class InjectProcessor extends AbstractProcessor {

    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(InjectView.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Map<Entry, List<Element>> dict = new HashMap<>();

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(InjectView.class);
        for (Element e : elements) {
            Element enclosingElement = e.getEnclosingElement();
            if (enclosingElement instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) enclosingElement;
                final String clName = typeElement.getQualifiedName().toString();
                messager.printMessage(Diagnostic.Kind.NOTE, clName);
                String packageName = clName.substring(0, clName.lastIndexOf("."));
                messager.printMessage(Diagnostic.Kind.NOTE, packageName);
                String className = clName.substring(clName.lastIndexOf(".") + 1) + "_InjectView";
                messager.printMessage(Diagnostic.Kind.NOTE, className);
                Entry entry = new Entry();
                entry.className = className;
                entry.packageName = packageName;
                entry.typeElement = typeElement;
                entry.element = e;

                List<Element> list = dict.get(entry);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(e);

                dict.put(entry, list);
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "birdidi : " + e);
        }

        for (Map.Entry<Entry, List<Element>> entry : dict.entrySet()) {
            Entry key = entry.getKey();
            genClass(key.typeElement, key.packageName, key.className, entry.getValue());
        }
        return true;
    }



    private void genClass(TypeElement classElement, String packageName, String className, List<Element> elements) {

        Writer writer = null;

        try {

            TypeName activityTypeName = /*ClassName.bestGuess("android.app.Activity");*/TypeName.get(classElement.asType());

            FieldSpec fieldSpec = FieldSpec.builder(activityTypeName, "view")
                    .addModifiers(Modifier.PROTECTED)
                    .build();


            MethodSpec constructorMethod = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(activityTypeName, "view")
                    .addStatement("this.$N = $N", "view", "view")
                    .build();

            MethodSpec.Builder bindMethodBuilder = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC);
            for (Element e : elements) {
                bindMethodBuilder.addStatement("this.view.$N = ($N)this.view.findViewById(" + e.getAnnotation(InjectView.class).value() + ")", e.getSimpleName(), e.asType().toString());
            }
            MethodSpec bindMethod = bindMethodBuilder.build();

            TypeSpec bindClass = TypeSpec.classBuilder(className)
                    .addField(fieldSpec)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(constructorMethod)
                    .addMethod(bindMethod)
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, bindClass).build();
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(new StringBuilder(packageName).append(".").append(className).toString());
            writer = javaFileObject.openWriter();
            javaFile.writeTo(writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Entry {
        public TypeElement typeElement;
        public String packageName;
        public String className;
        public Element element;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) o;
            return (packageName.equals(entry.packageName)) && className.equals(entry.className);
        }

        @Override
        public int hashCode() {
            return packageName.hashCode() + className.hashCode();
        }
    }
}
