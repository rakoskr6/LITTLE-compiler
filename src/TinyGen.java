import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class TinyGen {
    private List < IRList > allIRLists = new ArrayList < IRList > ();
    private Hashtable < String, String > regTypeTable = new Hashtable < String, String > ();
    private AntlrGlobalListener agl;
    static public int numVarInScope;

    public TinyGen(List < IRList > allIRLists, Hashtable < String, String > regTypeTable, AntlrGlobalListener agl) {
        this.allIRLists = allIRLists;
        this.regTypeTable = regTypeTable;
        this.agl = agl;

    }

    public void CreateTiny() {
        System.out.println(";tiny code");

        Registers reg = new Registers();

        
        for (int i = 0; i < AntlrGlobalListener.allSymbolTables.get(0).objectList.size(); ++i) {
            if (AntlrGlobalListener.allSymbolTables.get(0).objectList.get(i).varType == "STRING") {
                System.out.print("str ");
            System.out.println(AntlrGlobalListener.allSymbolTables.get(0).objectList.get(i).varName + " " + AntlrGlobalListener.allSymbolTables.get(0).objectList.get(i).varValue);  
            } else {
                //System.out.print(AntlrGlobalListener.allSymbolTables.get(0).objectList.get(i).varType + " ");
                System.out.print("var ");
                System.out.println(AntlrGlobalListener.allSymbolTables.get(0).objectList.get(i).varName);
            }
        } 
        // Print this every time
        System.out.println("push \npush r0 \npush r1 \npush r2 \npush r3 \njsr main\nsys halt");
        String scope = "global";
        SymbolTable currSymTable = AntlrGlobalListener.allSymbolTables.get(0);
        int numParams = 0;
        ControlFlowNode cfn, lastcfn = null;

        for (IRList ilist: allIRLists) {

            for (IRNode inode: ilist.getList()) {
                System.out.println("\n;" + inode.getOpcode() + " " + inode.getOperand1() + " " + inode.getOperand2() + " " + inode.getResult());
                cfn = this.agl.getBasicBlockFromGraphs(inode);
                
                if (cfn != lastcfn) {
                    reg.spillAll();
                }
                lastcfn = cfn;
                String op = inode.getOpcode();
                String opd1 = inode.getOperand1();
                String opd2 = inode.getOperand2();
                String res = inode.getResult();

                String opOrg = inode.getOpcode();
                String opd1Org = inode.getOperand1();
                String opd2Org = inode.getOperand2();
                String resOrg = inode.getResult();

                boolean is_float = true;
                if (regTypeTable.containsKey(opd1)) {
                    if (regTypeTable.get(opd1).equals("INT")) {
                        is_float = false;
                    } else {
                        is_float = true;
                    }
                } else if (regTypeTable.containsKey(opd2)) {
                    if (regTypeTable.get(opd2).equals("INT")) {
                        is_float = false;
                    } else {
                        is_float = true;
                    }
                }

                if (opd1.matches("^\\$T\\d+$")) {
                    int val = new Scanner(opd1).useDelimiter("\\D+").nextInt();
                    opd1 = "" + "r" + reg.getRegister(opd1Org,opd2Org,resOrg,opd1Org);
                    //opd1 = "" + "r" + Integer.toString(val - 1);
                }
                if (opd2.matches("^\\$T\\d+$")) {
                    int val = new Scanner(opd2).useDelimiter("\\D+").nextInt();
                    opd2 = "" + "r" + reg.getRegister(opd2Org,opd1Org,resOrg,opd2Org);
                    //opd2 = "" + "r" + Integer.toString(val - 1);
                }
                if (res.matches("^\\$T\\d+$")) {
                    int val = new Scanner(res).useDelimiter("\\D+").nextInt();
                    res = "" + "r" + reg.getRegister(res,opd1Org,opd2Org,resOrg);
                    //res = "" + "r" + Integer.toString(val - 1);
                }


                //System.out.println(";" + op + " " + opd1 + " " + opd2 + " " + res);


                // Redefined opps for easy manipulation
                String s1 = opd1, s2 = opd2, s3 = res;
                Integer i1 = 0, i2 = 0, i3 = 0;
                
                // maybe have to copy this stuff to registers! :(
                // Appropriatly manipulates strings for certain opps
                if (op.equals("STOREI") || op.equals("STOREF") || op.equals("ADDI") || op.equals("ADDF") || op.equals("SUBF") || op.equals("SUBI") || op.equals("MULTI") || op.equals("MULTF") || op.equals("DIVI") || op.equals("DIVF") || op.equals("WRITEI") || op.equals("WRITEF") || op.equals("READI") || op.equals("LE") || op.equals("GE") || op.equals("LT") || op.equals("GT") || op.equals("EQ") || op.equals("NE") || op.equals("WRITES") || op.equals("READF") || op.equals("JSR") || op.equals("POP") || op.equals("PUSH")) {
                    if (s1.startsWith("$P")) {
                            s1 = s1.replace("$P", "");
                            i1 = Integer.parseInt(s1);
                            i1 = -i1 + numParams + 6;
                            opd1 = "$" + i1;
                        } else if (s1.startsWith("$L")) {
                            s1 = s1.replace("$L", "");
                            i1 = Integer.parseInt(s1);
                            i1 = -i1;
                            opd1 = "$" + i1;
                        } else if (s1.startsWith("$R")) {
                            i1 = 6 + numParams;
                            opd1 = "$" + i1;
                        } else if ((!s1.matches("[0-9]+\\.?[0-9]*")) && (!opd1.startsWith("$T")) && (!s1.matches("r[0-9]")) && (!s1.matches("")) && (!s1.startsWith("label")) && (!op.equals("JSR")) && (!op.equals("WRITES"))) {
                            opd1 = "" + "r" + reg.getRegister(opd1Org,opd2Org,resOrg,opd1Org);
                        }

                        if (s2.startsWith("$P")) {
                            s2 = s2.replace("$P", "");
                            i2 = Integer.parseInt(s2);
                            i2 = -i2 + numParams + 6;
                            opd2 = "$" + i2;
                        } else if (s2.startsWith("$L")) {
                            s2 = s2.replace("$L", "");
                            i2 = Integer.parseInt(s2);
                            i2 = -i2;
                            opd2 = "$" + i2;
                        } else if (s2.startsWith("$R")) {
                            i2 = 6 + numParams;
                            opd2 = "$" + i2;
                        } else if ((!s2.matches("[0-9]+\\.?[0-9]*")) && (!opd2.startsWith("$T")) && (!s2.matches("r[0-9]")) && (!s2.matches("")) && (!s2.startsWith("label")) && (!op.equals("JSR")) && (!op.equals("WRITES"))) {
                            opd2 = "" + "r" + reg.getRegister(opd2Org,opd1Org,resOrg,opd2Org);
                            
                        }


                        if (s3.startsWith("$P")) {
                            s3 = s3.replace("$P", "");
                            i3 = Integer.parseInt(s3);
                            i3 = -i3 + numParams + 6;
                            res = "$" + i3;
                        } else if (s3.startsWith("$L")) {
                            s3 = s3.replace("$L", "");
                            i3 = Integer.parseInt(s3);
                            i3 = -i3;
                            res = "$" + i3;
                        } else if (s3.startsWith("$R")) {
                            i3 = 6 + numParams;
                            res = "$" + i3;
                        } else if ((!s3.matches("[0-9]+\\.?[0-9]*")) && (!res.startsWith("$T")) && (!s3.matches("r[0-9]")) && (!s3.matches("")) && (!s3.startsWith("label")) && (!op.equals("JSR")) && (!op.equals("WRITES"))) {
                            res = "" + "r" + reg.getRegister(res,opd1Org,opd2Org,res);
                        }

                }

                if (op.equals("STOREI")) {
                    //reg.switchIfRegisters(opd1, res, opd1Org, resOrg);
                    System.out.println("move " + opd1 + " " + res);
                } else if (op.equals("STOREF")) {
                    //reg.switchIfRegisters(opd1, res, opd1Org, resOrg);
                    System.out.println("move " + opd1 + " " + res);
                } else if (op.equals("ADDI")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("addi " + opd2 + " " + res);
                } else if (op.equals("ADDF")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("addr " + opd2 + " " + res);
                } else if (op.equals("SUBI")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("subi " + opd2 + " " + res);
                } else if (op.equals("SUBF")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("subr " + opd2 + " " + res);
                } else if (op.equals("MULTI")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("muli " + opd2 + " " + res);
                } else if (op.equals("MULTF")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("mulr " + opd2 + " " + res);
                } else if (op.equals("DIVI")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("divi " + opd2 + " " + res);
                } else if (op.equals("DIVF")) {
                    System.out.println("move " + opd1 + " " + res);
                    System.out.println("divr " + opd2 + " " + res);
                    System.out.println(";opd1: " + opd1 +", opd2: " + opd2 + ", res: " + res);
                } else if (op.equals("WRITEI")) {
                    System.out.println("sys writei " + opd1);
                } else if (op.equals("WRITEF")) {
                    System.out.println("sys writer " + opd1);
                } else if (op.equals("READI")) {
                    System.out.println("sys readi " + res);
                } else if (op.equals("LABEL")) {
                    if (!opd1.isEmpty()) {
                        System.out.println("label " + opd1);
                        scope = opd1;
                    } else if (!opd2.isEmpty()) {
                        System.out.println("label " + opd2);
                        scope = opd2;
                    } else {
                        System.out.println("label " + res);
                        scope = res;
                    }
                } else if (op.equals("JUMP")) {
                    reg.spillAll();
                    System.out.println("jmp " + res);
                } else if (op.equals("LE")) {
                    if (is_float) {
                        System.out.println("cmpr " + opd1 + " " + opd2);
                    } else {
                        System.out.println("cmpi " + opd1 + " " + opd2);
                    }
                    System.out.println("jle " + res);
                } else if (op.equals("GE")) {
                    if (is_float) {
                        System.out.println("cmpr " + opd1 + " " + opd2);
                    } else {
                        System.out.println("cmpi " + opd1 + " " + opd2);
                    }
                    System.out.println("jge " + res);
                } else if (op.equals("LT")) {
                    if (is_float) {
                        System.out.println("cmpr " + opd1 + " " + opd2);
                    } else {
                        System.out.println("cmpi " + opd1 + " " + opd2);
                    }
                    System.out.println("jlt " + res);
                } else if (op.equals("GT")) {
                    if (is_float) {
                        System.out.println("cmpr " + opd1 + " " + opd2);
                    } else {
                        System.out.println("cmpi " + opd1 + " " + opd2);
                    }
                    System.out.println("jgt " + res);
                } else if (op.equals("EQ")) {
                    if (is_float) {
                        System.out.println("cmpr " + opd1 + " " + opd2);
                    } else {
                        System.out.println("cmpi " + opd1 + " " + opd2);
                    }
                    System.out.println("jeq " + res);
                } else if (op.equals("NE")) {
                    if (is_float) {
                        System.out.println("cmpr " + opd1 + " " + opd2);
                    } else {
                        System.out.println("cmpi " + opd1 + " " + opd2);
                    }
                    System.out.println("jne " + res);
                } else if (op.equals("LINK")) {
                    int numVar = 0; // make equal to #variables in function

                    for (SymbolTable sym: AntlrGlobalListener.allSymbolTables) {
                        if (sym.scope == scope) {
                            //numVar = sym.getNumVarsInScope();
                            numVar = sym.getNumInScope(); 
                            numVarInScope = sym.getNumVarsInScope();
                            currSymTable = sym;
                            numParams = sym.getNumParamsInScope();

                        }
                    }

                    System.out.println("\nlink " + agl.getFuncRegNum(inode) + "\n");
                } else if (op.equals("RET")) {
                    System.out.println("unlnk");
                    System.out.println("ret");

                } else if (op.equals("WRITES")) {
                    System.out.println("sys writes " + opd1);
                } else if (op.equals("READF")) {
                    System.out.println("sys readr " + res);
                } else if (op.equals("JSR")) {
                    System.out.println("push r0\npush r1\npush r2\npush r3");
                    reg.spillAll();
                    System.out.println("jsr " + opd1);
                    System.out.println("pop r3\npop r2\npop r1\npop r0");

                } else if (op.equals("POP")) {
                    System.out.println("pop " + opd1);
                } else if (op.equals("PUSH")) {
                    System.out.println("push " + opd1);
                } else {
                    System.out.println("Unsupported operation: " + op);
                }
                
                


                System.out.print(";Out set: ");
                for (String s : agl.getOutSetFromGraphs(inode)) {
                    System.out.print(s + ", ");
                }
                System.out.println();

                reg.updateLive(agl.getOutSetFromGraphs(inode));
                
            }


        }
        reg.spillAll();

    }
}