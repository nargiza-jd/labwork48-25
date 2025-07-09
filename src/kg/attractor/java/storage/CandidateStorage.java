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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static List<Candidate> loadCandidates() {
        if (!Files.exists(FILE_PATH)) {
            List<Candidate> emptyList = new ArrayList<>();
            emptyList.add(new Candidate("1", "Markus Sillman", "markus_sillman.jpg", 0));
            emptyList.add(new Candidate("2", "Nikita Culler", "nikita_culler.jpg", 0));
            emptyList.add(new Candidate("3", "Tawanna Melanson", "tawanna_melanson.jpg", 0));
            emptyList.add(new Candidate("4", "Brunilda Mikels", "brunilda_mikels.jpg", 0));
            emptyList.add(new Candidate("5", "Hubert Takahashi", "hubert_takahashi.jpg", 0));
            emptyList.add(new Candidate("6", "Hershel Caffrey", "hershel_caffrey.jpg", 0));
            saveCandidates(emptyList);
            return emptyList;
        }
        try (Reader reader = Files.newBufferedReader(FILE_PATH)) {
            Type type = new TypeToken<List<Candidate>>() {}.getType();
            List<Candidate> candidates = GSON.fromJson(reader, type);
            return candidates != null ? candidates : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Ошибка при чтении JSON файла: " + FILE_PATH + ". Возвращен пустой список.");
            return new ArrayList<>();
        } catch (JsonSyntaxException e) {
            System.err.println("Ошибка синтаксиса JSON в файле: " + FILE_PATH + ". Возможно, файл поврежден или пуст. Возвращен пустой список.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveCandidates(List<Candidate> candidates) {
        Path parentDir = FILE_PATH.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Не удалось создать директории для: " + FILE_PATH);
                return;
            }
        }

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