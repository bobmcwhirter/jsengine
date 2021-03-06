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

import me.qmx.jitescript.CodeBlock;

import org.antlr.runtime.tree.Tree;
import org.dynjs.compiler.CodeBlockUtils;
import org.dynjs.parser.Statement;
import org.dynjs.runtime.BlockManager;
import org.dynjs.runtime.Completion;
import org.objectweb.asm.tree.LabelNode;

public class DoWhileStatement extends AbstractCompilingStatement implements Statement {

    private final Expression vbool;
    private final Statement vloop;

    public DoWhileStatement(final Tree tree, BlockManager blockManager, final Expression vbool, final Statement vloop) {
        super( tree, blockManager );
        this.vbool = vbool;
        this.vloop = vloop;
    }

    @Override
    public CodeBlock getCodeBlock() {
        return new CodeBlock() {
            {
                LabelNode begin = new LabelNode();
                LabelNode normalTarget = new LabelNode();
                LabelNode breakTarget = new LabelNode();
                LabelNode end = new LabelNode();

                label( begin );
                append( CodeBlockUtils.invokeCompiledStatementBlock( getBlockManager(), "Do", vloop ) );
                // completion(block)
                dup();
                // completion(block) completion(block)
                append( handleCompletion( normalTarget, breakTarget, normalTarget, end, end ) );

                // ----------------------------------------
                // NORMAL
                label( normalTarget );
                // completion(block)

                append( vbool.getCodeBlock() );
                // completion(block) bool
                iffalse( end );
                pop();
                // <EMPTY>
                go_to( begin );

                // ----------------------------------------
                // BREAK
                label( breakTarget );
                // completion(block,BREAK)
                append( convertToNormal() );
                // completion(block,NORMAL)

                // ----------------------------------------
                label( end );
                // completion(block)
                nop();
                // completion(block)
            }
        };
    }
}
