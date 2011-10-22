/**
 *  Copyright 2011 Douglas Campos
 *  Copyright 2011 dynjs contributors
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

import me.qmx.internal.org.objectweb.asm.tree.LabelNode;
import me.qmx.jitescript.CodeBlock;
import org.dynjs.parser.Statement;

import java.util.List;

public class BlockStatement implements Statement {

    private final CodeBlock codeBlock;
    private LabelNode beginLabel = new LabelNode();
    private LabelNode endLabel = new LabelNode();

    public BlockStatement(final List<Statement> blockContent) {
        this.codeBlock = new CodeBlock() {{
            label(beginLabel);
            for (Statement statement : blockContent) {
                append(statement.getCodeBlock());
            }
            label(endLabel);
        }};
    }

    @Override
    public CodeBlock getCodeBlock() {
        return this.codeBlock;
    }

    public LabelNode getBeginLabel() {
        return beginLabel;
    }

    public LabelNode getEndLabel() {
        return endLabel;
    }
}
