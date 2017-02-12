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

import com.sun.source.tree.BlockTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * Created by herculewang on 16/9/2.
 */
public class ClassScanner extends TreePathScanner<String, Trees> {

    private List<BlockTree> mBlockTreeList = new ArrayList<>();

    public String scan(Element classElement, Trees trees) {
        if (classElement != null
                && classElement.getKind() == ElementKind.CLASS) {
            scan(trees.getPath(classElement), trees);
            StringBuilder builder = new StringBuilder();
            for (BlockTree blockTree : mBlockTreeList) {
                builder.append(blockTree).append("\n\n");
            }
            return builder.toString();
        }
        return "";
    }

    @Override
    public String visitBlock(BlockTree node, Trees trees) {
        if (node.isStatic()) {
            mBlockTreeList.add(node);
        }
        return super.visitBlock(node, trees);
    }
}
