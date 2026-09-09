package clienthome;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ClientHome extends JFrame {
    // Tracks if system is armed
    private boolean armed = false;
    // Step counter for code sequence
    private int step = 0;
    // Field for entering house address
    private JTextField addressField = new JTextField(15);

    public ClientHome() {
        setTitle("Home");
        setLayout(new FlowLayout());

        // Address input
        add(new JLabel("Address:"));
        add(addressField);

        // Numeric code buttons
        JButton btn1 = new JButton("1");
        JButton btn2 = new JButton("2");
        JButton btn3 = new JButton("3");
        // Action buttons
        JButton moveBtn = new JButton("Move");
        JButton alarmBtn = new JButton("Alarm");

        // Add buttons to window
        add(btn1); add(btn2); add(btn3);
        add(moveBtn); add(alarmBtn);

        // Code sequence actions
        btn1.addActionListener(e -> checkCode(1));
        btn2.addActionListener(e -> checkCode(2));
        btn3.addActionListener(e -> checkCode(3));

        // Send alerts when armed
        moveBtn.addActionListener(e -> {
            if (armed) sendAlarm("Movement detected!");
        });

        alarmBtn.addActionListener(e -> {
            if (armed) sendAlarm("Manual alarm triggered!");
        });

        // Window settings
        setSize(350, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Check numeric code sequence (1-3-2)
    private void checkCode(int num) {
        int[] code = {1, 3, 2};
        if (num == code[step]) {
            step++;
            if (step == code.length) {
                armed = !armed;
                JOptionPane.showMessageDialog(this,
                    "Alarm is now " + (armed ? "armed" : "disarmed"));
                step = 0;
            }
        } else {
            step = 0; // reset if wrong
        }
    }

    // Send alarm details to server
    private void sendAlarm(String message) {
        try (Socket socket = new Socket("localhost", 4321);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("Home");                 // client type
            out.println(addressField.getText()); // house address
            out.println(message);                // alert message
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientHome());
    }
}