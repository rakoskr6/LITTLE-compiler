import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.String;
import java.io.*;

// DONE
	// I have added listeners that get us most of the needed info for declarations and scope info

// TODO
	// Create a symbol table structure.
	// A new symbol table will be created for each scope then appended to a list
	// When a variable is declared, it will be added to the last symbol table in the list (ie. the current scope)
    // When done use Java sets to ensure no duplicate variables within same scope

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
	private int blockCounter;

    public AntlrGlobalListener() {
    	this.blockCounter = 1;
    }


    /** Scope stuff **/
    @Override
    public void enterPgm_body(MicroParser.Pgm_bodyContext ctx) {
        System.out.println("GLOBAL");

    }

    @Override 
    public void enterFunc_decl(MicroParser.Func_declContext ctx) { 
    	// Create new symbol table with name ctx.getChild(2)
    	System.out.println("\n" + ctx.getChild(2).getText());

    }

    @Override 
    public void enterIf_stmt(MicroParser.If_stmtContext ctx) { 
    	// Create new symbol table with blockCounter
    	System.out.println("\n" + blockCounter++);

    }

    @Override 
    public void enterElse_part(MicroParser.Else_partContext ctx) { 
    	// Create new symbol table with blockCounter
    	if (!ctx.getText().isEmpty()) {
	    	System.out.println("\n" + blockCounter++);
    	}	


    }

    @Override 
    public void enterDo_while_stmt(MicroParser.Do_while_stmtContext ctx) { 
    	// Create new symbol table with blockCounter
    	System.out.println("\n" + blockCounter++);
    }
 


    /** Variable declarations **/
    @Override
    public void enterString_decl(MicroParser.String_declContext ctx) {
        System.out.print(ctx.getChild(1).getText() + " ");
        System.out.println(ctx.getChild(3).getText());
    }

	@Override 
	public void enterVar_decl(MicroParser.Var_declContext ctx) { 
        System.out.println(ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
        // Will need to parse beginning of variable to determine if int or float. Then will split on commas to get all variable names
        // (this gross technique may be needed in order to acutally get all variables for declarations like INT a,b,c)

    }

	@Override 
	public void enterParam_decl(MicroParser.Param_declContext ctx) { 
        System.out.println(ctx.getChild(0).getText() + " " + ctx.getChild(1).getText());
	}





}

class SymbolTable {
	public void SymbolTable() {


	}

}
