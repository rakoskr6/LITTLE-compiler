import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.String;
import java.io.*;

class SymbolObject { // This class contains variable name, type, and (optionally) value
    public String varName;
    public String varType;
    public String varValue;

    public SymbolObject(String varType, String varName) {
        this.varType = varType;
        this.varName = varName;
    }

    public SymbolObject(String varType, String varName, String varValue) {
        this.varType = varType;
        this.varName = varName;
        this.varValue = varValue;
    }

    public void print() {
        System.out.print("name " + this.varName + " ");
        System.out.print("type " + this.varType);
        if (this.varValue != null) {
            System.out.print(" value " + varValue);
        }
        System.out.println("");
    }
}
