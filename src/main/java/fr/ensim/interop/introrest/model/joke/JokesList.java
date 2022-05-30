package fr.ensim.interop.introrest.model.joke;

import java.util.List;

public class JokesList {
    private List<Joke> jokes;

    public List<Joke> getJokes() {
        return jokes;
    }

    public void setJokes(List<Joke> jokes) {
        this.jokes = jokes;
    }
}
