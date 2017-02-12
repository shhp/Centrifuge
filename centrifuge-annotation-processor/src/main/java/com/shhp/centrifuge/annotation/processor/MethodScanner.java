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

import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * Created by herculewang on 16/9/1.
 */
public class MethodScanner extends TreePathScanner<String, Trees> {

    private MethodTree mMethodTree;
    private String mMethodName;

    public String scan(Element methodElement, Trees trees) {
        if (methodElement != null
                && (methodElement.getKind() == ElementKind.METHOD || methodElement.getKind() == ElementKind.CONSTRUCTOR)) {
            mMethodName = methodElement.getSimpleName().toString();
            scan(trees.getPath(methodElement), trees);
            return mMethodTree != null ? mMethodTree.getBody().toString() : "";
        }
        return "";
    }

    @Override
    public String visitMethod(MethodTree methodTree, Trees trees) {
        if (mMethodTree == null && mMethodName.equals(methodTree.getName().toString())) {
            mMethodTree = methodTree;
        }
        return super.visitMethod(methodTree, trees);
    }
}
