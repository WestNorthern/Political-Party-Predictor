package src.survey;

import java.util.List;

public class Question {
    private String prompt;
    private List<AnswerOption> options;

    public String getPrompt() {
        return prompt;
    }

    public List<AnswerOption> getOptions() {
        return options;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setOptions(List<AnswerOption> options) {
        this.options = options;
    }
}
