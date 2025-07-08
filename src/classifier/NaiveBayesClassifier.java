package src.classifier;

import src.survey.*;

import java.util.*;

public class NaiveBayesClassifier implements Classifier {

    private final List<Question> questions;
    private final boolean useWeights;

    private final Map<PoliticalParty, Integer> classCounts;
    private int totalExamples;
    private final Map<PoliticalParty, List<Map<String, Integer>>> featureCounts;

    public NaiveBayesClassifier(List<Question> questions, boolean useWeights) {
        this.questions = questions;
        this.useWeights = useWeights;
        this.classCounts = new EnumMap<>(PoliticalParty.class);
        this.featureCounts = new EnumMap<>(PoliticalParty.class);
        this.totalExamples = 0;

        for (PoliticalParty party : PoliticalParty.values()) {
            classCounts.put(party, 0);
            featureCounts.put(party, new ArrayList<>());
            for (int i = 0; i < questions.size(); i++) {
                featureCounts.get(party).add(new HashMap<>());
            }
        }
    }

    @Override
    public void train(List<List<String>> answers, List<PoliticalParty> labels) {
        for (int i = 0; i < answers.size(); i++) {
            List<String> response = answers.get(i);
            PoliticalParty label = labels.get(i);

            classCounts.put(label, classCounts.get(label) + 1);
            totalExamples++;

            List<Map<String, Integer>> partyFeatureCounts = featureCounts.get(label);
            for (int j = 0; j < response.size(); j++) {
                String ans = response.get(j); // DO NOT strip prefix
                Map<String, Integer> counts = partyFeatureCounts.get(j);
                counts.put(ans, counts.getOrDefault(ans, 0) + 1);
            }
        }
    }

    @Override
    public PoliticalParty predict(List<String> input) {
        double bestScore = Double.NEGATIVE_INFINITY;
        PoliticalParty bestGuess = PoliticalParty.INDEPENDENT;

        for (PoliticalParty party : PoliticalParty.values()) {
            double logProb = Math.log(classCounts.get(party) + 1.0)
                    - Math.log(totalExamples + PoliticalParty.values().length);

            for (int i = 0; i < input.size(); i++) {
                String answer = input.get(i);

                if (useWeights) {
                    int index = answer.charAt(0) - 'A';
                    if (index >= 0 && index < questions.get(i).getOptions().size()) {
                        AnswerOption option = questions.get(i).getOptions().get(index);
                        double weight = option.getWeights().getOrDefault(party, 0.01);
                        logProb += Math.log(weight);
                    }
                } else {
                    Map<String, Integer> valueCounts = featureCounts.get(party).get(i);
                    int count = valueCounts.getOrDefault(answer, 0);
                    int total = valueCounts.values().stream().mapToInt(Integer::intValue).sum();
                    int numOptions = questions.get(i).getOptions().size();
                    logProb += Math.log(count + 1.0) - Math.log(total + numOptions);
                }
            }

            if (logProb > bestScore) {
                bestScore = logProb;
                bestGuess = party;
            }
        }

        return bestGuess;
    }

    public void printSummary() {
        System.out.println("Trained on " + totalExamples + " examples.");
        for (var entry : classCounts.entrySet()) {
            System.out.println(entry.getKey().getDisplayName() + ": " + entry.getValue());
        }
    }
}
