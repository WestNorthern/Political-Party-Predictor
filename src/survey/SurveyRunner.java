package src.survey;

import java.util.*;
import java.io.*;
import src.classifier.Classifier;
import src.classifier.NaiveBayesClassifier;

public class SurveyRunner {

    private List<Question> questions;
    private List<String> responses;
    private Scanner scanner;

    public SurveyRunner(List<Question> questions) {
        this.questions = questions;
        this.responses = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void runSurvey() {
        System.out.println("Welcome to the Political Party Predictor!");
        System.out.println("Please answer the following questions honestly, even if it indicates you are morally bankrupt.\n");

        // Collect user responses
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println("Q" + (i + 1) + ": " + q.getPrompt());

            List<AnswerOption> options = q.getOptions();
            for (int j = 0; j < options.size(); j++) {
                System.out.println("  " + (char) ('A' + j) + ". " + options.get(j).getText());
            }

            String answer = getUserChoice(options.size());
            responses.add(answer);
        }

        // Predict using trained Naive Bayes classifier
        PoliticalParty predictedParty = predictWithClassifier(true);
        System.out.println("\nBased on your survey, the classifier predicts: " +
                predictedParty.getDisplayName() + "\n");

        // Ask for actual affiliation and save
        PoliticalParty actualParty = promptForActualParty();
        System.out.println("You've let us know that you're a " + actualParty.getDisplayName() + ".");
        saveResponse(responses, actualParty);
        System.out.println("Your responses have been saved. Thank you for participating!");
    }

    private PoliticalParty predictWithClassifier(boolean useWeights) {
        // Load historical data
        List<List<String>> trainAnswers = new ArrayList<>();
        List<PoliticalParty> trainLabels = new ArrayList<>();

        File csv = new File("data/responses.csv");
        if (csv.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
                String header = br.readLine(); // skip over the header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length < questions.size() + 1) continue;

                    List<String> row = new ArrayList<>();
                    for (int i = 0; i < questions.size(); i++) {
                        String tok = tokens[i].trim();
                        row.add(tok.isEmpty() ? "?" : tok);
                    }
                    try {
                        PoliticalParty label = PoliticalParty.valueOf(tokens[questions.size()].trim());
                        trainAnswers.add(row);
                        trainLabels.add(label);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading historical data: " + e.getMessage());
            }
        }

        // Train classifier
        Classifier classifier = new NaiveBayesClassifier(questions, useWeights);
        classifier.train(trainAnswers, trainLabels);

        // Predict for current responses
        return classifier.predict(responses);
    }

    private PoliticalParty promptForActualParty() {
        PoliticalParty actual = null;
        while (actual == null) {
            System.out.println("Please choose your political affiliation:");
            PoliticalParty[] parties = PoliticalParty.values();
            for (int i = 0; i < parties.length; i++) {
                System.out.println((i + 1) + ". " + parties[i].getDisplayName());
            }
            System.out.print("Your choice (1-" + parties.length + "): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= parties.length) {
                    actual = parties[choice - 1];
                } else {
                    System.out.println("Enter a number between 1 and " + parties.length + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input; please enter a number.");
            }
        }
        return actual;
    }

    private String getUserChoice(int optionCount) {
        while (true) {
            System.out.print("Your answer (A-" + (char) ('A' + optionCount - 1) + "): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.length() == 1) {
                char c = input.charAt(0);
                if (c >= 'A' && c < 'A' + optionCount) {
                    return input;
                }
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }

    private void saveResponse(List<String> answers, PoliticalParty party) {
        File file = new File("data/responses.csv");
        file.getParentFile().mkdirs();

        boolean writeHeader = !file.exists() || file.length() == 0;
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            if (writeHeader) {
                for (int i = 0; i < answers.size(); i++) {
                    pw.print("Q" + (i + 1) + ",");
                }
                pw.println("Label");
            }
            for (String ans : answers) pw.print(ans + ",");
            pw.println(party.name());
        } catch (IOException e) {
            System.err.println("Error saving survey response: " + e.getMessage());
        }
    }
}
