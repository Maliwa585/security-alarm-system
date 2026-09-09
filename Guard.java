package guard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Guard extends JFrame {
    // Label to show current alarm address
    private JLabel label1;
    // Main action button (changes text depending on phase)
    private JButton btn1;
    // Backup button
    private JButton btn2;
    // Address received from server
    private String address = "No alarms";
    // Phase of guard response (0=on way, 1=arrived, 2=location safe)
    private int phase = 0;

    public Guard() {
        setTitle("Guard");
        setLayout(new FlowLayout());

        // Initialize UI components
        label1 = new JLabel("No alarms");
        btn1 = new JButton();
        btn2 = new JButton("Send backup");

        // Hide buttons until an alarm is received
        btn1.setVisible(false);
        btn2.setVisible(false);

        // Add components to window
        add(label1);
        add(btn1);
        add(btn2);

        // Button actions
        btn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleBtn1(); // handle guard phase actions
            }
        });

        btn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendAction("Send backup"); // send backup request
            }
        });

        // Poll server every 3 seconds for new alarms
        Timer timer = new Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pollServer();
            }
        });
        timer.start();

        // Window settings
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Connect to server and check for alarms
    private void pollServer() {
        try (
            Socket socket = new Socket("localhost", 4321);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("Guard"); // identify as Guard client
            address = in.readLine(); // read alarm address

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    label1.setText(address); // update label
                    if ("No alarms".equalsIgnoreCase(address)) {
                        // Reset if no alarms
                        btn1.setVisible(false);
                        btn2.setVisible(false);
                        phase = 0;
                        out.println("No action");
                    } else {
                        updateButtons(); // show correct button text
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update button text based on phase
    private void updateButtons() {
        btn1.setVisible(true);
        switch (phase) {
            case 0:
                btn1.setText("On my way");
                break;
            case 1:
                btn1.setText("Arrived");
                break;
            case 2:
                btn1.setText("Location save");
                btn2.setVisible(true); // show backup option
                break;
        }
    }

    // Handle main button clicks (progress through phases)
    private void handleBtn1() {
        switch (phase) {
            case 0:
                sendAction("Guard on his way");
                phase++;
                updateButtons();
                break;
            case 1:
                sendAction("Guard arrived");
                phase++;
                updateButtons();
                break;
            case 2:
                sendAction("House is save");
                // Reset UI after safe
                btn1.setVisible(false);
                btn2.setVisible(false);
                phase = 0;
                break;
        }
    }

    // Send guard action back to server
    private void sendAction(String action) {
        try (
            Socket socket = new Socket("localhost", 4321);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("Guard"); // identify as Guard
            out.println(action);  // send action message
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Guard();
            }
        });
    }
}
