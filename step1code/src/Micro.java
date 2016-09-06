import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


public class Micro {

	public static void main(String []args) throws Exception {
		System.out.println("Argument: " + args[0]);

		ANTLRFileStream fid = new ANTLRFileStream(args[0]);
		MicroLexer lexer = new MicroLexer(fid);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		System.out.println(tokens.getText());

	}
}