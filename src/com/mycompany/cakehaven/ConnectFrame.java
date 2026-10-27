/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cakehaven;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author jouri
 */
public class ConnectFrame extends JFrame {

    private static final String Server_IP = "localhost";
    private static final int Server_port = 5050; // 5000 is used by AirPlay on mac

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel usernameLabel;
    private JLabel ipLabel;
    private JTextField usernameField;
    private JTextField ipField;
    private JButton connectButton;

    public ConnectFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Cake Haven - Connect");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 330);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(255, 248, 240));

        titleLabel = new JLabel("Cake Haven", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 34));
        titleLabel.setForeground(new Color(92, 61, 46));
        titleLabel.setBounds(0, 25, 420, 45);
        add(titleLabel);

        subtitleLabel = new JLabel("Fresh cakes, cupcakes & pastries", JLabel.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setBounds(0, 70, 420, 20);
        add(subtitleLabel);

        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usernameLabel.setBounds(50, 120, 90, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(145, 120, 220, 25);
        add(usernameField);

        ipLabel = new JLabel("Server IP:");
        ipLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        ipLabel.setBounds(50, 160, 90, 25);
        add(ipLabel);

        ipField = new JTextField(Server_IP);
        ipField.setBounds(145, 160, 220, 25);
        add(ipField);

        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        connectButton.setBackground(new Color(224, 122, 154));
        connectButton.setForeground(Color.WHITE);
        connectButton.setOpaque(true);
        connectButton.setBorderPainted(false);
        connectButton.setBounds(130, 215, 160, 38);
        add(connectButton);

        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });
    }

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String username = usernameField.getText().trim();
        String ip = ipField.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.");
            return;
        }
        // ':' is used in our messages (CONNECT:name) so it can't be in the name
        if (username.contains(":")) {
            JOptionPane.showMessageDialog(this, "The username can't contain ':'");
            return;
        }
        if (ip.isEmpty()) {
            ip = Server_IP;
        }

        try {
            Socket socket = new Socket(ip, Server_port);
            ServerConnection connection = new ServerConnection(socket, username);

            connection.send("CONNECT:" + username);
            String reply = connection.readLine();

            if (reply != null && reply.equals("CONNECTED")) {
                JOptionPane.showMessageDialog(this, "Connected successfully.\nWelcome to Cake Haven, " + username + "!");
                dispose();
                openStore(connection);
            } else {
                connection.close();
                String reason = "No reply from the server.";
                if (reply != null && reply.startsWith("ERROR:")) {
                    reason = reply.substring(6);
                }
                JOptionPane.showMessageDialog(this, "Connection failed: " + reason,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Connection failed: can't reach the server.\nMake sure the server is running and the IP is correct.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // opens the store screen (StoreFrame) with the connection that was just created
    private void openStore(ServerConnection connection) {
        new StoreFrame(connection).setVisible(true);
    }

    public static void main(String[] args) {
        new ConnectFrame().setVisible(true);
    }
}
