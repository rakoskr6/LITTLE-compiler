import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;
import java.io.*;

class AntlrGlobalListener extends MicroBaseListener {
    private int blockCounter;
    public List<SymbolTable> allSymbolTables = new ArrayList<SymbolTable>();
    private List<IRList> allIRLists = new ArrayList<IRList>();
    private Hashtable<String,String> varTypeTable = new Hashtable<String,String>();

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
        // for (int i = 0; i < this.allSymbolTables.size(); i++) {
        //     System.out.println("Symbol table " + this.allSymbolTables.get(i).scope);
        //     for (int x = 0; x < this.allSymbolTables.get(i).objectList.size(); x++) {
        //         this.allSymbolTables.get(i).objectList.get(x).print();
        //     }

        //     if (i +1 < this.allSymbolTables.size()) { // remove trailing extra newline
        //         System.out.println();
        //     }
        // }

        for(IRList ilist : allIRLists) {
            for(IRNode inode : ilist.getList()) {
                System.out.println(";" + inode.getOpcode() + " " + inode.getOperand1() 
                    + " " + inode.getOperand2() + " " + inode.getResult());
            }
        }

        System.out.println(";tiny code");

        for(int i = 0; i < this.allSymbolTables.get(0).objectList.size(); ++i) {
            System.out.println("var " + this.allSymbolTables.get(0).objectList.get(i).varName);
        }

        for(IRList ilist : allIRLists) {
            for(IRNode inode : ilist.getList()) {
                String op = inode.getOpcode();
                String opd1 = inode.getOperand1();
                String opd2 = inode.getOperand2();
                String res = inode.getResult();
                if(opd1.matches("^\\$T\\d+$")) {
                    int val = new Scanner(opd1).useDelimiter("\\D+").nextInt();
                    opd1 = "" + "r" + Integer.toString(val-1);
                    // System.out.println(opd1);
                }
                if(opd2.matches("^\\$T\\d+$")) {
                    int val = new Scanner(opd2).useDelimiter("\\D+").nextInt();
                    opd2 = "" + "r" + Integer.toString(val-1);
                    // System.out.println(opd2);
                }
                if(res.matches("^\\$T\\d+$")) {
                    int val = new Scanner(res).useDelimiter("\\D+").nextInt();
                    res = "" + "r" + Integer.toString(val-1);
                    // System.out.println(res);
                }
                if(op.equals("STOREI")) {
                    System.out.println("move " + opd1 + " " + res);
                }
                else if(op.equals("STOREF")) {
                    System.out.println("move " + opd1 + " " + res);
                }
                else if(op.equals("ADDI")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("addi " + opd2 + " " + res);
                }
                else if(op.equals("ADDF")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("addr " + opd2 + " " + res);
                }
                else if(op.equals("SUBI")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("subi " + opd2 + " " + res);
                }
                else if(op.equals("SUBF")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("subr " + opd2 + " " + res);
                }
                else if(op.equals("MULTI")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("muli " + opd2 + " " + res);
                }
                else if(op.equals("MULTF")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("mulr " + opd2 + " " + res);
                }
                else if(op.equals("DIVI")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("divi " + opd2 + " " + res);
                }
                else if(op.equals("DIVF")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("divr " + opd2 + " " + res);
                }
                else if(op.equals("WRITEI")) {
                    System.out.println("sys writei " + opd1);
                }
                else if(op.equals("WRITEF")) {
                    System.out.println("sys writer " + opd1);
                }
            }
        }
        System.out.println("sys halt");

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
            varTypeTable.put(varName, ctx.getChild(0).getText());
        }     
  }

    @Override 
    public void enterParam_decl(MicroParser.Param_declContext ctx) { 
        SymbolObject newSymbolObject = new SymbolObject(ctx.getChild(0).getText(), ctx.getChild(1).getText());
        allSymbolTables.get(allSymbolTables.size()-1).addObject(newSymbolObject);
        // possibly need to handle multiple variables seperately
    }

    @Override
    public void enterAssign_expr(MicroParser.Assign_exprContext ctx) {
        // converts to postfix
        ShuntingYardConverter converter = new ShuntingYardConverter();
        ArrayList<String> rpn_list = converter.expressionParse(ctx.getChild(2).getText()); 

        // creates abstract syntax tree from list
        RPNTree rpn_tree = new RPNTree();
        rpn_tree = rpn_tree.parseRPNList(rpn_list); 
       
        IRList ir = new IRList();
        if(varTypeTable.get(ctx.getChild(0).getText()).equals("INT")) {
            ir = rpn_tree.rhsIRGen(ir, rpn_tree);
            if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                rpn_tree.regnum++;
                ir.appendNode("STOREI", ctx.getChild(2).getText(), "", "$T" + Integer.toString(rpn_tree.regnum));
            }
            ir.appendNode("STOREI", "$T"+Integer.toString(rpn_tree.regnum), "", ctx.getChild(0).getText());
        }
        else {
            ir = rpn_tree.rhsIRGenFloat(ir, rpn_tree);
            if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                rpn_tree.regnum++;
                ir.appendNode("STOREF", ctx.getChild(2).getText(), "", "$T" + Integer.toString(rpn_tree.regnum));
            }
            ir.appendNode("STOREF", "$T"+Integer.toString(rpn_tree.regnum), "", ctx.getChild(0).getText());
        }
        // for(IRNode inode : ir.getList()) {
        //     System.out.println(inode.getOpcode() + " " + inode.getOperand1() 
        //         + " " + inode.getOperand2() + " " + inode.getResult());
        // }
        allIRLists.add(ir);
    }

    @Override
    public void enterWrite_stmt(MicroParser.Write_stmtContext ctx) {
        IRList ir = new IRList();
        if(varTypeTable.get(ctx.getChild(2).getText()).equals("INT")) {
            ir.appendNode("WRITEI", ctx.getChild(2).getText(), "", "");
        }
        else {
            ir.appendNode("WRITEF", ctx.getChild(2).getText(), "", "");
        }
        // System.out.println(ir.getNode().getOpcode() + " " + ir.getNode().getOperand1() 
        //         + " " + ir.getNode().getOperand2() + " " + ir.getNode().getResult());
        allIRLists.add(ir);
    }
    
}