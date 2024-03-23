

### 影响增量失效几个要素

#### createSourceFile没有element
```agsl
// 没有element，增量编译就会失效
sourceFile = filer.createSourceFile(simpleName+"java");
// 加一个element，就可以增量编译
sourceFile = filer.createSourceFile(simpleName+"java",classElement);

```

### 增量编译”JavacTrees、JavacFiler、JavacProcessingEnvironment“ 变化


#### JavacTrees
```
// 正常编译
javacTrees = JavacTrees.instance(processingEnv);

/*
javacTrees = JavacTrees.instance(processingEnv); //在增量编译的时候就会报错，如下：

Caused by: java.lang.IllegalArgumentException
at com.sun.tools.javac.api.JavacTrees.instance(JavacTrees.java:143)

*/

```


#### JavacFiler
processingEnv.getFiler() 会让增量模式失效
[参考](https://github.com/deepin-community/lombok/blob/7f4d9092e2b6269c06ba7802896fa1890a7274b7/src/core/lombok/javac/apt/LombokProcessor.java#L414
)
```
// 解决方案：javacFiler = getJavacFiler(processingEnv.getFiler());
public JavacFiler getJavacFiler(Object filer) {
    if (filer instanceof JavacFiler) return (JavacFiler) filer;

    // try to find a "delegate" field in the object, and use this to check for a JavacFiler
    for (Class<?> filerClass = filer.getClass(); filerClass != null; filerClass = filerClass.getSuperclass()) {
        Object delegate = tryGetDelegateField(filerClass, filer);
        if (delegate == null) delegate = tryGetProxyDelegateToField(filerClass, filer);
        if (delegate == null) delegate = tryGetFilerField(filerClass, filer);

        if (delegate != null) return getJavacFiler(delegate);
        // delegate field was not found, try on superclass
    }

    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
            "Can't get a JavacFiler from " + filer.getClass().getName() + ". Lombok won't work.");
    return null;
}


```


#### JavacProcessingEnvironment
[参考lombok](https://github.com/projectlombok/lombok/blob/master/src/core/lombok/javac/apt/LombokProcessor.java)

```

//  增量模式时  ((JavacProcessingEnvironment) processingEnv) 会报错
/*
org.gradle.api.internal.tasks.compile.processing.IncrementalProcessingEnvironment 
 cannot be cast to com.sun.tools.javac.processing.JavacProcessingEnvironment
*/

// 解决方案：JavacProcessingEnvironment javacProcessingEnv = getJavacProcessingEnvironment(processingEnv);
public JavacProcessingEnvironment getJavacProcessingEnvironment(ProcessingEnvironment procEnv) {
    final Class<?> procEnvClass = procEnv.getClass();
    if (procEnv.getClass().getName().equals("org.gradle.api.internal.tasks.compile.processing.IncrementalProcessingEnvironment")) {
        try {
            Field field = procEnvClass.getDeclaredField("delegate");
            field.setAccessible(true);
            Object delegate = field.get(procEnv);
            return (JavacProcessingEnvironment) delegate;
        } catch (final Exception e) {
            e.printStackTrace();
            procEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Can't get the delegate of the gradle IncrementalProcessingEnvironment. Lombok won't work.");
        }
    }
    return ( JavacProcessingEnvironment) procEnv;
}
```


### gradle annotationProcessor、apt区别

```kotlin
annotationProcessor("com.google.auto.service:auto-service:1.1.1")
annotationProcessor("net.ltgt.gradle.incap:incap-processor:1.0.0")

kapt("com.google.auto.service:auto-service:1.1.1")
kapt("net.ltgt.gradle.incap:incap-processor:1.0.0")
```

将annotationProcessor换成apt时， 获取ProcessingEnvironment 会报错
```
org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessingEnvironment cannot be cast to com.sun.tools.javac.processing.JavacProcessingEnvironment

```
结论：增量模式的时候尽量使用annotationProcessor(当然你找到了在kapt增量模式下获取ProcessingEnvironment，也开始可以的)



[参考-让Annotation Processor支持增量编译](https://jiyang.site/posts/2020-03-24-%E8%AE%A9annotation-processor-%E6%94%AF%E6%8C%81%E5%A2%9E%E9%87%8F%E7%BC%96%E8%AF%91/)


