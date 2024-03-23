package org.example;
import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@AutoService(Processor.class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EasyExcelProcessor extends AbstractProcessor {




    @Override
    public Set<String> getSupportedAnnotationTypes() {

        return Sets.newHashSet("com.wq.EasyExcelConveter"
             ,   "org.gradle.annotation.processing.aggregating"
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        log("process start1====");

        //遍历所有  本注解处理器要求的注解
        for(TypeElement annotationElement : annotations) {
            log("for annotations");
            //从roundEnv中获取持有注解的所有classElement
            for (Element classElement : roundEnv.getElementsAnnotatedWith(annotationElement)) {


                if (false ==classElement.getKind().isClass()) {
                    continue;
                }

                log("\n\nclass:"+classElement.getSimpleName());
                List<? extends Element> fields = classElement.getEnclosedElements().stream()
                        .filter(c -> c.getKind().equals(ElementKind.FIELD))
                        .collect(Collectors.toList());

                //  writeFields(classElement,fields);
                // write(classElement.getSimpleName(),classElement);
                createFile(classElement.getSimpleName());
            }
        }



        return true;
    }

    private  void log(String text){
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                text, null);
    }

    private void writeFields(Element ele, List<? extends Element> fields) {

        Filer filer = processingEnv.getFiler();
        try {
          FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", ele.getSimpleName()+".txt",ele);
          //  FileObject fileObject = filer.createSourceFile( "META_INF/"+simpleName+".txt");



           log("url: " + fileObject.toUri());

//            try ( PrintWriter printWriter = new PrintWriter(
//                    new OutputStreamWriter(fileObject.openOutputStream(), StandardCharsets.UTF_8)
//            )
//            ) {
//
//                fields.forEach(c->{
//                    log(" field:"+c.getSimpleName());
//                    printWriter.println(c.getSimpleName());
//                });
//
//                if(printWriter.checkError()){
//                    throw new IOException(" error writing to the file");
//                }
//            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(Name simpleName, Element classElement){

        Writer writer = null;
        Filer filer = processingEnv.getFiler();
        JavaFileObject sourceFile = null;
        try {
            sourceFile = filer.createSourceFile(simpleName+"java",classElement);
            //  sourceFile = filer.createSourceFile(simpleName+"java");
            writer = sourceFile.openWriter();
            StringBuilder builder = new StringBuilder()
                    .append("package com.bert.annotations;\n")
                    .append(" class " + simpleName +"Test {\n")
                    .append("private int age;")
                    .append("\n")
                    .append("private void getAge() {}\n")
                    .append("}\n");

            writer.write(builder.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not write source for....");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void createFile(Name simpleName){
        String classDir ="D:/code/incap/processorDemo/build/generated/source/kapt/main/com/myq/";

        if (false == (new File(classDir)).exists()) {
            ( new File(classDir)).mkdirs();
        }

        String filePath = classDir+simpleName+"Test.java";
        File javaFile = new File(filePath);
        if (javaFile.exists()) {
            javaFile.delete();
        }

        // 创建 FileWriter 对象
        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath);
            // 写入字符串
            StringBuilder builder = new StringBuilder()
                    .append("package com.bert.annotations;\n")
                    .append(" class " + simpleName +"Test {\n")
                    .append("private int age;")
                    .append("\n")
                    .append("private void getAge() {}\n")
                    .append("}\n");
            writer.write(builder.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(writer!=null){
                // 关闭 FileWriter 对象
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }




    }


}
