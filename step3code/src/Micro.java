import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.String;
import java.io.*;


public class Micro {

	public static void main(String []args) throws Exception {
		ANTLRFileStream fid = new ANTLRFileStream(args[0]);
		MicroLexer lexer = new MicroLexer(fid);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		MicroParser parser = new MicroParser(tokenStream);
		

	}

}

class AntlrGlobalListener extends MicroBaseListener {
 
    @Override
    public void enterProgram(MicroParser.ProgramContext ctx) {
        System.out.println(ctx.getText());
    }
 
}
