package fr.ensim.interop.introrest.model.joke;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Joke {
    @JsonProperty("id")
    private int id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("content")
    private String content;
    @JsonProperty("answer")
    private String answer;
    @JsonProperty("rating")
    private int rating;

    public Joke(int id, String title, String content, String answer, int rating) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.answer = answer;
        this.rating = rating;
    }

    public Joke() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
