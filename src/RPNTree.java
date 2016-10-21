import java.util.*;
import java.lang.String;
import java.io.*;

public class RPNTree {
    private String value;
    private RPNTree left_child;
    private RPNTree right_child;

    public RPNTree() {
        this.value = "";
        this.left_child  = null;
        this.right_child = null;
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

    public static void main(String []args) {
        ShuntingYardConverter conv = new ShuntingYardConverter();
        ArrayList<String> rpn_list = conv.expressionParse("c+a*b+(a*b+c)/a+20");
        System.out.println(rpn_list);
        RPNTree tree = new RPNTree();
        tree = tree.parseRPNList(rpn_list);
        System.out.println(tree);
    }

}