package kg.attractor.java.controller;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.model.Candidate;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;
import kg.attractor.java.server.RouteHandler;
import kg.attractor.java.storage.CandidateStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class VotingController extends BasicServer {

    public VotingController(String host, int port) throws IOException {
        super(host, port);
        initRoutes();
    }

    private void initRoutes() {
        registerGet("/vote", this::votePage);
        registerPost("/vote", this::handleVote);
        registerGet("/result", this::resultPage);
        registerGet("/candidates", this::candidatesPage);
    }

    private void votePage(HttpExchange ex) throws IOException {
        List<Candidate> candidates = CandidateStorage.loadCandidates();
        renderTemplate(ex, "votes.ftlh", Map.of("candidates", candidates));
    }

    private void handleVote(HttpExchange ex) throws IOException {
        String id = getQueryParam(ex, "id");
        List<Candidate> candidates = CandidateStorage.loadCandidates();
        Candidate c = CandidateStorage.findById(id, candidates);
        if (c != null) {
            c.setVotes(c.getVotes() + 1);
            CandidateStorage.saveCandidates(candidates);
            redirect303(ex, "/result?id=" + c.getId());
        } else {
            sendText(ex, ResponseCodes.NOT_FOUND, "Кандидат не найден");        }
    }

    private void resultPage(HttpExchange ex) throws IOException {
        String id = getQueryParam(ex, "id");
        List<Candidate> candidates = CandidateStorage.loadCandidates();
        Candidate c = CandidateStorage.findById(id, candidates);
        if (c != null) {
            renderTemplate(ex, "thankyou.ftlh", Map.of("candidate", c));
        } else {
            sendText(ex, ResponseCodes.NOT_FOUND, "Результат не найден");
        }
    }

    private void candidatesPage(HttpExchange ex) throws IOException {
        List<Candidate> candidates = CandidateStorage.loadCandidates();
        renderTemplate(ex, "candidates.ftlh", Map.of("candidates", candidates));
    }


    private void renderTemplate(HttpExchange ex, String templateName, Map<String, Object> data) throws IOException {
        String content = "<html><body>Заглушка шаблона: " + templateName + "</body></html>";
        sendBytes(ex, ResponseCodes.OK, ContentType.TEXT_HTML, content.getBytes(StandardCharsets.UTF_8));
    }

    private void sendText(HttpExchange ex, ResponseCodes code, String message) throws IOException {
        sendBytes(ex, code, ContentType.TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
    }
}