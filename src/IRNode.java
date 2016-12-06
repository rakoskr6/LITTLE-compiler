// File name:   IRNode.java
// Updated:     19 October 2016
// Authors:     Brian Rieder
//              Kyle Rakos
// Description: Intermediate representation node

import java.util.*;
import java.lang.String;
import java.io.*;

public class IRNode {
    private String opcode;
    private String operand1;
    private String operand2;
    private String result;
    private int statementNum;

    public IRNode (String opcode, String operand1, String operand2, String result) {
        this.opcode   = opcode;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.result   = result;
        this.statementNum = 0;
    }

    public void printIRNode() {
        System.out.printf("%-20s %-20s %-20s %-20s %-20s\n", "opcode: " + this.opcode + ";", "operand1: " + this.operand1 + ";", "operand2: " + this.operand2 + ";", "result: " + this.result + ";", "statementNum: " + this.statementNum);
    }
    public String getIRString() {
        return this.opcode + " " + this.operand1 + " " + this.operand2 + " " + this.result;
    }

    public String getOpcode() {
        return this.opcode;
    }
    public String getOperand1() {
        return this.operand1;
    }
    public String getOperand2() {
        return this.operand2;
    }
    public String getResult() {
        return this.result;
    }
    public Integer getStatementNum() {
        return this.statementNum;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }
    public void setOperand1(String operand1) {
        this.operand1 = operand1;
    }
    public void setOperand2(String operand2) {
        this.operand2 = operand2;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public void setStatementNum(Integer statementnum) {
        this.statementNum = statementnum;
    }

    @Override
    public int hashCode() {
        return this.opcode.hashCode() + this.operand1.hashCode() + this.operand2.hashCode() + this.result.hashCode() * this.statementNum;
    }
    @Override
    public boolean equals(Object inode) {
        if (this.hashCode() == inode.hashCode()) {
            return true;
        }
        else {
            return false;
        }
    }
}