import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;
import java.io.*;

class TinyGenerator  {
    public void TinyGenerator() {

        System.out.println(";tiny code");

        for(int i = 0; i < this.allSymbolTables.get(0).objectList.size(); ++i) {
            System.out.println("var " + this.allSymbolTables.get(0).objectList.get(i).varName);
        }
        // Print this every time
        System.out.println("push \npush r0 \npush r1 \npush r2 \npush r3 \njsr main\nsys halt");

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
                else if(op.equals("READI")) {
                    System.out.println("sys readi " + res);
                }
                else if(op.equals("LABEL")) {
                    System.out.println("label " + res);
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
                    System.out.println("link" + numVar);
                }
                else {
                    System.out.println("Unsupported operation: " + op);
                }
            }
        }`
        System.out.println("sys halt");
        
        
    }
}
}