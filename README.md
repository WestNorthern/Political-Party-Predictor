# Political Party Predictor CLI App

A Java-based command-line application that predicts a user’s political affiliation using both hand-weighted scoring and a supervised Naïve Bayes classifier. It also supports comprehensive evaluation via k-fold cross-validation and stores survey data for continuous model improvement.

---

## Features

- **Interactive Survey:** Presents a series of ideological questions with four multiple-choice options each.
- **Manual Scoring Mode:** Uses JSON-defined intensity weights to compute a quick, hand-weighted party guess.
- **Naïve Bayes Classifier:** Trains on historical responses (CSV) and predicts affiliation based on answer likelihoods.
- **5-Fold Cross-Validation:** Splits labeled data into 5 folds, runs training/testing in each, prints per-instance predictions and fold accuracies, and reports average accuracy.
- **Data Management:**
  - **questions.json**: Structured prompts, four options each, with `PoliticalParty -> weight` maps.
  - **responses.csv**: Header-driven CSV (Q1…Qn,Label) capturing each respondent’s answers and true party.

---

## Building & Running

You can build and run the app on **macOS / Linux** or **Windows**. Pick your instructions below.

---

### macOS / Linux

1. Make the build/run scripts executable:  
   ```bash
   chmod +x build.sh run.sh
   ```

2. **Build** the project:  
   ```bash
   ./build.sh
   ```
   - Removes any existing `bin/` folder.
   - Recompiles all `src/**/*.java` into `bin/` and includes any JARs under `lib/`.

3. **Run** the app:  
   ```bash
   ./run.sh
   ```
   - Uses `:` as the classpath separator to load `bin/` and everything in `lib/`.

---

### Windows (cmd.exe)

1. Open a **Command Prompt** in the project root.

2. **Build** the project:  
   ```batch
   build.bat
   ```
   - Recursively compiles `src\**\*.java` into `bin\`, using `;` as the classpath separator.

3. **Run** the app:  
   ```batch
   run.bat
   ```
   - Launches `src.Main` and pauses the console afterward so you can read the output.


## CLI Usage

```bash
$ ./run.sh
Welcome to the Political Party Predictor!
Choose an option:
1. Take the survey
2. Run classifier evaluation (weighted, 5-fold CV)
3. Run classifier evaluation (unweighted, 5-fold CV)
4. Exit
Enter your choice: 
```

1. **Take the survey** – Asks all questions, shows:  
   - Manual weighted guess  
   - Naïve Bayes guess based on existing `responses.csv`  
   - Prompts for your true affiliation and appends your answers to `responses.csv`.  

2. **Run classifier evaluation** – Performs 5-fold cross-validation:  
   - Shuffles with a fixed seed for reproducibility  
   - Trains & tests on each fold  
   - Prints each test instance’s answer vector, predicted party, and actual party  
   - Reports fold accuracies and overall average  

---

## Evaluation Metrics

- **Fold Accuracy:** Percentage of correct predictions per fold.  
- **Overall Accuracy:** Average of the five fold accuracies.  

With realistic noise levels (e.g. 20% random entries), you should see average accuracy near **80%** if the model and weights align.

---

## Future Improvements

- Integrate **Decision Trees** or **Logistic Regression** for comparison.  
- Build a **Web UI** for broader user access.  
- Implement **active learning**: selectively query users on ambiguous cases.  
- Expand to **multi-label** or **ranked-choice** response handling.  
- Add **precision/recall/F1** metrics per party for deeper performance analysis.
