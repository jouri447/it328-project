package com.mycompany.cakehaven;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class CakeHavenServer {

    private static final int PORT = 5050; // agreed port (5000 is used by AirPlay on Mac)

    // usernames of everyone who is connected now (shared by all client threads)
    private static ArrayList<String> users = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Cake Haven Server is running on port " + PORT + "...");

            while (true) {
                Socket client = server.accept();
                Thread thread = new Thread(() -> handleClient(client));
                thread.start();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleClient(Socket client) {
        String username = null; // stays null until the name is registered
        try (
            BufferedReader input = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
            PrintWriter output = new PrintWriter(
                client.getOutputStream(), true)
        ) {
            // the client sends CONNECT:<username>
            String request = input.readLine();
            if (request == null || !request.startsWith("CONNECT:")) {
                output.println("ERROR:Expected CONNECT:username");
                return;
            }
            String name = request.substring(8).trim();
            if (name.isEmpty()) {
                output.println("ERROR:Username is empty");
                return;
            }
            if (!addUser(name)) {
                output.println("ERROR:Username already taken");
                return;
            }
            username = name;

            System.out.println(username + " connected. Online: " + users);
            output.println("CONNECTED");

            // keep the connection open until the client leaves
            while (input.readLine() != null) {
            }

        } catch (IOException e) {
            System.out.println("Client connection ended");
        } finally {
            if (username != null) {
                removeUser(username);
                System.out.println(username + " disconnected. Online: " + users);
            }
            try {
                client.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // registers the name; returns false if someone already uses it (capitals don't matter)
    private static synchronized boolean addUser(String name) {
        for (String u : users) {
            if (u.equalsIgnoreCase(name)) {
                return false;
            }
        }
        users.add(name);
        return true;
    }

    private static synchronized void removeUser(String name) {
        users.remove(name);
    }
}