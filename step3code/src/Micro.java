import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.String;
import java.io.*;


public class Micro {

	public static void main(String []args) throws Exception {
		ANTLRFileStream fid = new ANTLRFileStream(args[0]);

        // Generate the lexer
		MicroLexer lexer = new MicroLexer(fid);

        // Get a list of matched tokens
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        // Pass the tokens into the parser
		MicroParser parser = new MicroParser(tokenStream);

        // Specify  our entry point
		MicroParser.ProgramContext programContext = parser.program();

        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        AntlrGlobalListener listener = new AntlrGlobalListener();
        walker.walk(listener, programContext);
	}

}

class AntlrGlobalListener extends MicroBaseListener {
 
    @Override
    public void enterProgram(MicroParser.ProgramContext ctx) {
        System.out.println(ctx.getText());
    }
 
}
