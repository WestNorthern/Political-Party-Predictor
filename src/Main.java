package src;

import src.survey.*;
import src.classifier.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Political Party Predictor!");
        System.out.println("Choose an option:");
        System.out.println("1. Take the survey");
        System.out.println("2. Run classifier evaluation (weighted, 5-fold CV)");
        System.out.println("3. Run classifier evaluation (unweighted, 5-fold CV)");
        System.out.println("4. Exit");

        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine().trim();

        List<Question> questions = SurveyLoader.loadQuestions("data/questions.json");

        switch (choice) {
            case "1": {
                SurveyRunner runner = new SurveyRunner(questions);
                runner.runSurvey();
                break;
            }

            case "2": {
                // Weighted classifier with 5-fold cross-validation
                ClassifierEvaluator.evaluate(
                    () -> new NaiveBayesClassifier(questions, true),
                    "data/responses.csv"
                );
                break;
            }

            case "3": {
                // Unweighted classifier with 5-fold cross-validation
                ClassifierEvaluator.evaluate(
                    () -> new NaiveBayesClassifier(questions, false),
                    "data/responses.csv"
                );
                break;
            }

            case "4":
                System.out.println("Goodbye!");
                break;

            default:
                System.out.println("Invalid choice. Run again and try 1, 2, or 3.");
        }
    }
}