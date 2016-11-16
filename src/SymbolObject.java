import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.String;
import java.io.*;

class SymbolObject { // This class contains variable name, type, and (optionally) value
    public String varName;
    public String varType;
    public String varValue;
    public String scopeReg;

    public SymbolObject(String varType, String varName) {
        this.varType = varType;
        this.varName = varName;
    }

    public SymbolObject(String varType, String varName, String varValue) {
        this.varType = varType;
        this.varName = varName;
        this.varValue = varValue;
    }

    public SymbolObject(String varType, String varName, String varValue, String scopeReg) {
        this.varType = varType;
        this.varName = varName;
        this.varValue = varValue;
        this.scopeReg = scopeReg;
    }

    public void print() {
        System.out.print("name " + this.varName + " ");
        System.out.print("type " + this.varType);
        if (this.varValue != null) {
            System.out.print(" value " + this.varValue);
        }
        if (this.scopeReg != null) {
            System.out.print(" scopeReg " + this.scopeReg);
        }
        System.out.println("");
    }
}
