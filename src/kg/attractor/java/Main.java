package kg.attractor.java;

import kg.attractor.java.controller.VotingController;


import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new VotingController("localhost", 8089
            ).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


