package vn.edu.tlu.luucongquocdung.enappf;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {
    private Spinner topicSpinner;
    private TextView flashcardText;
    private Button saveButton, nextButton, reviewButton;
    private FirebaseFirestore db;
    private final List<Word> words = new ArrayList<>();
    private final List<Word> savedWords = new ArrayList<>();
    private int currentIndex = -1;
    private boolean showingWord = true;
    private boolean reviewMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        topicSpinner = findViewById(R.id.topicSpinner);
        flashcardText = findViewById(R.id.flashcardText);
        saveButton = findViewById(R.id.saveButton);
        nextButton = findViewById(R.id.nextButton);
        reviewButton = findViewById(R.id.reviewButton);
        db = FirebaseFirestore.getInstance();

        setupSpinner();
        loadSavedWords();

        flashcardText.setOnClickListener(v -> {
            if (currentIndex >= 0) {
                showingWord = !showingWord;
                flashcardText.setText(showingWord ? words.get(currentIndex).getWord() : words.get(currentIndex).getDef());
            }
        });

        nextButton.setOnClickListener(v -> showNextWord());
        saveButton.setOnClickListener(v -> saveWord());
        reviewButton.setOnClickListener(v -> {
            reviewMode = !reviewMode;
            words.clear();
            if (reviewMode) {
                words.addAll(savedWords);
                Collections.shuffle(words);
                currentIndex = -1;
                showNextWord();
                Toast.makeText(this, "Đã bật chế độ ôn tập", Toast.LENGTH_SHORT).show();
            } else {
                setupSpinner();
                flashcardText.setText("");
                Toast.makeText(this, "Đã quay lại chế độ bình thường", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        List<String> topics = new ArrayList<>();
        topics.add("Tất cả");
        topics.add("noun");
        topics.add("verb");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, topics);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicSpinner.setAdapter(adapter);

        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String topic = topics.get(position);
                loadWords(topic);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadWords(String topic) {
        words.clear();
        Query query = topic.equals("Tất cả") ?
                db.collection("dictionary") :
                db.collection("dictionary").whereEqualTo("pos", topic);
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Word word = doc.toObject(Word.class);
                        words.add(word);
                    }
                    Collections.shuffle(words);
                    currentIndex = -1;
                    showNextWord();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải từ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadSavedWords() {
        db.collection("saved_flashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savedWords.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Word word = doc.toObject(Word.class);
                        savedWords.add(word);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải từ đã lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveWord() {
        if (currentIndex >= 0 && !savedWords.contains(words.get(currentIndex))) {
            Word word = words.get(currentIndex);
            db.collection("saved_flashcards").document(word.getWord()).set(word)
                    .addOnSuccessListener(aVoid -> {
                        savedWords.add(word);
                        Toast.makeText(this, "Đã lưu từ!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu từ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else if (savedWords.contains(words.get(currentIndex))) {
            Toast.makeText(this, "Từ đã được lưu!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showNextWord() {
        if (!words.isEmpty()) {
            currentIndex = (currentIndex + 1) % words.size();
            showingWord = true;
            flashcardText.setText(words.get(currentIndex).getWord());
        } else {
            flashcardText.setText("Không có từ nào!");
        }
    }
}