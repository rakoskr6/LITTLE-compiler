import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.String;


public class Micro {

	public static void main(String []args) throws Exception {
		ANTLRFileStream fid = new ANTLRFileStream(args[0]);
		MicroLexer lexer = new MicroLexer(fid);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		//System.out.println(tokenStream.getText());
		//tokenStream.getText();
		

		MicroParser parser = new MicroParser(tokenStream);
		System.out.println(parser.eval().value);


		/*tokenStream.fill();
		List<Token> tokenList = tokenStream.getTokens();

		String[] tokenType = lexer.getTokenNames();

		for (int i = 0; i < tokenList.size(); i++) {
			if (tokenList.get(i).getType() != -1) { // end of file token
				System.out.println("Token Type: " + tokenType[tokenList.get(i).getType()]);
				System.out.println("Value: " + tokenList.get(i).getText());
			}

		}
		*/

	}
}