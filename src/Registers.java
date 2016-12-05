import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class Registers {
	private List <Integer> dirty;
	private List <String> registers;
	private List <String> spilled;
	private boolean debug = true;

    public Registers() {
        dirty = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
        registers = new ArrayList<String>(Arrays.asList("","","",""));
        spilled = new ArrayList<String>();

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


    public int getRegister(String regName) { // gets register to use
    	    	
    	if(this.spilled.contains(regName)) { // if variable was spilled, reload into register
    		spill(regName);
    	}
    	else if(!this.registers.contains(regName)) { // if not spilled and not in register, load into register
	    		spill(regName);
	    		setRegister(this.registers.indexOf(""),regName,1);
	    }
	    // else in register and nothing needed

	    return this.registers.indexOf(regName);
    }

    public void spill(String regName) {
	    if(this.registers.indexOf("") != -1) { // there is a free register
	    	return;
	    }
	    else {
	    	// TODO
	    		// Find register used furthest away
	    		// Put onto spilled list
	    	System.out.println("No free registers (temporarily making one)");
	    	setRegister(3,"",1);

	    }

    }

}