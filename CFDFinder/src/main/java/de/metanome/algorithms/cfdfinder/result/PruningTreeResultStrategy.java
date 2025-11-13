package de.metanome.algorithms.cfdfinder.result;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.metanome.algorithm_integration.ColumnIdentifier;
import de.metanome.algorithm_integration.result_receiver.ConditionalFunctionalDependencyResultReceiver;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class PruningTreeResultStrategy extends ResultStrategy {

    private static String resultDir = "results/";
    private String filePath;
    private BufferedWriter writer;
    private List<ResultTree> resultTrees;

    public PruningTreeResultStrategy(ConditionalFunctionalDependencyResultReceiver resultReceiver, ObjectArrayList<ColumnIdentifier> columnIdentifiers, String fileName) {
        super(resultReceiver, columnIdentifiers);
        if (fileName != null) {
            this.filePath = resultDir + fileName;
        } else {
            this.filePath = resultDir + String.valueOf(System.nanoTime()) + ".txt";
        }
        try {
            writer = new BufferedWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getIdentifier() {
        return "PruningTreeStrategy";
    }

    @Override
    public void startReceiving() {
        super.startReceiving();
        resultTrees = new LinkedList<>();
    }

    private void append(String s) {
        try {
            writer.append(s);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Could not write to file " + filePath);
            e.printStackTrace();
        }
    }
    @Override
    public void stopReceiving() {
        super.stopReceiving();
        Set<Result> results = new HashSet<>();
        for (ResultTree resultTree : resultTrees) {
            results.addAll(resultTree.getLeaves());
        }
        for (Result result : results) {
            super.sendToMetanome(result);
            append(result.toString());
            append("\n\n");
        }
    }

    @Override
    public void receiveResult(Result result) {
        double minimalDistance = Double.MAX_VALUE;
        ResultTree minimalPosiition = null;
        for (ResultTree tree : resultTrees) {
            ResultTree position = tree.getInsertPosition(result);
            if (position != null) {
                double parentSupport = position.getNode().getPatternTableau().getSupport();
                if (parentSupport - result.getPatternTableau().getSupport() < minimalDistance) {
                    minimalDistance = parentSupport - result.getPatternTableau().getSupport();
                    minimalPosiition = position;
                }
            }
        }
        if (minimalPosiition != null) {
            minimalPosiition.insert(result);
        } else {
            resultTrees.add(new ResultTree(result));
        }
    }
}
