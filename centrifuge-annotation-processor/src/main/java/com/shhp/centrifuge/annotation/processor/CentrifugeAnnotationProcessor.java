/**
 * Copyright 2017 shhp

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.shhp.centrifuge.annotation.processor;

import com.shhp.centrifuge.annotation.Centrifuge;
import com.sun.source.util.Trees;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * The annotation processor.
 */

@SupportedAnnotationTypes("com.shhp.centrifuge.annotation.Centrifuge")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CentrifugeAnnotationProcessor extends AbstractProcessor {

    private FileObject outputFile;
    private Writer outputWriter;
    private Trees mTrees;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTrees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        BufferedWriter bufferedWriter = null;
        mMessager = processingEnv.getMessager();
        for (TypeElement element : annotations) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, "annotation:"+element.getQualifiedName());
        }
        try {
            if (outputFile == null) {
                outputFile = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "centrifuge", "Centrifuge");
                outputWriter = outputFile.openWriter();
            }
            mMessager.printMessage(Diagnostic.Kind.NOTE, "file location:"+outputFile.toUri());
            bufferedWriter = new BufferedWriter(outputWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : roundEnv.getElementsAnnotatedWith(Centrifuge.class)) {
            String identifier = "// " + getElementId(element);
            stringBuilder.append(identifier).append("\n").append(getElementSourceCode(element)).append("\n\n");
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, identifier);
        }

        if (bufferedWriter != null) {
            try {
                bufferedWriter.append(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * Return a string which represents the id of a given element.<br/><br/>
     * For a class element, the id is the full name of the class. <br/>
     * For a method element, the id is <em>{full name of the class}#{method name}({parameters})</em>. <br/>
     * For a constructor element, the id is <em>{full name of the class}#&lt;init&gt;({parameters})</em>. <br/>
     * @param element
     * @return
     */
    private String getElementId(Element element) {
        ElementKind elementKind = element.getKind();
        if (elementKind == ElementKind.CLASS) {
            return ((TypeElement)element).getQualifiedName().toString();
        } else if (elementKind == ElementKind.CONSTRUCTOR
                || elementKind == ElementKind.METHOD) {
            StringBuilder builder = new StringBuilder();
            TypeElement parent = (TypeElement) element.getEnclosingElement();
            ExecutableElement thisElement = (ExecutableElement) element;
            builder.append(parent.getQualifiedName()).append("#").append(thisElement.getSimpleName()).append("(");
            for (VariableElement parameter : thisElement.getParameters()) {
                builder.append(parameter.asType()).append(" ").append(parameter.getSimpleName()).append(",");
            }
            builder.append(")");
            return builder.toString();
        }
        return element.getSimpleName().toString();
    }

    /**
     * Return the associated source code of the given element.<br/><br/>
     * For a class element, the source code is all the static blocks within the class. <br/>
     * For a method or constructor element, the source code is the method body. <br/>
     * @param element
     * @return
     */
    private String getElementSourceCode(Element element) {
        if (element.getKind() == ElementKind.CLASS) {
            ClassScanner scanner = new ClassScanner();
            return scanner.scan(element, mTrees);
        } else if (element.getKind() == ElementKind.CONSTRUCTOR
                || element.getKind() == ElementKind.METHOD) {
            MethodScanner scanner = new MethodScanner();
            return scanner.scan(element, mTrees);
        }
        return element.getSimpleName().toString();
    }
}
