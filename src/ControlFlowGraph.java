import java.util.*;
import java.lang.*;
import java.io.*;

class ControlFlowGraph {
    private HashMap<IRNode,ControlFlowNode> graph = new HashMap<IRNode,ControlFlowNode>();
    private HashMap<IRNode,ControlFlowNode> statementGraph = new HashMap<IRNode,ControlFlowNode>();
    private ArrayList<IRNode> wlist = new ArrayList<IRNode>();
    private ArrayList<IRNode> slist = new ArrayList<IRNode>();

    public ControlFlowGraph(ArrayList<IRNode> worklist, IRList irlist, int endOfFuncNum) {
        this.wlist = new ArrayList<IRNode>(worklist);
        generateNodes(new ArrayList<IRNode>(worklist), irlist, endOfFuncNum);
        constructEdges(worklist);
        printControlFlowGraph(true);
        convertBlockCFGtoStatementCFG(worklist);
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
        if(cfn != null) {
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

    public void setGraph(HashMap<IRNode,ControlFlowNode> newgraph) {
        this.graph = newgraph;
    }

    private void generateNodes(ArrayList<IRNode> worklist, IRList irlist, int endOfFuncNum) {
        while(worklist.size() > 1) {
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
            for(int i = leaderNode.getStatementNum(); i < endOfFuncNum; ++i) {
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
                statementGraph.put(statement, statementNode);
            }
        }
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