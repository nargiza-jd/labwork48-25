package kg.attractor.java.storage;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import kg.attractor.java.model.Candidate;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class CandidateStorage {
    private static final Path FILE_PATH = Path.of("data/json/candidates.json");
    private static final Gson GSON = new Gson();

    public static List<Candidate> loadCandidates() {
        try (Reader reader = Files.newBufferedReader(FILE_PATH)) {
            Type type = new TypeToken<List<Candidate>>() {}.getType();
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveCandidates(List<Candidate> candidates) {
        try (Writer writer = Files.newBufferedWriter(FILE_PATH)) {
            GSON.toJson(candidates, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Candidate findById(String id, List<Candidate> candidates) {
        return candidates.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
