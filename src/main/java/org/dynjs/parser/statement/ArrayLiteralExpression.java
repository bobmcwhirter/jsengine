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

import java.util.List;

import me.qmx.jitescript.CodeBlock;

import org.antlr.runtime.tree.Tree;
import org.dynjs.parser.Statement;
import org.dynjs.runtime.DynArray;

public class ArrayLiteralExpression extends AbstractExpression {

    private final List<Expression> exprs;

    public ArrayLiteralExpression(final Tree tree, final List<Expression> exprs) {
        super( tree );
        this.exprs = exprs;
    }

    @Override
    public CodeBlock getCodeBlock() {
        CodeBlock codeBlock = new CodeBlock() {
            {
                newobj( p( DynArray.class ) );
                dup();
                pushInt( exprs.size() );
                invokespecial( p( DynArray.class ), "<init>", sig( void.class, int.class ) );
                astore( 4 );
            }
        };
        Statement[] statements = exprs.toArray( new Statement[] {} );
        for (int i = 0; i < statements.length; i++) {
            Statement statement = statements[i];
            codeBlock = retrieveArrayReference( i, statement, codeBlock );
        }
        return codeBlock.aload( 4 );
    }

    private CodeBlock retrieveArrayReference(final int stackReference, final Statement statement,
            final CodeBlock codeBlock) {
        return new CodeBlock() {
            {
                append( codeBlock );
                aload( 4 );
                pushInt( stackReference );
                append( statement.getCodeBlock() );
                invokevirtual( p( DynArray.class ), "set", sig( void.class, int.class, Object.class ) );
            }
        };
    }
}
