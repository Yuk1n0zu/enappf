package vn.edu.tlu.luucongquocdung.enappf;

public class Word {
    private String word, pos, def;

    public Word() {}

    public Word(String word, String pos, String def) {
        this.word = word;
        this.pos = pos;
        this.def = def;
    }

    public String getWord() { return word; }
    public String getPos() { return pos; }
    public String getDef() { return def; }
    public void setWord(String word) { this.word = word; }
    public void setPos(String pos) { this.pos = pos; }
    public void setDef(String def) { this.def = def; }
}