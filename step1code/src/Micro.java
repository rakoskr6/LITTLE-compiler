import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;


public class Micro {

	public static void main(String []args) throws Exception {
		ANTLRFileStream fid = new ANTLRFileStream(args[0]);
		MicroLexer lexer = new MicroLexer(fid);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		System.out.println(tokenStream.getText());
		List<Token> tokenList = tokenStream.getTokens();

		for (int i = 0; i < tokenList.size(); i++) {
			System.out.println("Token Type: " + tokenList.get(i).getType());
			System.out.println("Value: " + tokenList.get(i).getText());
		}

	}
}