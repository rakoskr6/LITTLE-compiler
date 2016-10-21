import java.util.*;
import java.lang.String;
import java.io.*;

public class ShuntingYardConverter {
    private Stack<String> opStack = new Stack<String>();
    private Hashtable<String, Integer> opHierarchy = new Hashtable<String, Integer>(); 
    private ArrayList<String> rpnExpr = new ArrayList<String>();

    public ArrayList<String> expressionParse(String expression) {
        setOpHierarchy();
        ArrayList<String> tokenized_list = new ArrayList<String>();
        tokenized_list = tokenizeExpression(expression);
        runShuntingYard(tokenized_list);
        return this.rpnExpr;
    }

    private ArrayList<String> tokenizeExpression(String expression) {
        ArrayList<String> tokenized_list = new ArrayList<String>();
        StringTokenizer strtok = new StringTokenizer(expression,"/*+-()", true);
        while(strtok.hasMoreTokens()) {
            tokenized_list.add(strtok.nextToken());
        }
        return tokenized_list;
    }

    private void runShuntingYard(ArrayList<String> expression) {
        for(String current_char : expression) {
            if(current_char.equals("(")) {
                this.opStack.push("(");
            }
            else if(current_char.equals(")")) {
                while(this.opStack.peek() != "(") {
                    this.rpnExpr.add(this.opStack.pop());
                }
                this.opStack.pop();
            }
            else if(isOperator(current_char)) {
                while(!this.opStack.empty() && isOperator(this.opStack.peek()) 
                    && (opHierarchy.get(this.opStack.peek()) >= opHierarchy.get(current_char))){
                    this.rpnExpr.add(this.opStack.pop());
                }
                this.opStack.push(current_char);
            }
            else { // number
                this.rpnExpr.add(current_char);
            }
        }
        while(!opStack.empty()) {
            this.rpnExpr.add(this.opStack.pop());
        }
    }

    public ArrayList<String> getRPNExpr() {
        return this.rpnExpr;
    }

    private void setOpHierarchy() {
        opHierarchy.put("/", 3);
        opHierarchy.put("*", 3);
        opHierarchy.put("+", 2);
        opHierarchy.put("-", 2);
    }

    private boolean isOperator(String ch) {
        return this.opHierarchy.containsKey(ch);
    }

    // public static void main(String []args) {
    //     ShuntingYardConverter conv = new ShuntingYardConverter();
    //     conv.expressionParse("c+a*b+(a*b+c)/a+20");
    //     System.out.println(conv.rpnExpr);
    // }
}