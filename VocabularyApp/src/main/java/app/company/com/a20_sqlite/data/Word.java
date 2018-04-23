package app.company.com.a20_sqlite.data;

import java.io.Serializable;


public class Word implements Serializable{
    private String word = "";
    private String meaning = "";
    private String sample = "";

    public Word(String word, String meaning, String sample){
        this.word = word;
        this.meaning = meaning;
        this.sample = sample;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getSample() {
        return sample;
    }

}
