package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.joke.*;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JokeRestController {

    // JokesList jokesList = mapper.readValue
    // Map db = mapper.readValue(jsonString, Joke.class);
    static Map<Integer, Joke> jokes;
    static {
        jokes = new HashMap<>();
        jokes.put(1, new Joke(1, "How do you comfort a JavaScript bug?", "", "You console it.", 0));
        jokes.put(2, new Joke(2, "Why did the child component have such great self-esteem?", "",
                "Because its parent kept giving it `props`!", 0));
        jokes.put(3, new Joke(3, "Why do C# and Java developers keep breaking their keyboards", "",
                "Because they use a strongly typed language", 0));
    }

    // private Map<Integer, Joke> jokeDatabase = new ConcurrentHashMap<>();
    // private AtomicInteger fakeSequence = new AtomicInteger(0);
    Random generator = new Random();
    Object[] values = jokes.values().toArray();
    Joke randomJoke;

    @GetMapping("/randomJoke")
    public ResponseEntity<Joke> randomJoke() {
        randomJoke = (Joke) values[generator.nextInt(values.length)];
        return ResponseEntity.ok(randomJoke);
    }

    // @GetMapping("/equipes/{id}")
    // public ResponseEntity<Equipe> recupererEquipe(@PathVariable("id") int id) {
    // if(fakeDatabase.containsKey(id)) {
    // return ResponseEntity.ok(fakeDatabase.get(id));
    // }
}
