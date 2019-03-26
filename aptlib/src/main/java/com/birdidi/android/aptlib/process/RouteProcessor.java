package com.birdidi.android.aptlib.process;

import com.birdidi.android.aptlib.RouteService;
import com.birdidi.android.aptlib.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
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
public class RouteProcessor extends AbstractProcessor {

    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Route.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        HashMap<String, String> map = new HashMap<String, String>();
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if(elements != null && !elements.isEmpty()){
            for (Element e : elements) {
                if (e instanceof TypeElement) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "route : " + e);
                    TypeElement typeElement = (TypeElement) e;
                    map.put(e.getAnnotation(Route.class).value(), typeElement.getQualifiedName().toString());
                }
            }
            genClass(map);
            return true;
        }
        return false;
    }

    private void genClass(Map<String, String> map) {
        messager.printMessage(Diagnostic.Kind.NOTE, "route : " + map);
        TypeName TYPE_MAP = ParameterizedTypeName.get(HashMap.class, String.class, String.class);

        FieldSpec sRouteMapSpec = FieldSpec.builder(TYPE_MAP, "sRouteMap")
                .addModifiers(Modifier.STATIC, Modifier.FINAL)
                .initializer(CodeBlock.builder()
                        .addStatement("new $T()", HashMap.class)
                        .build())
                .build();

        CodeBlock.Builder staticCodeBlock = CodeBlock.builder();
        for (Map.Entry<String, String> entry: map.entrySet()) {
            staticCodeBlock.add("$N.put($S, $S);", "sRouteMap", entry.getKey(), entry.getValue());
        }

        MethodSpec navigateSpec = MethodSpec.methodBuilder("navigate")
                .addParameter(ClassName.get("android.app", "Activity"), "activity")
                .addParameter(ClassName.get(String.class), "path")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addStatement("String className = $N.get($N)", "sRouteMap", "path")
                .addCode(CodeBlock.builder()
                        .beginControlFlow("try")
                        .beginControlFlow("if ($N != null && $N.length() > 0)", "className", "className")
                        .add("$T intent = new $T();", ClassName.get("android.content", "Intent"), ClassName.get("android.content", "Intent"))
                        .add("intent.setClass($N, Class.forName($N));", "activity", "className")
                        .add("activity.startActivity(intent);")
                        .endControlFlow()
                        .endControlFlow()
                        .beginControlFlow("catch ($T e)", Exception.class)
                        .endControlFlow()
                        .build())
                .build();

        TypeSpec router = TypeSpec.classBuilder("Router")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(navigateSpec)
                .addStaticBlock(staticCodeBlock.build())
                .addField(sRouteMapSpec)
                .build();

        JavaFile javaFile = JavaFile.builder("com.birdidi.android.myapt", router).build();
        Writer writer = null;
        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile("com.birdidi.android.myapt.Router");
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
}
