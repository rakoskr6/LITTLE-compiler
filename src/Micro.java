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
	public List<SymbolTable> allSymbolTables = new ArrayList<SymbolTable>();

    public AntlrGlobalListener() {
      this.blockCounter = 1;
    }


    /****** Scope stuff 
    /****** (creates new symbol tables and adds it to list of all symbol tables) ******/

    @Override
    public void enterPgm_body(MicroParser.Pgm_bodyContext ctx) {
        SymbolTable global = new SymbolTable("GLOBAL");
        this.allSymbolTables.add(global); 
    }

    @Override
    public void exitPgm_body(MicroParser.Pgm_bodyContext ctx) {

        for (int i = 0; i < this.allSymbolTables.size(); i++) {
        	Set<String> set = new HashSet<String>();

    		for (int x = 0; x < this.allSymbolTables.get(i).objectList.size(); x++) {
    			if (set.contains(this.allSymbolTables.get(i).objectList.get(x).varName) == true) {
    				System.out.println("DECLARATION ERROR " + this.allSymbolTables.get(i).objectList.get(x).varName);
    				return;
    			}
    			set.add(this.allSymbolTables.get(i).objectList.get(x).varName);
    		}
		}

        // Print output
    	for (int i = 0; i < this.allSymbolTables.size(); i++) {
    		System.out.println("Symbol table " + this.allSymbolTables.get(i).scope);
    		for (int x = 0; x < this.allSymbolTables.get(i).objectList.size(); x++) {
    			this.allSymbolTables.get(i).objectList.get(x).print();
    		}

    		if (i +1 < this.allSymbolTables.size()) { // remove trailing extra newline
    			System.out.println();
    		}
    	}

    }

    @Override 
    public void enterFunc_decl(MicroParser.Func_declContext ctx) { 
    	SymbolTable func = new SymbolTable(ctx.getChild(2).getText());
      this.allSymbolTables.add(func); 

    }

    @Override 
    public void enterIf_stmt(MicroParser.If_stmtContext ctx) { 
    	SymbolTable ifst = new SymbolTable("BLOCK " + blockCounter++);
      this.allSymbolTables.add(ifst); 

    }

    @Override 
    public void enterElse_part(MicroParser.Else_partContext ctx) { 
    	if (!ctx.getText().isEmpty()) { // don't want to add else block if unused
	    	SymbolTable elst = new SymbolTable("BLOCK " + blockCounter++);
        this.allSymbolTables.add(elst); 	    	
    	}	
    }

    @Override 
    public void enterDo_while_stmt(MicroParser.Do_while_stmtContext ctx) { 
      SymbolTable dowhl = new SymbolTable("BLOCK " + blockCounter++);
      this.allSymbolTables.add(dowhl);
    }
 


    /** Variable declarations 
    /** (creates symbol objects for variables and adds them to current symbol table) **/

    @Override
    public void enterString_decl(MicroParser.String_declContext ctx) {
      SymbolObject newSymbolObject = new SymbolObject("STRING", ctx.getChild(1).getText(), ctx.getChild(3).getText());
      allSymbolTables.get(allSymbolTables.size()-1).addObject(newSymbolObject);
    }

	@Override 
	public void enterVar_decl(MicroParser.Var_declContext ctx) { 
		String varNames = ctx.getChild(1).getText();
		varNames = varNames.replaceAll(";","");

		for (String varName : varNames.split(",")) {
			SymbolObject newSymbolObject = new SymbolObject(ctx.getChild(0).getText(), varName);
        	allSymbolTables.get(allSymbolTables.size()-1).addObject(newSymbolObject);
		}     
  }

	@Override 
	public void enterParam_decl(MicroParser.Param_declContext ctx) { 
		SymbolObject newSymbolObject = new SymbolObject(ctx.getChild(0).getText(), ctx.getChild(1).getText());
    allSymbolTables.get(allSymbolTables.size()-1).addObject(newSymbolObject);
    // possibly need to handle multiple variables seperately
	}





}

class SymbolTable { // This symbol table contains a list of declarations
    public String scope;
    public List<SymbolObject> objectList;

	public SymbolTable(String scope) {
		this.scope = scope;
		this.objectList = new ArrayList<SymbolObject>();
	}

	public void addObject(SymbolObject obj) {
		this.objectList.add(obj);
	}

}

class SymbolObject { // This class contains variable name, type, and (optionally) value
	public String varName;
	public String varType;
	public String varValue;

	public SymbolObject(String varType, String varName) {
		this.varType = varType;
		this.varName = varName;
	}

	public SymbolObject(String varType, String varName, String varValue) {
		this.varType = varType;
		this.varName = varName;
		this.varValue = varValue;
	}

	public void print() {
		System.out.print("name " + this.varName + " ");
		System.out.print("type " + this.varType);
		if (this.varValue != null) {
			System.out.print(" value " + varValue);
		}
		System.out.println("");
	}
}
