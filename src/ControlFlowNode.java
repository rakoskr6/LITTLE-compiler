import java.util.*;
import java.lang.*;
import java.io.*;

class ControlFlowNode {
    private ArrayList<IRNode> statementList = new ArrayList<IRNode>();
    private ArrayList<ControlFlowNode> successorList = new ArrayList<ControlFlowNode>();
    private ArrayList<ControlFlowNode> predecessorList = new ArrayList<ControlFlowNode>();

    public ControlFlowNode(ControlFlowNode cfn) {
        this.statementList = cfn.statementList;
        this.successorList = cfn.successorList;
        this.predecessorList = cfn.predecessorList;
    }
    public ControlFlowNode(IRNode leader) {
        this.statementList.add(leader);
    }

    public void appendStatement(IRNode inode) {
        this.statementList.add(inode);
    }
    public void appendSuccessor(ControlFlowNode cfn) {
        this.successorList.add(cfn);
    }
    public void appendPredecessor(ControlFlowNode cfn) {
        this.predecessorList.add(cfn);
    }

    public ArrayList<IRNode> getStatementList() {
        return this.statementList;
    }
    public IRNode getStatement(Integer index) {
        return this.statementList.get(index);
    }
    public IRNode getLeaderStatement() {
        return this.statementList.get(0);
    }
    public IRNode getLastStatement() {
        return this.statementList.get(this.statementList.size()-1);
    }
    public ArrayList<ControlFlowNode> getSuccessorList() {
        return this.successorList;
    }
    public ArrayList<ControlFlowNode> getPredecessorList() {
        return this.predecessorList;
    }

    public void setStatementList(ArrayList<IRNode> slist) {
        this.statementList = slist;
    }
    public void setSuccessorList(ArrayList<ControlFlowNode> succlist) {
        this.successorList = succlist;
    }
    public void setPredecessorList(ArrayList<ControlFlowNode> predlist) {
        this.predecessorList = predlist;
    }

    public void clearSuccessorList() {
        this.successorList.clear();
    }

    public void printControlFlowNode(boolean printEdges) {
        System.out.println("Printing control flow node:");
        for(IRNode statement : statementList) {
            statement.printIRNode();
        }
        if(printEdges) {
            System.out.println("Printing successor edges:");
            for(ControlFlowNode cfn : successorList) {
                // cfn.getLeaderStatement().printIRNode();
                cfn.printStatementList();
            }
            System.out.println("Printing predecessor edges:");
            for(ControlFlowNode cfn : predecessorList) {
                cfn.printStatementList();
            }
        }
        System.out.println();
    }
    public void printStatementList() {
        for(IRNode inode : statementList)
            inode.printIRNode();
    }
}