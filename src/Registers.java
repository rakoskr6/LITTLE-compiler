import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class Registers {
	private List <Integer> dirty;
	private List <String> registers;
	private boolean debug = true;

    public Registers() {
        dirty = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
        registers = new ArrayList<String>(Arrays.asList("","","",""));

        if (debug) {
       	 	System.out.println("---Initilization---");
        	System.out.println("Registers: " + registers);
        	System.out.println("Dirty:     " + dirty + "\n");
    	}
    }

    private void setRegister(int regNum, String regName, int dirty) {
    	if ((regNum < 4) && ((dirty == 0) || (dirty == 1))) {
	    	this.registers.set(regNum,regName);
	    	this.dirty.set(regNum,dirty);
    	}
    	else {
    		System.out.println("Incorrect register or dirty indicator");
    	}

    	if (debug) {
    		System.out.println("---Current Registers---");
        	System.out.println("Registers: " + registers);
        	System.out.println("Dirty:     " + this.dirty + "\n");
    	}
    }


    public void newRegister(String regName, int dirty) {
    	if(this.registers.indexOf("") != -1) { // there is a free register
    			setRegister(this.registers.indexOf(""),regName,dirty);
    		}
    		else {
    			System.out.println("No free register for " + regName);
    		}
    }

    public void newRegister(String regName) {
    	newRegister(regName,1); //default to dirty register
    }
}