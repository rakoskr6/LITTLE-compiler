import java.util.*;
import java.lang.*;
import java.io.*;

class ControlFlowGraph {
    private HashMap<IRNode,ControlFlowNode> graph = new HashMap<IRNode,ControlFlowNode>();

    public ControlFlowGraph(ArrayList<IRNode> worklist) {
        generateNodes(worklist);
        printControlFlowGraph();
    }

    private void generateNodes(ArrayList<IRNode> worklist) {
        while(worklist.size() > 0) {
            IRNode leaderNode = worklist.remove(0);
            ControlFlowNode cfnode = new ControlFlowNode(leaderNode);
            // add in the for loop from lecture 10, pg 9
            graph.put(leaderNode, cfnode);
        }
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