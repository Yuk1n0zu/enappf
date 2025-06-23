package vn.edu.tlu.luucongquocdung.enappf;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CSVImporter.importCSVToFirestore(this);

        Button dictionaryButton = findViewById(R.id.dictionaryButton);
        Button flashcardButton = findViewById(R.id.flashcardButton);
        Button quizButton = findViewById(R.id.quizButton);
        Button toggleDNDButton = findViewById(R.id.toggleDNDButton);

        dictionaryButton.setOnClickListener(v -> startActivity(new Intent(this, DictionaryActivity.class)));
        flashcardButton.setOnClickListener(v -> startActivity(new Intent(this, FlashcardActivity.class)));
        quizButton.setOnClickListener(v -> startActivity(new Intent(this, QuizActivity.class)));
        toggleDNDButton.setOnClickListener(v -> NotificationHelper.toggleDoNotDisturb(this));
    }
}