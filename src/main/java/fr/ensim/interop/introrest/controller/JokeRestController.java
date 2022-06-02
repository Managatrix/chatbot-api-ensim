package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.joke.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JokeRestController {

    // JokesList jokesList = mapper.readValue
    // Map db = mapper.readValue(jsonString, Joke.class);
    static Map<Integer, Joke> jokes;
    static {
        jokes = new HashMap<>();
        jokes.put(1, new Joke(1, "How do you comfort a JavaScript bug?",
                "You console it.", 6));
        jokes.put(2, new Joke(2, "Why did the child component have such great self-esteem?",
                "Because its parent kept giving it `props`!", 2));
        jokes.put(3, new Joke(3, "Why do C# and Java developers keep breaking their keyboards",
                "Because they use a strongly typed language", 7));
        jokes.put(4, new Joke(4, "Why did the functional component feel lost?",
                "Because it didn't know what `state` it was in!", 1));
        jokes.put(5, new Joke(5, "Why was the JavaScript developer sad?",
                "Because he didn't Node how to Express himself!", 5));
        jokes.put(6, new Joke(6, "Why did the developer go broke?",
                "Because he used up all his cache!", 10));
        jokes.put(7, new Joke(7, "Why did the React Higher Order Component give up?",
                "Because it sur-rendered to the prop-aganda!", 5));
        jokes.put(8, new Joke(8, "When a JavaScript date has gone bad",
                "Don't call me, I'll callback you. I promise!", 8));
        jokes.put(9, new Joke(9, "Why did the react class component feel relieved?",
                "Because it was now off the hook.", 3));
        jokes.put(10, new Joke(10, "Why did the react developer have an addiction?",
                "Because they were completely hooked on the hooks proposal.", 3));
    }

    // private Map<Integer, Joke> jokeDatabase = new ConcurrentHashMap<>();
    // private AtomicInteger fakeSequence = new AtomicInteger(0);
    Random generator = new Random();
    Object[] values = jokes.values().toArray();
    Joke randomJoke;

    @GetMapping(value = "/randomJoke", params = { "isGoodJokeRequested" })
    public ResponseEntity<Joke> randomJoke(@RequestParam("isGoodJokeRequested") boolean isGoodJokeRequested) {
        randomJoke = (Joke) values[generator.nextInt(values.length)];
        if (isGoodJokeRequested) {
            while (randomJoke.getRating() < 5) {
                randomJoke = (Joke) values[generator.nextInt(values.length)];
            }
        } else {
            while (randomJoke.getRating() > 5) {
                randomJoke = (Joke) values[generator.nextInt(values.length)];
            }
        }
        return ResponseEntity.ok(randomJoke);
    }
}
