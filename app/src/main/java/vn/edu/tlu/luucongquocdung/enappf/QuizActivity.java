package vn.edu.tlu.luucongquocdung.enappf;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView questionText;
    private Button optionA, optionB, optionC, optionD, nextButton;
    private FirebaseFirestore db;
    private final List<QuizQuestion> questions = new ArrayList<>();
    private final List<Integer> shownQuestionIds = new ArrayList<>();
    private QuizQuestion currentQuestion;
    private int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionText = findViewById(R.id.questionText);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        nextButton = findViewById(R.id.nextQuestionButton);

        db = FirebaseFirestore.getInstance();

        loadQuestions();

        optionA.setOnClickListener(v -> checkAnswer(optionA.getText().toString()));
        optionB.setOnClickListener(v -> checkAnswer(optionB.getText().toString()));
        optionC.setOnClickListener(v -> checkAnswer(optionC.getText().toString()));
        optionD.setOnClickListener(v -> checkAnswer(optionD.getText().toString()));
        nextButton.setOnClickListener(v -> loadNextQuestion());
    }

    private void loadQuestions() {
        db.collection("questions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    questions.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        QuizQuestion question = doc.toObject(QuizQuestion.class);
                        questions.add(question);
                    }
                    Collections.shuffle(questions);
                    loadNextQuestion();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải câu hỏi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadNextQuestion() {
        if (!questions.isEmpty() && shownQuestionIds.size() < questions.size()) {
            do {
                currentIndex = (currentIndex + 1) % questions.size();
                currentQuestion = questions.get(currentIndex);
            } while (shownQuestionIds.contains(Integer.parseInt(currentQuestion.getId())));
            shownQuestionIds.add(Integer.parseInt(currentQuestion.getId()));

            questionText.setText(currentQuestion.getTextQuestion());

            List<String> options = new ArrayList<>();
            options.add(currentQuestion.getOptionA());
            options.add(currentQuestion.getOptionB()); // Fixed typo from getOptionsB
            options.add(currentQuestion.getOptionC());
            options.add(currentQuestion.getOptionD());
            Collections.shuffle(options);

            optionA.setText(options.get(0));
            optionB.setText(options.get(1));
            optionC.setText(options.get(2));
            optionD.setText(options.get(3));
        } else {
            questionText.setText("Hết câu hỏi!");
            optionA.setEnabled(false);
            optionB.setEnabled(false);
            optionC.setEnabled(false);
            optionD.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    private void checkAnswer(String selected) {
        if (selected.equals(currentQuestion.getCorrectAnswer())) {
            Toast.makeText(this, "Đúng!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sai. Đáp án đúng là: " + currentQuestion.getCorrectAnswer(), Toast.LENGTH_LONG).show();
        }
    }
}