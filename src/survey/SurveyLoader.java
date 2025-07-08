package src.survey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class SurveyLoader {

    public static List<Question> loadQuestions(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            Gson gson = new Gson();

            Type questionListType = new TypeToken<List<Question>>() {}.getType();

            return gson.fromJson(reader, questionListType);
        } catch (IOException e) {
            System.err.println("Failed to load questions: " + e.getMessage());
            return List.of();
        }
    }
}
