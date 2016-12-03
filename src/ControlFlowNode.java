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
    public void appendAdjacency(ControlFlowNode cfn) {
        this.adjacencyList.add(cfn);
    }

    public ArrayList<IRNode> getStatementList() {
        return this.statementList;
    }
    public IRNode getLeaderStatement() {
        return this.statementList.get(0);
    }
    public IRNode getLastStatement() {
        return this.statementList.get(this.statementList.size()-1);
    }

    public void setStatementList(ArrayList<IRNode> slist) {
        this.statementList = slist;
    }

    public void printControlFlowNode(boolean printEdges) {
        System.out.println("Printing control flow node:");
        for(IRNode statement : statementList) {
            statement.printIRNode();
        }
        if(printEdges) {
            System.out.println("Printing leaders of edges:");
            for(ControlFlowNode cfn : adjacencyList) {
                cfn.getLeaderStatement().printIRNode();
            }
        }
        System.out.println();
    }
}