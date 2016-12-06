import java.util.*;
import java.lang.*;
import java.io.*;

class ControlFlowGraph {
    private HashMap<IRNode,ControlFlowNode> graph = new HashMap<IRNode,ControlFlowNode>();
    private HashMap<IRNode,ControlFlowNode> statementGraph = new HashMap<IRNode,ControlFlowNode>();
    private ArrayList<IRNode> wlist = new ArrayList<IRNode>();

    public ControlFlowGraph(ArrayList<IRNode> worklist, IRList irlist, int endOfFuncNum) {
        this.wlist = new ArrayList<IRNode>(worklist);
        generateNodes(new ArrayList<IRNode>(worklist), irlist, endOfFuncNum);
        constructEdges(worklist);
        // printControlFlowGraph(true);
        convertBlockCFGtoStatementCFG(worklist);
        generateInAndOut();
    }

    public HashMap<IRNode,ControlFlowNode> getBlockLevelGraph() {
        return graph;
    }
    public HashMap<IRNode,ControlFlowNode> getStatementLevelGraph() {
        return statementGraph;
    }
    public ControlFlowNode getCFNodeFromIRNode(IRNode inode) {
        // NOTE: returns null if IRNode is not found
        ControlFlowNode lookup = graph.get(inode);
        if(lookup != null) {
             return lookup;
        }
        else {
            for(Map.Entry<IRNode,ControlFlowNode> entry : graph.entrySet()) {
                ControlFlowNode cfnFromGraph = entry.getValue();
                for(IRNode statement : cfnFromGraph.getStatementList()) {
                    if(inode.equals(statement)) {
                        return cfnFromGraph;
                    }
                }
            }
        }
        return null;
    }
    public HashSet<String> getInSetFromIRNode(IRNode inode) {
        if(statementGraph.containsKey(inode)){
            return statementGraph.get(inode).getInSet();
        }
        else {
            return null;
        }
    }
    public HashSet<String> getOutSetFromIRNode(IRNode inode) {
        if(statementGraph.containsKey(inode)) {
            return statementGraph.get(inode).getOutSet();
        }
        else {
            return null;
        }
    }

    public void setGraph(HashMap<IRNode,ControlFlowNode> newgraph) {
        this.graph = newgraph;
    }

    private void generateNodes(ArrayList<IRNode> worklist, IRList irlist, int endOfFuncNum) {
        while(worklist.size() > 1) {
            System.out.println();
            IRNode leaderNode = worklist.remove(0);
            IRNode nextLeader = worklist.get(0);
            ControlFlowNode cfnode = new ControlFlowNode(leaderNode);
            ArrayList<IRNode> statementList = createStatementList(leaderNode, nextLeader, irlist);
            cfnode.setStatementList(statementList);
            graph.put(leaderNode, cfnode);
        }
        for(IRNode leaderNode : worklist) { // who the fuck knows why worklist.remove(0) throws an exception
            ControlFlowNode cfnode = new ControlFlowNode(leaderNode);
            ArrayList<IRNode> statementList = new ArrayList<IRNode>();
            for(int i = leaderNode.getStatementNum(); i <= endOfFuncNum; ++i) {
                statementList.add(irlist.getNode(i - 1));
            }
            cfnode.setStatementList(statementList);
            graph.put(leaderNode, cfnode);
        }
    }
    private ArrayList<IRNode> createStatementList(IRNode leaderNode, IRNode nextLeader, IRList irlist) {
        ArrayList<IRNode> statementList = new ArrayList<IRNode>();
        for(int i = leaderNode.getStatementNum(); i < nextLeader.getStatementNum(); ++i) {
            statementList.add(irlist.getNode(i - 1));
        }
        return statementList;
    }

    private void constructEdges(ArrayList<IRNode> worklist) {
        for(int i = 0; i < worklist.size(); ++i) {
            IRNode leaderNode = worklist.get(i);
            ControlFlowNode cfn = graph.get(leaderNode);

            IRNode lastStatement = cfn.getLastStatement();
            if(lastStatement.getOpcode().matches("(LE|GE|LT|GT|EQ|NE)")) {
                IRNode branch_target = new IRNode("LABEL","","",lastStatement.getResult());
                branch_target.setStatementNum(AntlrGlobalListener.labelTable.get(lastStatement.getResult()));
                cfn.appendSuccessor(graph.get(branch_target));
                graph.get(branch_target).appendPredecessor(cfn);
                // graph.put(branch_target, graph.get(branch_target).appendSuccessor(cfn));
                if(i+1 < worklist.size()) {
                    IRNode next_cmd_target = worklist.get(i+1);
                    cfn.appendSuccessor(graph.get(next_cmd_target));
                    graph.get(next_cmd_target).appendPredecessor(cfn);
                    // graph.put(next_cmd_target, graph.get(next_cmd_target).appendSuccessor(cfn));
                }
            }
            else if(lastStatement.getOpcode().equals("JUMP")) {
                IRNode jump_target = new IRNode("LABEL","","",lastStatement.getResult());
                jump_target.setStatementNum(AntlrGlobalListener.labelTable.get(lastStatement.getResult()));
                cfn.appendSuccessor(graph.get(jump_target));
                graph.get(jump_target).appendPredecessor(cfn);
                // graph.put(jump_target, graph.get(jump_target).appendSuccessor(cfn));
            }
            else { // non-PC altering
                if(i+1 < worklist.size()) {
                    IRNode next_cmd_target = worklist.get(i+1);
                    cfn.appendSuccessor(graph.get(next_cmd_target));
                    graph.get(next_cmd_target).appendPredecessor(cfn);
                    // graph.put(next_cmd_target, graph.get(next_cmd_target).appendSuccessor(cfn));
                }
            }
        }
    }

    private void convertBlockCFGtoStatementCFG(ArrayList<IRNode> worklist) {
        for(int i = 0; i < worklist.size(); ++i) {
            IRNode leaderNode = worklist.get(i);
            ControlFlowNode cfn = new ControlFlowNode(graph.get(leaderNode));

            for(int j = 0; j < cfn.getStatementList().size(); ++j) {
                IRNode statement = cfn.getStatement(j);
                ControlFlowNode statementNode = new ControlFlowNode(statement);
                if(j == 0) { // first entry in block
                    // statementNode.setPredecessorList(cfn.getPredecessorList());
                    for(ControlFlowNode predecessor : cfn.getPredecessorList()) {
                        IRNode predecessorLast = predecessor.getLastStatement();
                        if(!predecessorLast.getOpcode().matches("RET")) {
                            // statementNode.appendPredecessor(graph.get(predecessorLast));
                            if(statementGraph.get(predecessorLast) == null) {
                                statementNode.appendPredecessor(graph.get(predecessorLast));
                            }
                            else {
                                statementNode.appendPredecessor(statementGraph.get(predecessorLast));
                            }
                        }
                    }
                } 
                else if(!(j == 0) && j == cfn.getStatementList().size()-1) { // last entry in block
                    IRNode pred = cfn.getStatementList().get(j-1);
                    if(!pred.getOpcode().matches("RET")) {
                        statementNode.appendPredecessor(statementGraph.get(pred));
                        // statementNode.appendPredecessor(statementGraph.get(cfn.getStatementList().get(j-1)));
                    }
                } 
                else { // all middle entries
                    IRNode pred = cfn.getStatementList().get(j-1);
                    if(!pred.getOpcode().matches("RET")) {
                        statementNode.appendPredecessor(statementGraph.get(cfn.getStatementList().get(j-1)));
                    }
                }
                ArrayList<HashSet<String>> genAndKillSets = generateGenAndKill(statement);
                statementNode.setGenSet(genAndKillSets.get(0));
                statementNode.setKillSet(genAndKillSets.get(1));
                statementGraph.put(statement, statementNode);
            }
        }
        // successor list generation
        for(int i = 0; i < worklist.size(); ++i) {
            IRNode leaderNode = worklist.get(i);
            ControlFlowNode cfn = new ControlFlowNode(graph.get(leaderNode));

            for(int j = 0; j < cfn.getStatementList().size(); ++j) {
                IRNode statement = cfn.getStatement(j);
                ControlFlowNode statementNode = statementGraph.get(statement);
                if(!statement.getOpcode().matches("RET")) {
                    if(j == cfn.getStatementList().size()-1) {
                        for(ControlFlowNode successor : cfn.getSuccessorList()) {
                            IRNode successorLeader = successor.getLeaderStatement();
                            statementNode.appendSuccessor(statementGraph.get(successorLeader));
                        }
                    }
                    else {
                        IRNode succ = cfn.getStatementList().get(j+1);
                        statementNode.appendSuccessor(statementGraph.get(succ));
                    }
                    statementGraph.put(statement, statementNode);
                }
            }
        }
    }

    public ArrayList<HashSet<String>> generateGenAndKill(IRNode inode) {
        HashSet<String> genSet = new HashSet<String>();
        HashSet<String> killSet = new HashSet<String>();

        String opcode = inode.getOpcode();
        String operand1 = inode.getOperand1();
        String operand2 = inode.getOperand2();
        String result = inode.getResult();

        if(opcode.equals("PUSH") && !operand1.equals("")) { 
            // PUSH instructions use the variable/temporary being pushed
            genSet.add(operand1);
        }
        else if(opcode.equals("POP") && !operand1.equals("")) {
            // POP instructions define the variable/temporary being popped
            killSet.add(operand1);
        }
        else if(opcode.contains("WRITE")) {
            // WRITE instructions use their variables
            genSet.add(operand1);
        }
        else if(opcode.contains("READ")) {
            // READ instructions define their variables
            killSet.add(result);
        }
        else if(opcode.contains("JSR")) {
            // CALL instructions require special care. Because we do not analyze liveness 
            // across functions, we must make conservative assumptions about what happens 
            // function calls. In particular, we GEN any variables that may be used, and 
            // KILL any variables that must be used. The GEN set for any CALL instruction 
            // therefore contains all global variables, while the KILL set is empty.
            for(int i = AntlrGlobalListener.allSymbolTables.size()-1; i >= 0; --i) {
                SymbolTable currTable = AntlrGlobalListener.allSymbolTables.get(i);
                ArrayList<String> globList = currTable.getGlobals();
                for(String glob : globList) {
                    genSet.add(glob);
                }
            }
        }
        else if(opcode.matches("(LE|GE|LT|GT|EQ|NE)")) {
            if(!operand1.equals("") && !operand1.matches("^\\d+"))
                genSet.add(operand1);
            if(!operand2.equals("") && !operand2.matches("^\\d+"))
                genSet.add(operand2);
        }
        else if(opcode.equals("LABEL") || opcode.equals("LINK") || opcode.equals("JUMP") || opcode.equals("RET")){

        }
        else {
            if(!result.equals(""))
                killSet.add(result);
            if(!operand1.equals("") && !operand1.matches("^\\d+(?:\\.\\d+)?$"))
                genSet.add(operand1);
            if(!operand2.equals("") && !operand2.matches("^\\d+(?:\\.\\d+)?$"))
                genSet.add(operand2);
        } 
        ArrayList<HashSet<String>> returnList = new ArrayList<HashSet<String>>();
        returnList.add(genSet);
        returnList.add(killSet);
        return returnList;
    }

    public void generateInAndOut() {
        // list ordering
        ArrayList<IRNode> orderedList = generateOrderedList();
        // generating sets
        for(int i = orderedList.size()-1; i >= 0; --i) {
            ControlFlowNode cfn = statementGraph.get(orderedList.get(i));
            HashSet<String> inSet = new HashSet<String>();
            HashSet<String> outSet = new HashSet<String>();
            if(!orderedList.get(i).getOpcode().equals("RET")) {
                for(ControlFlowNode successor : cfn.getSuccessorList()) {
                    outSet.addAll(successor.getInSet());
                }
                HashSet<String> genSet = new HashSet<String>(cfn.getGenSet());
                HashSet<String> killSet = new HashSet<String>(cfn.getKillSet());
                inSet.addAll(outSet);
                inSet.removeAll(killSet);
                inSet.addAll(genSet);
            }
            else {
                for(int j = AntlrGlobalListener.allSymbolTables.size()-1; j >= 0; --j) {
                    SymbolTable currTable = AntlrGlobalListener.allSymbolTables.get(j);
                    ArrayList<String> globList = currTable.getGlobals();
                    for(String glob : globList) {
                        outSet.add(glob);
                    }
                }
            }
            cfn.setInSet(inSet);
            cfn.setOutSet(outSet);
        }

        boolean isEqual = false;
        while(!isEqual) {
            isEqual = true;
            for(int i = orderedList.size()-1; i >= 0; --i) {
                ControlFlowNode cfn = statementGraph.get(orderedList.get(i));
                HashSet<String> inSet = new HashSet<String>();
                HashSet<String> outSet = new HashSet<String>();
                if(!orderedList.get(i).getOpcode().equals("RET")) {
                    for(ControlFlowNode successor : cfn.getSuccessorList()) {
                        outSet.addAll(successor.getInSet());
                    }
                    HashSet<String> genSet = new HashSet<String>(cfn.getGenSet());
                    HashSet<String> killSet = new HashSet<String>(cfn.getKillSet());
                    inSet.addAll(outSet);
                    inSet.removeAll(killSet);
                    inSet.addAll(genSet);
                }
                else {
                    for(int j = AntlrGlobalListener.allSymbolTables.size()-1; j >= 0; --j) {
                        SymbolTable currTable = AntlrGlobalListener.allSymbolTables.get(j);
                        ArrayList<String> globList = currTable.getGlobals();
                        for(String glob : globList) {
                            outSet.add(glob);
                        }
                    }
                }
                // compare in and out
                if(!cfn.getInSet().equals(inSet) || !cfn.getOutSet().equals(outSet)) {
                    isEqual = false;
                }
                // set the node
                cfn.setInSet(inSet);
                cfn.setOutSet(outSet);
            }   
        }
    }

    private ArrayList<IRNode> generateOrderedList() {
        ArrayList<IRNode> orderedList = new ArrayList<IRNode>();
        for(Map.Entry<IRNode,ControlFlowNode> entry : statementGraph.entrySet()) {
            IRNode inode = entry.getKey();
            orderedList.add(inode);
        }
        Collections.sort(orderedList, new Comparator<IRNode>() {
            @Override
            public int compare(IRNode inode1, IRNode inode2) {
                return inode1.getStatementNum() - inode2.getStatementNum();
            }
        });
        return orderedList;
    }

    private String getScopeReg(String value) {
        for(int i = AntlrGlobalListener.allSymbolTables.size()-1; i >= 0; --i) {
            SymbolTable currTable = AntlrGlobalListener.allSymbolTables.get(i);
            String lookup = currTable.getScopeRegByVarName(value);
            if(!lookup.equals("")) 
                return lookup;
            if(!currTable.scope.contains("BLOCK") && !currTable.scope.equals("GLOBAL")) {
                i = 1; // move to GLOBAL
            }
        }
        return "";
    }

    public void printControlFlowGraph(boolean printEdges) {
        System.out.println("Printing control flow graph: --------------------------------------------------------");
        ArrayList<IRNode> printarray = new ArrayList<IRNode>();
        for(Map.Entry<IRNode,ControlFlowNode> entry : graph.entrySet()) {
            IRNode inode = entry.getKey();
            printarray.add(inode);
        }
        Collections.sort(printarray, new Comparator<IRNode>() {
            @Override
            public int compare(IRNode inode1, IRNode inode2) {
                return inode1.getStatementNum() - inode2.getStatementNum();
            }
        });
        for(IRNode inode : printarray) {
            ControlFlowNode cfn = graph.get(inode);
            cfn.printControlFlowNode(printEdges);
        }
        System.out.println();
        System.out.println();
    }

    public void printStatementControlFlowGraph(boolean printEdges) {
        System.out.println("Printing statement level control flow graph: --------------------------------------------------------");
        ArrayList<IRNode> printarray = new ArrayList<IRNode>();
        for(Map.Entry<IRNode,ControlFlowNode> entry : statementGraph.entrySet()) {
            IRNode inode = entry.getKey();
            printarray.add(inode);
        }
        Collections.sort(printarray, new Comparator<IRNode>() {
            @Override
            public int compare(IRNode inode1, IRNode inode2) {
                return inode1.getStatementNum() - inode2.getStatementNum();
            }
        });
        for(IRNode inode : printarray) {
            ControlFlowNode cfn = statementGraph.get(inode);
            cfn.printControlFlowNode(printEdges);
        }
        System.out.println();
        System.out.println();
    }
}