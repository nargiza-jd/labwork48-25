package kg.attractor.java.model;

public class Candidate {
    private String id;
    private String name;
    private String image;
    private int votes;

    public Candidate(String id, String name, String image, int votes) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.votes = votes;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public int getVotes() { return votes; }
    public void setVotes(int votes) { this.votes = votes; }
}