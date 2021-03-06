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
import static me.qmx.jitescript.util.CodegenUtils.*;

import org.antlr.runtime.tree.Tree;

public class NumberLiteralExpression extends AbstractExpression {

    private final String text;
    private final int radix;

    public NumberLiteralExpression(final Tree tree, final String text, final int radix) {
        super( tree );
        this.text = text;
        this.radix = radix;
    }
    
    public String getText() {
        return this.text;
    }

    @Override
    public CodeBlock getCodeBlock() {
        return new CodeBlock() {
            {
                ldc(text);
                bipush(radix);
                invokestatic( p(Integer.class), "valueOf", sig(Integer.class, String.class, int.class));
            }
        };
    }
}
