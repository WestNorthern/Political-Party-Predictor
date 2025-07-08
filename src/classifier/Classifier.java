package src.classifier;

import java.util.List;
import src.survey.PoliticalParty;

public interface Classifier {
    /**
     * Train the classifier on existing labeled survey data.
     * Each training example is a list of answers
     * and a known political party.
     */
    void train(List<List<String>> answers, List<PoliticalParty> labels);

    /**
     * Predict the political party given a new list of answers (e.g. ["B", "D", "A", ...]).
     */
    PoliticalParty predict(List<String> answers);
}
