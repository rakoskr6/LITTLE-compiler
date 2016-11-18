import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;
import java.io.*;

class AntlrGlobalListener extends MicroBaseListener {
    private int blockCounter;
    public int paramCounter;
    public int localCounter;
    public static List<SymbolTable> allSymbolTables = new ArrayList<SymbolTable>();
    private List<IRList> allIRLists = new ArrayList<IRList>();
    private Hashtable<String,String> varTypeTable = new Hashtable<String,String>();
    private Hashtable<String,String> regTypeTable = new Hashtable<String,String>();

    // if blocks
    private int labelCounter;
    private int regCounter;
    private Hashtable<String,String> logicOpTable = new Hashtable<String,String>();
    private Stack<String> labelStack = new Stack<String>();
    private Stack<String> exitStack = new Stack<String>();

    private boolean funcReturnsInt; // true = INT, false = FLOAT

    // debug
    public boolean debugST = true;

    public AntlrGlobalListener() {
        this.blockCounter = 1;
        this.regCounter   = 1;
        this.labelCounter = 1;
        this.paramCounter = 1;
        this.localCounter = 1;
        populateLogicalOps();
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

        // Symbol table expulsion
        // for (int i = 0; i < this.allSymbolTables.size(); i++) {
        //     System.out.println("Symbol table " + this.allSymbolTables.get(i).scope);
        //     for (int x = 0; x < this.allSymbolTables.get(i).objectList.size(); x++) {
        //         this.allSymbolTables.get(i).objectList.get(x).print();
        //     }

        //     if (i +1 < this.allSymbolTables.size()) { // remove trailing extra newline
        //         System.out.println();
        //     }
        // }
        if(this.debugST) {
            System.out.println("START ALL SYMBOL TABLE DEBUG INFORMATION");
            for (SymbolTable st : this.allSymbolTables) {
                st.printSymbolTable();
            }
            System.out.println("END ALL SYMBOL TABLE DEBUG INFORMATION\n");
        }

        System.out.println(";IR code");
        for(IRList ilist : allIRLists) {
            for(IRNode inode : ilist.getList()) {
                System.out.println(";" + inode.getOpcode() + " " + inode.getOperand1() 
                    + " " + inode.getOperand2() + " " + inode.getResult());
                if(inode.getOpcode().equals("RET"))
                    System.out.println();
            }
        }

       
        
        
        System.out.println(";tiny code");

        for(int i = 0; i < this.allSymbolTables.get(0).objectList.size(); ++i) {
            if (this.allSymbolTables.get(0).objectList.get(i).varType == "STRING") {
                System.out.print("str ");
            }
            else {
                System.out.print(this.allSymbolTables.get(0).objectList.get(i).varType + " ");
            }
            System.out.println(this.allSymbolTables.get(0).objectList.get(i).varName + " " + this.allSymbolTables.get(0).objectList.get(i).varValue);
        }
        // Print this every time
        System.out.println("push \npush r0 \npush r1 \npush r2 \npush r3 \njsr main\nsys halt");
        String scope = "global";
        SymbolTable currSymTable = this.allSymbolTables.get(0);
        int numParams = 0;
        for(IRList ilist : allIRLists) {
           
            for(IRNode inode : ilist.getList()) {
                String op = inode.getOpcode();
                String opd1 = inode.getOperand1();
                String opd2 = inode.getOperand2();
                String res = inode.getResult();

                boolean is_float = true;
                if(regTypeTable.containsKey(opd1)) {
                    if(regTypeTable.get(opd1).equals("INT")) {
                        is_float = false;
                    }
                    else {
                        is_float = true;
                    }
                }
                else if(regTypeTable.containsKey(opd2)) {
                    if(regTypeTable.get(opd2).equals("INT")) {
                        is_float = false;
                    }
                    else {
                        is_float = true;
                    }
                }

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
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }


                    System.out.println("move " + opd1 + " " + res);
                }
                else if(op.equals("STOREF")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }

                    System.out.println("move " + opd1 + " " + res);
                }
                else if(op.equals("ADDI")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }

                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("addi " + opd2 + " " + res);
                }
                else if(op.equals("ADDF")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }

                    
                    //System.out.println("\n" + opd1 + ": " + i1 + ", " + opd2 + ": " + i2);
                    
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("addr " + opd2 + " " + res);
                }
                else if(op.equals("SUBI")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }

                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("subi " + opd2 + " " + res);
                }
                else if(op.equals("SUBF")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }


                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("subr " + opd2 + " " + res);
                }
                else if(op.equals("MULTI")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }

                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("muli " + opd2 + " " + res);
                }

                else if(op.equals("MULTF")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }

                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("mulr " + opd2 + " " + res);
                }
                else if(op.equals("DIVI")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }

                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("divi " + opd2 + " " + res);
                }
                else if(op.equals("DIVF")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }


                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("divr " + opd2 + " " + res);
                }
                else if(op.equals("WRITEI")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }


                    System.out.println("sys writei " + opd1);
                }
                else if(op.equals("WRITEF")) {
                    String s1 = opd1, s2 = opd2;
                    Integer i1 = 0, i2 = 0;
                    
                    if (s1.startsWith("$P")) {
                        s1 = s1.replace("$P","");
                        i1 = Integer.parseInt(s1);
                        i1 = -i1 + numParams + 6;
                        opd1 = "$"  + i1;
                    }

                    if (s2.startsWith("$P")) {
                        s2 = s2.replace("$P","");
                        i2 = Integer.parseInt(s2);
                        i2 = -i2 + numParams + 6;
                        opd2 = "$"  + i2;
                    }
                    
                    System.out.println("sys writer " + opd1);
                }
                else if(op.equals("READI")) {
                    // To implement actual addresses
                    System.out.println("sys readi " + res);
                }
                else if(op.equals("LABEL")) {
                    System.out.println("label " + opd1);
                    scope = opd1;
                }
                else if(op.equals("JUMP")) {
                    System.out.println("jmp " + res);
                }
                else if(op.equals("LE")) {
                    if(is_float) { System.out.println("cmpr " + opd1 + " " + opd2); }
                    else { System.out.println("cmpi " + opd1 + " " + opd2); }
                    System.out.println("jle " + res);
                }
                else if(op.equals("GE")) {
                    if(is_float) { System.out.println("cmpr " + opd1 + " " + opd2); }
                    else { System.out.println("cmpi " + opd1 + " " + opd2); }
                    System.out.println("jge " + res);
                }
                else if(op.equals("LT")) {
                    if(is_float) { System.out.println("cmpr " + opd1 + " " + opd2); }
                    else { System.out.println("cmpi " + opd1 + " " + opd2); }
                    System.out.println("jlt " + res);
                }
                else if(op.equals("GT")) {
                    if(is_float) { System.out.println("cmpr " + opd1 + " " + opd2); }
                    else { System.out.println("cmpi " + opd1 + " " + opd2); }
                    System.out.println("jgt " + res);
                }
                else if(op.equals("EQ")) {
                    if(is_float) { System.out.println("cmpr " + opd1 + " " + opd2); }
                    else { System.out.println("cmpi " + opd1 + " " + opd2); }
                    System.out.println("jeq " + res);
                }
                else if(op.equals("NE")) {
                    if(is_float) { System.out.println("cmpr " + opd1 + " " + opd2); }
                    else { System.out.println("cmpi " + opd1 + " " + opd2); }
                    System.out.println("jne " + res);
                }
                
                else if(op.equals("LINK")) {
                    int numVar = 0;// make equal to #variables in function

                    for (SymbolTable sym : allSymbolTables) {
                        if (sym.scope == scope) {
                            numVar = sym.getNumVarsInScope();
                            currSymTable = sym;
                            numParams = sym.getNumParamsInScope();
                        }
                    }

                    System.out.println("link " + numVar);
                }
                else if(op.equals("RET")) {
                    System.out.println("unlnk");
                    System.out.println("ret");

                }
                else if (op.equals("WRITES")) {
                    System.out.println("sys writes " + opd1);
                }
                else if (op.equals("READF")) {
                    System.out.println("sys readr " + res);
                }
                else if (op.equals("JSR")) {
                    System.out.println("jsr " + opd1);
                }
                else if (op.equals("POP")) {
                    System.out.println("POP TO BE IMPLEMENTED");
                }
                else if (op.equals("PUSH")) {
                    System.out.println("PUSH TO BE IMPLEMENTED");
                }

                else {
                    System.out.println("Unsupported operation: " + op);
                }

            }
        }
        System.out.println("end");
        
        
        
    }

    @Override 
    public void enterFunc_decl(MicroParser.Func_declContext ctx) { 
        // global symbol tables
        String func_name = ctx.getChild(2).getText();
        SymbolTable func = new SymbolTable(func_name);
        this.allSymbolTables.add(func); 
        
        String ret_type = ctx.getChild(1).getText();
        funcReturnsInt = (ret_type.equals("INT"));

        paramCounter = 1;
        localCounter = 1;
        RPNTree.regnum = 0;

        IRList ir = new IRList();
        ir.appendNode("LABEL", func_name, "", "");
        ir.appendNode("LINK", "", "", "");

        allIRLists.add(ir);
    }

    private void populateLogicalOps() {
        logicOpTable.put(">",  "LE");
        logicOpTable.put("<",  "GE");
        logicOpTable.put(">=", "LT");
        logicOpTable.put("<=", "GT");
        logicOpTable.put("!=", "EQ");
        logicOpTable.put("=",  "NE");
    }

    private ArrayList<String> tokenizeConditional(String expression) {
        ArrayList<String> tokenized_list = new ArrayList<String>();
        StringTokenizer strtok = new StringTokenizer(expression, "<=>=!=<>=", true);
        tokenized_list.add(strtok.nextToken());
        if (strtok.countTokens() > 2) {
            tokenized_list.add(strtok.nextToken() + strtok.nextToken());
        } else {
            tokenized_list.add(strtok.nextToken());
        }
        tokenized_list.add(strtok.nextToken());
        return tokenized_list;
    }

    private void pushLabelStack() {
        labelStack.push("label" + Integer.toString(labelCounter));
        labelCounter += 1;
    }

    private void pushExitStack() {
        exitStack.push("label" + Integer.toString(labelCounter));
        labelCounter += 1;
    }

    @Override 
    public void enterIf_stmt(MicroParser.If_stmtContext ctx) { 
        SymbolTable ifst = new SymbolTable("BLOCK " + blockCounter++);
        this.allSymbolTables.add(ifst); 

        IRList ir = new IRList();
        String expr = ctx.getChild(2).getText();
        ArrayList<String> tokenized_list = tokenizeConditional(expr);
        if(expr.equals("TRUE")) {
            // untested by step 5
            RPNTree.regnum++;
            ir.appendNode("STOREI", "1", "", "$T" + Integer.toString(RPNTree.regnum));
            regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
            RPNTree.regnum++;
            ir.appendNode("STOREI", "1", "", "$T" + Integer.toString(RPNTree.regnum));
            regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
            ir.appendNode("NE", "$T" + Integer.toString(RPNTree.regnum - 1), 
                "$T" + Integer.toString(RPNTree.regnum), labelStack.peek());
        }
        else {
            ShuntingYardConverter converter = new ShuntingYardConverter();
            ArrayList<String> rpn_list = converter.expressionParse(tokenized_list.get(2));
            RPNTree rpn_tree = new RPNTree();
            rpn_tree = rpn_tree.parseRPNList(rpn_list);
            if(varTypeTable.get(tokenized_list.get(0)).equals("INT")) {
                ir = rpn_tree.rhsIRGen(ir, rpn_tree);
                if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                    rpn_tree.regnum++;
                    ir.appendNode("STOREI", tokenized_list.get(2), "", "$T" + Integer.toString(rpn_tree.regnum));
                    regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
                } 
            }
            else {
                ir = rpn_tree.rhsIRGenFloat(ir, rpn_tree);
                if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                    rpn_tree.regnum++;
                    ir.appendNode("STOREF", tokenized_list.get(2), "", "$T" + Integer.toString(rpn_tree.regnum));
                    regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "FLOAT");
                } 
            }
        }
        pushLabelStack();
        pushExitStack();
        String logic_op = logicOpTable.get(tokenized_list.get(1));
        String logic_val = tokenized_list.get(0);
        String scopeReg = getScopeReg(logic_val);
        logic_val = scopeReg;
        if(scopeReg.equals("GLOB")) {
            logic_val = tokenized_list.get(0);
        }
        ir.appendNode(logic_op, logic_val, "$T" + Integer.toString(RPNTree.regnum), labelStack.peek());

        allIRLists.add(ir);
    }

    @Override 
    public void enterElse_part(MicroParser.Else_partContext ctx) { 
        IRList ir = new IRList();
        ir.appendNode("JUMP", "", "", exitStack.peek());
        ir.appendNode("LABEL", "", "", labelStack.pop());
        if (!ctx.getText().isEmpty()) { // don't want to add else block if unused
            SymbolTable elst = new SymbolTable("BLOCK " + blockCounter++);
            this.allSymbolTables.add(elst);             

            pushLabelStack();
            // pushExitStack();
            String expr = ctx.getChild(2).getText();
            // ArrayList<String> tokenized_list = tokenizeConditional(expr);
            if(expr.equals("TRUE")) {
                RPNTree.regnum++;
                ir.appendNode("STOREI", "1", "", "$T" + Integer.toString(RPNTree.regnum));
                regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
                RPNTree.regnum++;
                ir.appendNode("STOREI", "1", "", "$T" + Integer.toString(RPNTree.regnum));
                regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
                ir.appendNode("NE", "$T" + Integer.toString(RPNTree.regnum - 1), 
                    "$T" + Integer.toString(RPNTree.regnum), labelStack.peek());
                regTypeTable.put(labelStack.peek(), "INT");
            }
            else { // i.e., not TRUE
                // untested by step 5
                ArrayList<String> tokenized_list = tokenizeConditional(expr);
                RPNTree.regnum++; 
                if(varTypeTable.get(tokenized_list.get(0)).equals("INT")) {
                    ir.appendNode("STOREI", tokenized_list.get(2), "", "$T" + Integer.toString(RPNTree.regnum));
                    regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
                }
                else {
                    ir.appendNode("STOREF", tokenized_list.get(2), "", "$T" + Integer.toString(RPNTree.regnum));
                    regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "FLOAT");
                }
                String logic_op = logicOpTable.get(tokenized_list.get(1));
                String logic_val = tokenized_list.get(0);
                String scopeReg = getScopeReg(logic_val);
                logic_val = scopeReg;
                if(scopeReg.equals("GLOB")) {
                    logic_val = tokenized_list.get(0);
                }
                ir.appendNode(logic_op, logic_val,
                    "$T" + Integer.toString(RPNTree.regnum), labelStack.peek());
                regTypeTable.put(labelStack.peek(), "FLOAT");
            }
        }
        allIRLists.add(ir);
    }

    @Override
    public void exitIf_stmt(MicroParser.If_stmtContext ctx) {
        IRList ir = new IRList();
        ir.appendNode("LABEL", "", "", exitStack.pop());
        allIRLists.add(ir);
    }

    @Override 
    public void enterDo_while_stmt(MicroParser.Do_while_stmtContext ctx) { 
        SymbolTable dowhl = new SymbolTable("BLOCK " + blockCounter++);
        this.allSymbolTables.add(dowhl);

        IRList ir = new IRList();
        pushLabelStack();
        pushExitStack();
        ir.appendNode("LABEL", "", "", labelStack.peek());
        allIRLists.add(ir);
    }

    @Override
    public void exitDo_while_stmt(MicroParser.Do_while_stmtContext ctx) {
        IRList ir = new IRList();
        String expr = ctx.getChild(5).getText();
        ArrayList<String> tokenized_list = tokenizeConditional(expr);
        RPNTree.regnum++;
        if(varTypeTable.get(tokenized_list.get(0)).equals("INT")) {
            ir.appendNode("STOREI", getScopeReg(tokenized_list.get(2)), "", "$T" + Integer.toString(RPNTree.regnum));
            regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
        }
        else {
            ir.appendNode("STOREF", getScopeReg(tokenized_list.get(2)), "", "$T" + Integer.toString(RPNTree.regnum));
            regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "FLOAT");
        }
        String logic_op = logicOpTable.get(tokenized_list.get(1));
        String logic_val = tokenized_list.get(0);
        String scopeReg = getScopeReg(logic_val);
        logic_val = scopeReg;
        if(scopeReg.equals("GLOB")) {
            logic_val = tokenized_list.get(0);
        }
        ir.appendNode(logic_op, logic_val, "$T" + Integer.toString(RPNTree.regnum), exitStack.peek());
        ir.appendNode("JUMP", "", "", labelStack.pop());
        ir.appendNode("LABEL", "", "", exitStack.pop());
        allIRLists.add(ir);
    }

    @Override
    public void enterRead_stmt(MicroParser.Read_stmtContext ctx) {
        IRList ir = new IRList();
        String[] read_vals = ctx.getChild(2).getText().split(",");
        for (String read_val : read_vals) {
            String var_name = read_val;
            String var_type = varTypeTable.get(var_name);
            String scopeReg = getScopeReg(var_name);
            String op_type = "";
            if(var_type.equals("INT")) {
                op_type = "I";
            }
            else if (var_type.equals("FLOAT")) {
                op_type = "F";
            }
            else {
                op_type = "S";
            }
            if(!scopeReg.equals("")) {
                ir.appendNode("READ" + op_type, "", "", scopeReg);
            }
            else {
                ir.appendNode("READ" + op_type, "", "", var_name);
            }
        }

        allIRLists.add(ir);
    }

    /** Variable declarations 
    /** (creates symbol objects for variables and adds them to current symbol table) **/

    @Override
    public void enterString_decl(MicroParser.String_declContext ctx) {
        SymbolObject newSymbolObject = new SymbolObject("STRING", ctx.getChild(1).getText(), ctx.getChild(3).getText());
        newSymbolObject.scopeReg = "GLOB";
        allSymbolTables.get(allSymbolTables.size()-1).addObject(newSymbolObject);

        varTypeTable.put(ctx.getChild(1).getText(), "STRING");
    }

    @Override 
    public void enterVar_decl(MicroParser.Var_declContext ctx) { 
        String varNames = ctx.getChild(1).getText();
        varNames = varNames.replaceAll(";","");

        for (String varName : varNames.split(",")) {
            SymbolObject newSymbolObject = new SymbolObject(ctx.getChild(0).getText(), varName);
            if(!allSymbolTables.get(allSymbolTables.size()-1).scope.equals("GLOBAL")) {
                newSymbolObject.scopeReg = "$L" + Integer.toString(localCounter);
                localCounter += 1;
            }
            else {
                newSymbolObject.scopeReg = "GLOB";
            }
            allSymbolTables.get(allSymbolTables.size()-1).addObject(newSymbolObject);
            varTypeTable.put(varName, ctx.getChild(0).getText());
        }     
  }

    @Override 
    public void enterParam_decl(MicroParser.Param_declContext ctx) { 
        SymbolObject newSymbolObject = new SymbolObject(ctx.getChild(0).getText(), ctx.getChild(1).getText());
        newSymbolObject.scopeReg = "$P" + Integer.toString(paramCounter);
        paramCounter += 1;
        allSymbolTables.get(allSymbolTables.size()-1).addObject(newSymbolObject);
        // possibly need to handle multiple variables seperately
        varTypeTable.put(ctx.getChild(1).getText(), ctx.getChild(0).getText());
    }

    @Override
    public void enterAssign_expr(MicroParser.Assign_exprContext ctx) {
        // expression slicing into sides
        String lhs = ctx.getChild(0).getText();
        String rhs = ctx.getChild(2).getText();

        if(rhs.matches("^\\w+\\(.*\\)$")) { // function...
            IRList ir = new IRList();
            String function_name = rhs.split("\\(")[0];
            String[] arg_list = rhs.split("\\(")[1].split("\\)")[0].split(",");
            for(String arg : arg_list) {
                // converts to postfix
                ShuntingYardConverter converter = new ShuntingYardConverter();
                ArrayList<String> rpn_list = converter.expressionParse(arg); 

                // creates abstract syntax tree from list
                RPNTree rpn_tree = new RPNTree();
                rpn_tree = rpn_tree.parseRPNList(rpn_list); 
       
                if(varTypeTable.get(lhs).equals("INT")) { // this is actually untrue - we /should/ look at arg types, not return type
                    ir = rpn_tree.rhsIRGen(ir, rpn_tree);
                    if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                        if(arg.matches("^\\d+(?:\\.\\d+)?$")) { 
                            rpn_tree.regnum++;
                            ir.appendNode("STOREI", arg, "", "$T" + Integer.toString(rpn_tree.regnum));
                        }
                        else { // a := b
                // ir.appendNode("DEBUG", "WE'RE", "IN", "HERE");
                            // ir.appendNode("STOREI", getScopeReg(arg), "", "$T" + Integer.toString(rpn_tree.regnum));
                        }
                    }
                }
                else {
                    ir = rpn_tree.rhsIRGenFloat(ir, rpn_tree);
                    if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                        if(rhs.matches("^\\d+(?:\\.\\d+)?$")) { // a := 1
                            rpn_tree.regnum++;
                            ir.appendNode("STOREF", arg, "", "$T" + Integer.toString(rpn_tree.regnum));
                        }
                        else { // a := b
                            // ir.appendNode("STOREF", getScopeReg(arg), "", "$T" + Integer.toString(rpn_tree.regnum));
                        }
                    }
                }
            }
            ir.appendNode("PUSH", "", "", "");
            for(String arg : arg_list) {
                String scopeReg = getScopeReg(arg);
                if(scopeReg.equals("not present"))
                    ir.appendNode("PUSH", "$T" + Integer.toString(RPNTree.regnum), "", "");
                else
                    ir.appendNode("PUSH", getScopeReg(arg), "", "");
            }
            ir.appendNode("JSR", function_name, "", "");
            for(String arg : arg_list) {
                ir.appendNode("POP", "", "", "");
            }
            RPNTree.regnum++;
            ir.appendNode("POP", "$T" + Integer.toString(RPNTree.regnum), "", "");
            if(varTypeTable.get(lhs).equals("INT"))
                ir.appendNode("STOREI", "$T" + Integer.toString(RPNTree.regnum), "", getScopeReg(lhs));
            else
                ir.appendNode("STOREF", "$T" + Integer.toString(RPNTree.regnum), "", getScopeReg(lhs));

            allIRLists.add(ir);
        }
        else {
            // converts to postfix
            ShuntingYardConverter converter = new ShuntingYardConverter();
            ArrayList<String> rpn_list = converter.expressionParse(rhs); 

            // creates abstract syntax tree from list
            RPNTree rpn_tree = new RPNTree();
            rpn_tree = rpn_tree.parseRPNList(rpn_list); 
           
            IRList ir = new IRList();
            if(varTypeTable.get(lhs).equals("INT")) {
                ir = rpn_tree.rhsIRGen(ir, rpn_tree);
                if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                    rpn_tree.regnum++;
                    if(rhs.matches("^\\d+(?:\\.\\d+)?$")) { // a := 1
                        ir.appendNode("STOREI", rhs, "", "$T" + Integer.toString(rpn_tree.regnum));
                    }
                    else { // a := b
                        ir.appendNode("STOREI", getScopeReg(rhs), "", "$T" + Integer.toString(rpn_tree.regnum));
                    }
                    regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
                }
                ir.appendNode("STOREI", "$T"+Integer.toString(rpn_tree.regnum), "", getScopeReg(lhs));
                regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "INT");
                regTypeTable.put(lhs, "INT");
                regTypeTable.put(getScopeReg(lhs), "INT");
            }
            else {
                ir = rpn_tree.rhsIRGenFloat(ir, rpn_tree);
                if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                    rpn_tree.regnum++;
                    if(rhs.matches("^\\d+(?:\\.\\d+)?$")) { // a := 1
                        ir.appendNode("STOREF", rhs, "", "$T" + Integer.toString(rpn_tree.regnum));
                    }
                    else { // a := b
                        ir.appendNode("STOREF", getScopeReg(rhs), "", "$T" + Integer.toString(rpn_tree.regnum));
                    }
                    regTypeTable.put("$T" + Integer.toString(RPNTree.regnum), "FLOAT");
                }
                ir.appendNode("STOREF", "$T"+Integer.toString(rpn_tree.regnum), "", getScopeReg(lhs));
                regTypeTable.put(lhs, "FLOAT");
                regTypeTable.put(getScopeReg(lhs), "FLOAT");
            }
            allIRLists.add(ir);
        }
    }

    private String getScopeReg(String value) {
        for(int i = this.allSymbolTables.size()-1; i >= 0; --i) {
            SymbolTable currTable = this.allSymbolTables.get(i);
            String lookup = currTable.getScopeRegByVarName(value);
            if(!lookup.equals("")) 
                return lookup;
            if(!currTable.scope.contains("BLOCK") && !currTable.scope.equals("GLOBAL")) {
                i = 1; // move to GLOBAL
            }
        }
        return "not present";
    }

    @Override
    public void enterWrite_stmt(MicroParser.Write_stmtContext ctx) {
        IRList ir = new IRList();
        String[] write_vals = ctx.getChild(2).getText().split(",");
        for(String write_val : write_vals) {
            String var_type = varTypeTable.get(write_val);
            String scopeReg = getScopeReg(write_val);
            String write_out = scopeReg;
            if(scopeReg.equals("GLOB")) {
                write_out = write_val;
            }
            if(var_type.equals("INT")) {
                ir.appendNode("WRITEI", write_out, "", "");
            }
            else if(var_type.equals("STRING")) {
                ir.appendNode("WRITES", write_out, "", "");
            }
            else {
                ir.appendNode("WRITEF", write_out, "", "");
            }
        }
        allIRLists.add(ir);
    }

    @Override
    public void enterReturn_stmt(MicroParser.Return_stmtContext ctx) {
        String return_val = ctx.getChild(1).getText();
        IRList ir = new IRList();
        if(return_val.matches("^\\d+$")) { // RETURN 0;
            RPNTree.regnum++;
            String temp_reg = "$T" + Integer.toString(RPNTree.regnum);
            ir.appendNode("STOREI", return_val, "", temp_reg);
            ir.appendNode("STOREI", temp_reg, "", "$R");
        }
        else if(return_val.matches("^\\d+(?:\\.\\d+)?$")) { // RETURN 0.5;
            RPNTree.regnum++;
            ir.appendNode("STOREF", return_val, "", "$T" + Integer.toString(RPNTree.regnum));
            ir.appendNode("STOREF", return_val, "", "$R");
        }
        else { // RETURN a;
            // converts to postfix
            ShuntingYardConverter converter = new ShuntingYardConverter();
            ArrayList<String> rpn_list = converter.expressionParse(return_val); 

            // creates abstract syntax tree from list
            RPNTree rpn_tree = new RPNTree();
            rpn_tree = rpn_tree.parseRPNList(rpn_list); 
           
            if(funcReturnsInt) {
                ir = rpn_tree.rhsIRGen(ir, rpn_tree);
                if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                    rpn_tree.regnum++;
                    ir.appendNode("STOREI", getScopeReg(return_val), "", "$T" + Integer.toString(rpn_tree.regnum));
                }
                ir.appendNode("STOREI", "$T"+Integer.toString(rpn_tree.regnum), "", "$R");
            }
            else {
                ir = rpn_tree.rhsIRGenFloat(ir, rpn_tree);
                if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
                    rpn_tree.regnum++;
                    ir.appendNode("STOREF", getScopeReg(return_val), "", "$T" + Integer.toString(rpn_tree.regnum));
                }
                ir.appendNode("STOREF", "$T"+Integer.toString(rpn_tree.regnum), "", "$R");
            }
        }
        ir.appendNode("RET", "", "", "");
        allIRLists.add(ir);
    }
}            // if(varTypeTable.get(return_val).equals("INT")) {
            //     ir.appendNode("STOREI", getScopeReg(return_val), "", "R");
            // }
            // else {
            //     ir.appendNode("STOREF", getScopeReg(return_val), "", "R");
            // }
