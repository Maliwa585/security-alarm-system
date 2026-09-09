package controlroomserver;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class ControlRoomServer {
    // Current alarm address (default: no alarms)
    static String address = "No alarms";
    // Flag to decide if a guard should be dispatched
    static boolean dispatch = false;
    // User decision from dialog (Yes/No/Cancel)
    static String userIn = "Cancel";

    public static void main(String[] args) throws IOException {
        // Start server on port 4321
        ServerSocket serverSocket = new ServerSocket(4321);
        System.out.println("Control Room Server started...");

        // Accept clients continuously
        while (true) {
            Socket socket = serverSocket.accept();
            // Handle each client in a new thread
            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // First line tells us client type (Home or Guard)
            String clientType = in.readLine();

            if ("Home".equalsIgnoreCase(clientType)) {
                // Read address from ClientHome
                address = in.readLine();
                System.out.println("address = " + address);

                // If alarm exists, ask operator if guard must be dispatched
                if (!"No alarms".equalsIgnoreCase(address)) {
                    Object[] options = {"Yes", "No", "Cancel"};
                    int result = JOptionPane.showOptionDialog(null,
                        "Must a guard be dispatched to the client?",
                        "Dispatch Decision",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]);

                    userIn = options[result].toString();
                    dispatch = "Yes".equals(userIn);
                }

            } else if ("Guard".equalsIgnoreCase(clientType)) {
                // Send address if dispatch is true, otherwise "No alarms"
                if (dispatch) {
                    out.println(address);
                } else {
                    out.println("No alarms");
                }

                // Read guard action (On my way, Arrived, Safe, Backup)
                String guardAction = in.readLine();
                if (!"No action".equalsIgnoreCase(guardAction)) {
                    System.out.println(guardAction);

                    // Reset system when guard reports house is safe
                    if ("House is save".equalsIgnoreCase(guardAction)) {
                        dispatch = false;
                        address = "No alarms";
                        userIn = "Cancel";
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
