/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cakehaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author jouri
 */
public class ServerConnection {

    private Socket server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ServerConnection(Socket s, String username) throws IOException {
        server = s;
        this.username = username;
        in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        out = new PrintWriter(server.getOutputStream(), true);
    }

    // send a message to the server
    public void send(String message) {
        out.println(message);
    }

    // read a message from the server
    public String readLine() throws IOException {
        return in.readLine();
    }

    public String getUsername() {
        return username;
    }

    public void close() {
        try {
            out.close();
            in.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
