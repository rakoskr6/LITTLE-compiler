import java.util.*;
import java.lang.*;
import java.io.*;

class ControlFlowGraph {
    private HashMap<IRNode,ControlFlowNode> graph = new HashMap<IRNode,ControlFlowNode>();
    private ArrayList<IRNode> wlist = new ArrayList<IRNode>();

    public ControlFlowGraph(ArrayList<IRNode> worklist, IRList irlist, int endOfFuncNum) {
        this.wlist = new ArrayList<IRNode>(worklist);
        generateNodes(new ArrayList<IRNode>(worklist), irlist, endOfFuncNum);
        constructEdges(worklist);
        convertBlockCFGtoStatementCFG(worklist);
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
            ControlFlowNode cfn = graph.get(leaderNode);
        }
    }

    public void printControlFlowGraph(boolean printEdges) {
        System.out.println("Printing control flow graph: --------------------------------------------------------");
        for(int i = 0; i < wlist.size(); ++i) {
            IRNode leaderNode = wlist.get(i);
            ControlFlowNode cfn = graph.get(leaderNode);
            cfn.printControlFlowNode(printEdges);
        }
        System.out.println();
        System.out.println();
    }
}