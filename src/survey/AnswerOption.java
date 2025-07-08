package src.survey;

import java.util.Map;

public class AnswerOption {
    private String text;
    private Map<PoliticalParty, Double> weights;

    public String getText() {
        return text;
    }

    public Map<PoliticalParty, Double> getWeights() {
        return weights;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setWeights(Map<PoliticalParty, Double> weights) {
        this.weights = weights;
    }
}

