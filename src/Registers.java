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
    private int maxRegs = 4;
    private AntlrGlobalListener agl;


    public Registers() {
        dirty = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
        registers = new ArrayList<String>(Arrays.asList("","","",""));
        spilled = new ArrayList<String>();
        this.agl = agl;

        if (debug) {
       	 	System.out.println(";---Initilization---");
        	System.out.println(";Registers: " + registers);
        	System.out.println(";Dirty:     " + dirty + "\n");
    	}
    }

    private void setRegister(int regNum, String regName, int dirty) {
    	if ((regNum < maxRegs) && ((dirty == 0) || (dirty == 1))) {
	    	this.registers.set(regNum,regName);
	    	this.dirty.set(regNum,dirty);
    	}
    	else {
    		System.out.println("Incorrect register or dirty indicator");
    	}

    	if (debug) {
        	System.out.println(";Registers after line: " + registers);
        	System.out.println(";Dirty:                " + this.dirty);
    	}
    }


    public int getRegister(String regName, String usedReg1, String usedReg2, String usedReg3) { // gets register to use
    	    	
    	if(this.spilled.contains(regName)) { // if variable was spilled, reload into register
    		spill(regName,usedReg1,usedReg2,usedReg3);
            this.spilled.remove(regName);

            int i = this.registers.indexOf("");

            if (regName.startsWith("$T")) {
                System.out.println("move $-" + regName.replaceAll("\\D+","") + " r" + i);
            }
            else {
                System.out.println("move " + regName + " r" + i);
            }

            setRegister(i,regName,0);
    	}
    	else if(!this.registers.contains(regName)) { // if not spilled and not in register, load into register
	    		spill(regName,usedReg1,usedReg2,usedReg3);
	    		setRegister(this.registers.indexOf(""),regName,1);
	    }
	    // else in register and nothing needed

	    return this.registers.indexOf(regName);
    }
    public int getRegister(String regName, String usedReg1) {  
        return getRegister(regName, usedReg1, "","");
    }

    public int getRegister(String regName) {  
        return getRegister(regName, "", "","");
    }


    public void spill(String regName, String usedReg1, String usedReg2, String usedReg3) { 
	    if(this.registers.indexOf("") != -1) { // there is a free register
	    	return;
	    }
	    else {
	    	//System.out.println("No free registers, " + usedReg1 + ", " + usedReg2 + " used");
            for (int i=0; i < maxRegs; i++) { // find register to spill that is not already used
                String curReg = this.registers.get(i);
                if ((!curReg.equals(usedReg1)) && (!curReg.equals(usedReg2)) && (!curReg.equals(usedReg3))) {
                    System.out.println(";SPILLED in " + regName + " " + curReg);
                    System.out.println(";Keep safe " + usedReg1 + ", " + usedReg2 + ", " + usedReg3);

                    this.spilled.add(curReg);

                    if (curReg.startsWith("$T")) {
                        int x = TinyGen.numVarInScope + Integer.parseInt(curReg.replaceAll("\\D+",""));
                        System.out.println("move r" + i + " $-" + x);
                    }
                    else {
                        System.out.println("move r" + i + " " + curReg);
                    }
                    
                    this.dirty.set(i,0);
                    setRegister(i,"",0);
                    return;
                }
            }
	    	
	    }

    }

    public void switchIfRegisters(String opd1, String res, String opd1Org, String resOrg) {
        if (opd1.matches("r[0-9]") && res.matches("r[0-9]")) { // if both registers, will want to copy
            int opd1Num = Integer.parseInt(opd1.replaceAll("\\D+",""));
            int resNum = Integer.parseInt(res.replaceAll("\\D+",""));

            this.debug = false;
            setRegister(opd1Num, resOrg, this.dirty.get(resNum));
            this.debug = true;
            setRegister(resNum, "", 0);
        }
    }

    public void spillAll() {
        System.out.println(";Spill All"); 
        for (int i = 0; i < maxRegs; i++) {
            if (this.registers.get(i) != "") {
                this.spilled.add(this.registers.get(i));
                if (this.registers.get(i).startsWith("$T")) {
                    int x = TinyGen.numVarInScope + Integer.parseInt(this.registers.get(i).replaceAll("\\D+",""));
                    System.out.println("move r" + i + " $-" + x);
                }
                else {
                    System.out.println("move r" + i + " " + this.registers.get(i));
                }
                setRegister(i,"",0);
            }
        }
    }


    public void updateLive(HashSet<String> liveSet) {
        int alive = 0;
        for (int i=0; i < maxRegs; i++) { // for all registers
            alive = 0;
            for (String s : liveSet) { // compare to all in liveliness set
                if (s.compareTo(this.registers.get(i)) == 0) { // if in livliness set
                    alive = 1;
                }
            }
            if (alive == 0) { // if dead, save and remove from registers
                if (this.registers.get(i) != "") { // if register is not empty
                    System.out.println(";-- Updating livliness --");
                    if (this.registers.get(i).startsWith("$T")) {
                        int x = TinyGen.numVarInScope + Integer.parseInt(this.registers.get(i).replaceAll("\\D+",""));
                        System.out.println("move r" + i + " $-" + x);
                    }
                    else {
                        System.out.println("move r" + i + " " + this.registers.get(i));
                    }
                    setRegister(i,"",0);
                }
            }
        }

    }


}