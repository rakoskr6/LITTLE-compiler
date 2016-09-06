import classes/MicroLexer.*; // idk - something like this

public class Micro {

	public static void main(String []args) {
		System.out.println("Starting program (sanity check)");

		ANTLRStringStream input = new ANTLRStringStream("hello parrt");
		MicroLexer lexer = new MicroLexer(input);


	}
}