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

    public void printSymbolTable() {
        System.out.println("Symbol table " + this.scope);
        for (int x = 0; x < this.objectList.size(); x++) {
            this.objectList.get(x).print();
        }
        System.out.println();
    }

}
