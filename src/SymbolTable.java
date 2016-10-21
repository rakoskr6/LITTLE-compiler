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

}
