import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.String;
import java.io.*;

class SymbolTable { // This symbol table contains a list of declarations
    public String scope;
    public List<SymbolObject> objectList;

    public SymbolTable(String scope) {
        this.scope = scope;
        this.objectList = new ArrayList<SymbolObject>();
    }

    public void addObject(SymbolObject obj) {
        this.objectList.add(obj);
    }

    public String getScopeRegByVarName(String varName) {
        String scopeReg = "";
        for (int x = 0; x < this.objectList.size(); x++) {
            if(this.objectList.get(x).varName.equals(varName)) {
                scopeReg = this.objectList.get(x).scopeReg;
            }
        }
        return scopeReg;
    }

    public ArrayList<String> getGlobals() {
        ArrayList<String> globalList = new ArrayList<String>();
        for (int x = 0; x < this.objectList.size(); x++) {
            if(this.objectList.get(x).scopeReg.equals("GLOB")) {
                globalList.add(this.objectList.get(x).varName);
            }
        }
        return globalList;
    }

    public void printSymbolTable() {
        System.out.println("Symbol table " + this.scope);
        for (int x = 0; x < this.objectList.size(); x++) {
            this.objectList.get(x).print();
        }
        System.out.println();
    }

    public int getNumVarsInScope() {
        int num = 0;
        for (int x = 0; x < this.objectList.size(); x++) {
            if(this.objectList.get(x).scopeReg.startsWith("$L")) {
                num++;

            }
        }

        return num;
    }

    public int getNumParamsInScope() {
        int num = 0;
        for (int x = 0; x < this.objectList.size(); x++) {
            if(this.objectList.get(x).scopeReg.startsWith("$P")) {
                num++;

            }
        }

        return num;
    }


    public int getNumInScope() {
        int num = 0;
        for (int x = 0; x < this.objectList.size(); x++) {
            if(this.objectList.get(x).scopeReg.startsWith("$")) {
                num++;

            }
        }

        return num;
    }


}
