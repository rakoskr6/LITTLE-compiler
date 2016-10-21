import java.util.*;
import java.lang.String;
import java.io.*;

public class RPNTree {
    private String value;
    private RPNTree left_child;
    private RPNTree right_child;
    Hashtable<String, String> opHash = new Hashtable<String, String>();
    public static Integer regnum = 0;

    public RPNTree() {
        this.value = "";
        this.left_child  = null;
        this.right_child = null;
        setOpHash();
    }

    private void setOpHash() {
        this.opHash.put("/", "DIVI");
        this.opHash.put("*", "MULTI");
        this.opHash.put("+", "ADDI");
        this.opHash.put("-", "SUBI");
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
            if(root.left_child.value.matches("^\\d+(?:\\.\\d+)?$")) {
                regnum++;
                curr_list.appendNode(new IRNode("STOREI", root.left_child.value, "", "$T"+Integer.toString(regnum)));
                root.left_child.setValue("$T"+Integer.toString(regnum));
            }
            if(root.right_child.value.matches("^\\d+(?:\\.\\d+)?$")) {
                regnum++;
                curr_list.appendNode(new IRNode("STOREI", root.right_child.value, "", "$T"+Integer.toString(regnum)));
                root.right_child.setValue("$T"+Integer.toString(regnum));
            }
            regnum++;
            curr_list.appendNode(new IRNode(opHash.get(root.value), 
                root.left_child.value, root.right_child.value, "$T"+Integer.toString(regnum)));
            root.setValue("$T"+Integer.toString(regnum));
        }
        return curr_list;
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
    //     ShuntingYardConverter conv = new ShuntingYardConverter();
    //     // ArrayList<String> rpn_list = conv.expressionParse("c+a*b+(a*b+c)/a+20");
    //     ArrayList<String> rpn_list = conv.expressionParse("b*b+a");
    //     System.out.println(rpn_list);
    //     RPNTree tree = new RPNTree();
    //     tree = tree.parseRPNList(rpn_list);
    //     IRList ir = new IRList();
    //     ir = tree.rhsIRGen(ir, tree);
    //     for(IRNode inode : ir.getList()) {
    //         System.out.println(inode.getOpcode() + " " + inode.getOperand1() 
    //             + " " + inode.getOperand2() + " " + inode.getResult());
    //     }
    // }

}