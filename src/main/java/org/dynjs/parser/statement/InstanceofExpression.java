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

import static me.qmx.jitescript.util.CodegenUtils.*;
import me.qmx.jitescript.CodeBlock;

import org.antlr.runtime.tree.Tree;
import org.dynjs.runtime.JSFunction;
import org.objectweb.asm.tree.LabelNode;

public class InstanceofExpression extends AbstractBinaryExpression {

    public InstanceofExpression(final Tree tree, final Expression l, final Expression r) {
        super( tree, l, r, "instanceof" );
    }

    @Override
    public CodeBlock getCodeBlock() {
        return new CodeBlock() {
            {
                LabelNode typeError = new LabelNode();
                LabelNode end = new LabelNode();

                append( getLhs().getCodeBlock() );
                // obj(lhs)
                append( jsGetValue() );
                // val(lhs)

                append( getRhs().getCodeBlock() );
                // val(lhs) obj(rhs)
                append( jsGetValue() );
                // val(lhs) val(rhs)

                dup();
                // val(lhs) val(rhs) val(rhs)
                instance_of( p( JSFunction.class ) );
                // val(lhs) val(rhs) bool
                iffalse( typeError );
                invokevirtual( p( JSFunction.class ), "hasInstance", sig( boolean.class, Object.class ) );
                go_to( end );

                label( typeError );
                pop();
                pop();
                // FIXME: throw TypeError;

                label( end );
            }
        };
    }

}
