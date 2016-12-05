import java.util.*;
import java.lang.*;
import java.io.*;

class ControlFlowNode {
    private ArrayList<IRNode> statementList = new ArrayList<IRNode>();
    private ArrayList<ControlFlowNode> successorList = new ArrayList<ControlFlowNode>();
    private ArrayList<ControlFlowNode> predecessorList = new ArrayList<ControlFlowNode>();

    private HashSet<String> genSet = new HashSet<String>();
    private HashSet<String> killSet = new HashSet<String>();

    private HashSet<String> inSet = new HashSet<String>();
    private HashSet<String> outSet = new HashSet<String>();

    public ControlFlowNode(ControlFlowNode cfn) {
        this.statementList = cfn.statementList;
        this.successorList = cfn.successorList;
        this.predecessorList = cfn.predecessorList;
        this.genSet = cfn.genSet;
        this.killSet = cfn.killSet;
        this.inSet = cfn.inSet;
        this.outSet = cfn.outSet;
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
    public void appendToKillSet(String reg) {
        this.killSet.add(reg);
    }
    public void appendToGenSet(String reg) {
        this.genSet.add(reg);
    }
    public void appendToInSet(String reg) {
        this.inSet.add(reg);
    }
    public void appendToOutSet(String reg) {
        this.outSet.add(reg);
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
    public HashSet<String> getKillSet() {
        return this.killSet;
    }
    public HashSet<String> getGenSet() {
        return this.genSet;
    }
    public HashSet<String> getInSet() {
        return this.inSet;
    }
    public HashSet<String> getOutSet() {
        return this.outSet;
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
    public void setKillSet(HashSet<String> newKill) {
        this.killSet = newKill;
    }
    public void setGenSet(HashSet<String> newGen) {
        this.genSet = newGen;
    }
    public void setInSet(HashSet<String> newIn) {
        this.inSet = newIn;
    }
    public void setOutSet(HashSet<String> newOut) {
        this.outSet = newOut;
    }

    public void clearSuccessorList() {
        this.successorList.clear();
    }
    public void clearKillSet() {
        this.killSet.clear();
    }
    public void clearGenSet() {
        this.genSet.clear();
    }
    public void clearInSet() {
        this.inSet.clear();
    }
    public void clearOutSet() {
        this.outSet.clear();
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
            System.out.println("Printing gen set:");
            System.out.println(genSet);
            System.out.println("Printing kill set:");
            System.out.println(killSet);
            System.out.println("Printing in set:");
            System.out.println(inSet);
            System.out.println("Printing out set:");
            System.out.println(outSet);
        }
        System.out.println();
    }
    public void printStatementList() {
        for(IRNode inode : statementList)
            inode.printIRNode();
    }
}