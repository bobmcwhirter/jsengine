package org.dynjs.runtime;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.dynjs.Config;
import org.dynjs.compiler.JSCompiler;
import org.dynjs.exception.DynJSException;
import org.dynjs.parser.ES3Lexer;
import org.dynjs.parser.ES3Parser;
import org.dynjs.parser.ES3Walker;
import org.dynjs.parser.Executor;
import org.dynjs.parser.SyntaxError;
import org.dynjs.parser.statement.BlockStatement;

public class JSEngine {

    private Config config;
    private ExecutionContext context;

    public JSEngine() {
        this( new Config() );
    }

    public JSEngine(Config config) {
        this.config = config;
        this.context = ExecutionContext.createGlobalExecutionContext( this );
    }
    
    public Config getConfig() {
        return this.config;
    }
    
    public ExecutionContext getExecutionContext() {
        return this.context;
    }
    
    public Object execute(FileInputStream program, String filename) throws IOException {
        JSCompiler compiler = this.context.getCompiler();
        BlockStatement statements = parseSourceCode( this.context, program, filename );
        JSProgram programObj = compiler.compileProgram( statements );
        Completion completion = this.context.execute( programObj );
        if ( completion.type == Completion.Type.THROW ) {
            throw (DynJSException) completion.value;
        }
        return completion.value;
    }

    public Object execute(String program, String filename, int lineNumber) {
        JSCompiler compiler = this.context.getCompiler();
        BlockStatement statements = parseSourceCode( this.context, program, filename );
        JSProgram programObj = compiler.compileProgram( statements );
        Completion completion = this.context.execute( programObj );
        System.err.println( "completion: " + completion );
        if ( completion.type == Completion.Type.THROW ) {
            throw (DynJSException) completion.value;
        }
        return completion.value;
    }
    
    public Object execute(String program) {
        return execute( program, null, 0);
    }
    
    public Object evaluate(String...code) {
        StringBuffer fullCode = new StringBuffer();
        
        for ( int i = 0 ; i < code.length ; ++i ) {
            fullCode.append( code[i] );
            fullCode.append( "\n" );
        }
        return execute( fullCode.toString(), null, 0 );
    }

    private BlockStatement parseSourceCode(ExecutionContext context, String code, String filename) {
        try {
            final ANTLRStringStream stream = new ANTLRStringStream( code );
            stream.name = filename;
            ES3Lexer lexer = new ES3Lexer( stream );
            return parseSourceCode( context, lexer );
        } catch (RecognitionException e) {
            throw new SyntaxError( e );
        }
    }
    
    private BlockStatement parseSourceCode(ExecutionContext context, InputStream code, String filename) throws IOException {
        try {
            final ANTLRStringStream stream = new ANTLRInputStream( code );
            stream.name = filename;
            ES3Lexer lexer = new ES3Lexer( stream );
            return parseSourceCode( context, lexer );
        } catch (RecognitionException e) {
            throw new SyntaxError( e );
        }
    }

    private BlockStatement parseSourceCode(ExecutionContext context, ES3Lexer lexer) throws RecognitionException, SyntaxError {
        CommonTokenStream stream = new CommonTokenStream( lexer );
        ES3Parser parser = new ES3Parser( stream );
        ES3Parser.program_return program = parser.program();
        List<String> errors = parser.getErrors();
        if (!errors.isEmpty()) {
            throw new SyntaxError( errors );
        }
        CommonTree tree = (CommonTree) program.getTree();
        CommonTreeNodeStream treeNodeStream = new CommonTreeNodeStream( tree );
        treeNodeStream.setTokenStream( stream );
        ES3Walker walker = new ES3Walker( treeNodeStream );

        Executor executor = new Executor();
        executor.setBlockManager( context.getBlockManager() );
        walker.setExecutor( executor );
        walker.program();
        return walker.getResult();
    }

}
