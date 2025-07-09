package kg.attractor.java.controller;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.model.Candidate;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;
import kg.attractor.java.utils.TemplateEngine;
import kg.attractor.java.storage.CandidateStorage;
import kg.attractor.java.utils.Utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VotingController extends BasicServer {

    public VotingController(String host, int port) throws IOException {
        super(host, port);
        initRoutes();
    }

    private void initRoutes() {
        registerGet("/", this::displayCandidatesForVoting);
        registerGet("/vote", this::displayCandidatesForVoting);
        registerPost("/vote", this::handleVote);
        registerGet("/thankyou", this::thankYouPage);
        registerGet("/votes", this::showVotingResults);
    }

    private void displayCandidatesForVoting(HttpExchange ex) throws IOException {
        List<Candidate> candidates = CandidateStorage.loadCandidates();
        renderTemplate(ex, "candidates.ftlh", Map.of("candidates", candidates));
    }

    private void showVotingResults(HttpExchange ex) throws IOException {
        List<Candidate> candidates = CandidateStorage.loadCandidates();
        int totalVotes = candidates.stream().mapToInt(Candidate::getVotes).sum();

        List<Map<String, Object>> candidateResults = candidates.stream()
                .map(c -> {
                    double percentage = (totalVotes > 0) ? (double) c.getVotes() / totalVotes * 100 : 0;
                    Map<String, Object> candidateMap = new HashMap<>();
                    candidateMap.put("id", c.getId());
                    candidateMap.put("name", c.getName());
                    candidateMap.put("image", "/static/images/" + c.getImage());
                    candidateMap.put("votes", c.getVotes());
                    candidateMap.put("percentage", String.format("%.2f", percentage));
                    candidateMap.put("isWinner", false);
                    return candidateMap;
                })
                .sorted((c1, c2) -> Integer.compare((Integer)c2.get("votes"), (Integer)c1.get("votes")))
                .collect(Collectors.toList());

        if (totalVotes > 0 && !candidateResults.isEmpty()) {
            int maxVotes = (Integer) candidateResults.get(0).get("votes");

            for (Map<String, Object> result : candidateResults) {
                if ((Integer) result.get("votes") == maxVotes && maxVotes > 0) {
                    result.put("isWinner", true);
                } else {
                    result.put("isWinner", false);
                }
            }
        }
        renderTemplate(ex, "votes.ftlh", Map.of("candidateResults", candidateResults));
    }

    private void handleVote(HttpExchange ex) throws IOException {
        String requestBody = body(ex);
        Map<String, String> formData = Utils.parseUrlEncoded(requestBody, "&");
        String candidateId = formData.get("candidateId");

        List<Candidate> candidates = CandidateStorage.loadCandidates();
        Candidate c = CandidateStorage.findById(candidateId, candidates);

        if (c != null) {
            c.setVotes(c.getVotes() + 1);
            CandidateStorage.saveCandidates(candidates);
            redirect303(ex, "/thankyou?id=" + c.getId());
        } else {
            sendText(ex, ResponseCodes.NOT_FOUND, "Кандидат не найден.");
        }
    }

    private void thankYouPage(HttpExchange ex) throws IOException {
        String id = getQueryParam(ex, "id");
        List<Candidate> candidates = CandidateStorage.loadCandidates();
        Candidate c = CandidateStorage.findById(id, candidates);
        if (c != null) {
            renderTemplate(ex, "thankyou.ftlh", Map.of("candidate", c));
        } else {
            redirect303(ex, "/");
        }
    }

    private void sendText(HttpExchange ex, ResponseCodes code, String message) throws IOException {
        sendBytes(ex, code, ContentType.TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
    }

    private void renderTemplate(HttpExchange ex, String templateName, Map<String, Object> data) throws IOException {
        String html = TemplateEngine.render(templateName, data);
        sendBytes(ex, ResponseCodes.OK, ContentType.TEXT_HTML, html.getBytes(StandardCharsets.UTF_8));
    }
}