package src.classifier;

import src.survey.PoliticalParty;

import java.io.*;
import java.util.*;

public class ClassifierEvaluator {

    /**
     * Runs a 5-fold cross-validation on the given model,
     * printing per-instance predictions and reporting fold & average accuracy.
     */
    public static void evaluate(CrossValClassifierFactory factory, String csvFilePath) {
        List<List<String>> allAnswers = new ArrayList<>();
        List<PoliticalParty> allLabels = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String header = br.readLine();
            if (header == null) {
                System.err.println("No data in CSV.");
                return;
            }
            String[] headerCols = header.split(",");
            int numQuestions = headerCols.length - 1;

            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String[] tokens = line.split(",");
                if (tokens.length != headerCols.length) {
                    System.out.printf("Skipping line %d: expected %d cols but got %d\n",
                        lineNum, headerCols.length, tokens.length);
                    continue;
                }
                List<String> answers = new ArrayList<>(numQuestions);
                for (int i = 0; i < numQuestions; i++) {
                    String tok = tokens[i].trim();
                    answers.add(tok.isEmpty() ? "?" : tok);
                }
                try {
                    PoliticalParty label = PoliticalParty.valueOf(tokens[numQuestions].trim());
                    allAnswers.add(answers);
                    allLabels.add(label);
                } catch (IllegalArgumentException iae) {
                    System.out.printf("Skipping line %d: unknown party '%s'%n",
                        lineNum, tokens[numQuestions]);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV: " + e.getMessage());
            return;
        }

        int N = allAnswers.size();
        if (N == 0) {
            System.out.println("No valid responses to evaluate.");
            return;
        }
        System.out.printf("Loaded %d responses; beginning 5-fold cross-validation…%n", N);

        // Shuffle it up
        List<Integer> indices = new ArrayList<>(N);
        for (int i = 0; i < N; i++) indices.add(i);
        Collections.shuffle(indices, new Random(42));

        int k = 5;
        double totalAcc = 0.0;

        for (int fold = 0; fold < k; fold++) {
            // Split into train/test for this fold
            List<List<String>> trainX = new ArrayList<>();
            List<PoliticalParty>  trainY = new ArrayList<>();
            List<List<String>> testX  = new ArrayList<>();
            List<PoliticalParty>  testY  = new ArrayList<>();

            int start = fold * N / k;
            int end   = (fold + 1) * N / k;

            for (int i = 0; i < N; i++) {
                int idx = indices.get(i);
                if (i >= start && i < end) {
                    testX.add(allAnswers.get(idx));
                    testY.add(allLabels.get(idx));
                } else {
                    trainX.add(allAnswers.get(idx));
                    trainY.add(allLabels.get(idx));
                }
            }

            System.out.printf("%n--- Fold %d: train=%d, test=%d --- %n",
                fold + 1, trainX.size(), testX.size());

            // Train a fresh model
            Classifier model = factory.create();
            model.train(trainX, trainY);

            // Evaluate & print per-instance
            int correct = 0;
            for (int i = 0; i < testX.size(); i++) {
                List<String> feat = testX.get(i);
                PoliticalParty pred = model.predict(feat);
                PoliticalParty actual = testY.get(i);

                String featStr = String.join(",", feat);
                System.out.printf("  [%2d] %-20s → %-25s (actual: %-25s)%n",
                    i + 1, featStr, pred.getDisplayName(), actual.getDisplayName());

                if (pred == actual) correct++;
            }

            double acc = (double) correct / testX.size();
            totalAcc += acc;
            System.out.printf("Fold %d accuracy: %.2f%% (%d/%d)%n",
                fold + 1, acc * 100, correct, testX.size());
        }

        double avgAcc = totalAcc / k;
        System.out.printf("%n>>> Average accuracy over %d folds: %.2f%%%n", k, avgAcc * 100);
    }

    /**
     * A factory interface so we can get a fresh classifier (weighted or not)
     * for each fold without having to re-pass all constructors here.
     */
    public interface CrossValClassifierFactory {
        Classifier create();
    }
}
