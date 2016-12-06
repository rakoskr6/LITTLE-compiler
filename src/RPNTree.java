import java.util.*;
import java.lang.String;
import java.io.*;

public class RPNTree {
    private String value;
    private RPNTree left_child;
    private RPNTree right_child;
    Hashtable<String, String> opHash = new Hashtable<String, String>();
    Hashtable<String, String> opHashFloat = new Hashtable<String, String>();
    public static Integer regnum = 0;

    public RPNTree() {
        this.value = "";
        this.left_child  = null;
        this.right_child = null;
        setOpHash();
        setOpHashFloat();
    }

    private void setOpHash() {
        this.opHash.put("/", "DIVI");
        this.opHash.put("*", "MULTI");
        this.opHash.put("+", "ADDI");
        this.opHash.put("-", "SUBI");
    }

    private void setOpHashFloat() {
        this.opHashFloat.put("/", "DIVF");
        this.opHashFloat.put("*", "MULTF");
        this.opHashFloat.put("+", "ADDF");
        this.opHashFloat.put("-", "SUBF");
    }

    public RPNTree parseRPNList(ArrayList<String> rpnList) {
        Stack<RPNTree> nodeStack = new Stack<RPNTree>();
        for(String current_val : rpnList) {
            if(isOperator(current_val)) {
                RPNTree operator = new RPNTree();
                operator.setValue(current_val);
                operator.setRightChild(nodeStack.pop());
                operator.setLeftChild(nodeStack.pop());
                nodeStack.push(operator);
            }
            else {
                RPNTree operand = new RPNTree();
                operand.setValue(current_val);
                nodeStack.push(operand);
            }
        }
        return nodeStack.pop();
    }

    public IRList rhsIRGen(IRList curr_list, RPNTree root) {
        if(root.left_child != null) {
            curr_list = rhsIRGen(curr_list, root.left_child);
        }
        if(root.right_child != null) {
            curr_list = rhsIRGen(curr_list, root.right_child);
        }
        if(opHash.containsKey(root.value)) {
            String operator = root.value;
            String opd1 = root.left_child.value;
            String opd2 = root.right_child.value;
            if(opd1.matches("^\\d+(?:\\.\\d+)?$")) {
                regnum++;
                curr_list.appendNode(new IRNode("STOREI", opd1, "", "$T"+Integer.toString(regnum)));
                root.left_child.setValue("$T"+Integer.toString(regnum));
                opd1 = "$T"+Integer.toString(regnum);
            }
            if(opd2.matches("^\\d+(?:\\.\\d+)?$")) {
                regnum++;
                curr_list.appendNode(new IRNode("STOREI", opd2, "", "$T"+Integer.toString(regnum)));
                root.right_child.setValue("$T"+Integer.toString(regnum));
                opd2 = "$T"+Integer.toString(regnum);
            }
            SymbolTable st = AntlrGlobalListener.allSymbolTables.get(AntlrGlobalListener.allSymbolTables.size()-1);
            if(st.scope.equals("GLOBAL") || (opd1.contains("$T") && opd2.contains("$T"))) {
                regnum++;
                curr_list.appendNode(new IRNode(opHash.get(operator), 
                    opd1, opd2, "$T"+Integer.toString(regnum)));
                root.setValue("$T"+Integer.toString(regnum));
            }
            else {
                if(!opd1.contains("$T")) {
                    opd1 = (getScopeReg(opd1).equals("GLOB")) ? opd1 : getScopeReg(opd1);
                }
                if(!opd2.contains("$T")) {
                    opd2 = (getScopeReg(opd2).equals("GLOB")) ? opd2 : getScopeReg(opd2);
                }
                regnum++;
                curr_list.appendNode(new IRNode(opHash.get(operator), 
                    opd1, opd2, "$T"+Integer.toString(regnum)));
                root.setValue("$T"+Integer.toString(regnum));
            }
        }
        return curr_list;
    }

    public IRList rhsIRGenFloat(IRList curr_list, RPNTree root) {
        if(root.left_child != null) {
            curr_list = rhsIRGenFloat(curr_list, root.left_child);
        }
        if(root.right_child != null) {
            curr_list = rhsIRGenFloat(curr_list, root.right_child);
        }
        if(opHashFloat.containsKey(root.value)) {
            String operator = root.value;
            String opd1 = root.left_child.value;
            String opd2 = root.right_child.value;
            if(opd1.matches("^\\d+(?:\\.\\d+)?$")) {
                regnum++;
                curr_list.appendNode(new IRNode("STOREF", opd1, "", "$T"+Integer.toString(regnum)));
                root.left_child.setValue("$T"+Integer.toString(regnum));
                opd1 = "$T"+Integer.toString(regnum);
            }
            if(opd2.matches("^\\d+(?:\\.\\d+)?$")) {
                regnum++;
                curr_list.appendNode(new IRNode("STOREF", opd2, "", "$T"+Integer.toString(regnum)));
                root.right_child.setValue("$T"+Integer.toString(regnum));
                opd2 = "$T"+Integer.toString(regnum);
            }
            SymbolTable st = AntlrGlobalListener.allSymbolTables.get(AntlrGlobalListener.allSymbolTables.size()-1);
            if(!(st.scope.equals("GLOBAL") || (opd1.contains("$T") && opd2.contains("$T")))) {
                if(!opd1.contains("$T")) {
                    opd1 = (getScopeReg(opd1).equals("GLOB")) ? opd1 : getScopeReg(opd1);
                }
                if(!opd2.contains("$T")) {
                    opd2 = (getScopeReg(opd2).equals("GLOB")) ? opd2 : getScopeReg(opd2);
                }
            }
            regnum++;
            curr_list.appendNode(new IRNode(opHashFloat.get(operator), 
                opd1, opd2, "$T"+Integer.toString(regnum)));
            root.setValue("$T"+Integer.toString(regnum));
        }
        return curr_list;
    }

    private String getScopeReg(String value) {
        for(int i = AntlrGlobalListener.allSymbolTables.size()-1; i >= 0; --i) {
            SymbolTable currTable = AntlrGlobalListener.allSymbolTables.get(i);
            String lookup = currTable.getScopeRegByVarName(value);
            if(!lookup.equals("")) 
                return lookup;
            if(!currTable.scope.contains("BLOCK") && !currTable.scope.equals("GLOBAL")) {
                i = 1; // move to GLOBAL
            }
        }
        return "";
    }

    private boolean isOperator(String str) {
        return Arrays.asList("+","-","*","/").contains(str);
    }

    // Setters
    public void setValue(String value) {
        this.value = value;
    }
    public void setLeftChild(RPNTree left_child) {
        this.left_child = left_child;
    }
    public void setRightChild(RPNTree right_child) {
        this.right_child = right_child;
    }

    // Getters
    public String getValue() {
        return this.value;
    }
    public RPNTree getLeftChild() {
        return this.left_child;
    }
    public RPNTree getRightChild() {
        return this.right_child;
    }

    // public static void main(String []args) {
    //     ShuntingYardConverter converter = new ShuntingYardConverter();
    //     ArrayList<String> rpn_list = converter.expressionParse(ctx.getChild(2).getText());
    //     RPNTree rpn_tree = new RPNTree();
    //     rpn_tree = rpn_tree.parseRPNList(rpn_list);
    //     IRList ir = new IRList();
    //     if(varTypeTable.get(ctx.getChild(0)) == "INT") {
    //         ir = rpn_tree.rhsIRGen(ir, rpn_tree);
    //         if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
    //             rpn_tree.regnum++;
    //             ir.appendNode("STOREI", ctx.getChild(2).getText(), "", "$T" + Integer.toString(rpn_tree.regnum));
    //         }
    //         ir.appendNode("STOREI", "$T"+Integer.toString(rpn_tree.regnum), "", ctx.getChild(0).getText());
    //     }
    //     else {
    //         ir = rpn_tree.rhsIRGenFloat(ir, rpn_tree);
    //         if(rpn_tree.getLeftChild() == null && rpn_tree.getRightChild() == null) {
    //             rpn_tree.regnum++;
    //             ir.appendNode("STOREF", ctx.getChild(2).getText(), "", "$T" + Integer.toString(rpn_tree.regnum));
    //         }
    //         ir.appendNode("STOREF", "$T"+Integer.toString(rpn_tree.regnum), "", ctx.getChild(0).getText());
    //     }
    // }

}