// File name:   IRList.java
// Updated:     19 October 2016
// Authors:     Brian Rieder
//              Kyle Rakos
// Description: Intermediate representation node list

import java.util.*;
import java.lang.String;
import java.io.*;

public class IRList {
    private List<IRNode> listIR = new ArrayList<IRNode>();

    public List<IRNode> getList() {
        return this.listIR;
    }

    public IRNode getNode() {
        return this.listIR.get(listIR.size()-1);
    }
    public IRNode getNode(int index) {
        return this.listIR.get(index);
    }
    public Integer getSize() {
        return this.listIR.size();
    }

    public void appendNode(IRNode node) {
        this.listIR.add(node);
    }
    public void appendNode(String opcode, String operand1, String operand2, String result) {
        this.listIR.add(new IRNode(opcode, operand1, operand2, result));
    }

    public void setNode(int index, IRNode inode) {
        this.listIR.set(index, inode);
    }
}