/**
 *  Copyright 2012 Douglas Campos, and individual contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dynjs.parser.statement;

import java.util.List;

import me.qmx.jitescript.CodeBlock;

import org.antlr.runtime.tree.Tree;

public class VariableDeclarationStatement extends AbstractStatement {

    private List<VariableDeclaration> declExprs;

    public VariableDeclarationStatement(final Tree tree, final List<VariableDeclaration> declExprs) {
        super( tree );
        this.declExprs = declExprs;
    }
    
    public List<VariableDeclaration> getVariableDeclarations() {
        return this.declExprs;
    }

    @Override
    public CodeBlock getCodeBlock() {
        return new CodeBlock() {
            {
                for (VariableDeclaration each : declExprs) {
                    append( each.getCodeBlock() );
                    // identifier
                    pop();
                    // <EMPTY>
                }
                append( normalCompletion() );
            }
        };
    }
}
