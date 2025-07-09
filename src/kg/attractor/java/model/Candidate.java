package kg.attractor.java.model;

import java.util.Objects;

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

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImage(String image) { this.image = image; }
    public void setVotes(int votes) { this.votes = votes; }

    @Override
    public String toString() {
        return "Candidate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", votes=" + votes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return votes == candidate.votes &&
                Objects.equals(id, candidate.id) &&
                Objects.equals(name, candidate.name) &&
                Objects.equals(image, candidate.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image, votes);
    }
}