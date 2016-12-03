import java.util.*;
import java.lang.*;
import java.io.*;

class ControlFlowNode {
    private ArrayList<IRNode> statementList = new ArrayList<IRNode>();
    private ArrayList<ControlFlowNode> adjacencyList = new ArrayList<ControlFlowNode>();

    public ControlFlowNode(IRNode leader) {
        this.statementList.add(leader);
    }

    public void appendStatement(IRNode inode) {
        this.statementList.add(inode);
    }

    public ArrayList<IRNode> getStatementList() {
        return this.statementList;
    }

    public void setStatementList(ArrayList<IRNode> slist) {
        this.statementList = slist;
    }

    public void printControlFlowNode() {
        System.out.println("Printing control flow node:");
        for(IRNode statement : statementList) {
            statement.printIRNode();
        }
    }
}