import java.util.*;
import java.lang.*;
import java.io.*;

class ControlFlowGraph {
    private HashMap<IRNode,ControlFlowNode> graph = new HashMap<IRNode,ControlFlowNode>();

    public ControlFlowGraph(ArrayList<IRNode> worklist, IRList irlist, int endOfFuncNum) {
        generateNodes(worklist, irlist, endOfFuncNum);
        printControlFlowGraph();
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

    public void printControlFlowGraph() {
        System.out.println("Printing control flow graph: ");
        for (Map.Entry<IRNode, ControlFlowNode> entry : graph.entrySet()) {
            IRNode leaderNode = entry.getKey();
            ControlFlowNode cfnode = entry.getValue();
            System.out.println("Printing leader node:");
            leaderNode.printIRNode();
            cfnode.printControlFlowNode();
        }
        System.out.println();
    }
}