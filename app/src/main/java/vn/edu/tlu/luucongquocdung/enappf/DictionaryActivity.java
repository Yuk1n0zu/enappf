package vn.edu.tlu.luucongquocdung.enappf;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {
    private WordAdapter adapter;
    private final List<Word> words = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        EditText searchBar = findViewById(R.id.searchBar);
        RecyclerView wordList = findViewById(R.id.wordList);
        wordList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordAdapter(words);
        wordList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadWords("");

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                loadWords(s.toString());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadWords(String query) {
        words.clear();
        db.collection("dictionary")
                .orderBy("word")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Word word = doc.toObject(Word.class);
                        words.add(word);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải từ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}