package homepages;
import javax.swing.*;

import databases.GeneralDatabase;
import databases.PersonalDatabase;
import login.LoginRegistration;

import java.awt.*;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("Home Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to the Home Page!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton generalDatabaseButton = new JButton("General Database");
        generalDatabaseButton.setPreferredSize(new Dimension(150, 30)); // Set preferred size
        generalDatabaseButton.setBackground(new Color(26,24,82));
        generalDatabaseButton.setForeground(Color.WHITE);
        generalDatabaseButton.addActionListener(e -> {
            dispose(); // Close the current window
           
            new GeneralDatabase().setVisible(true);
        });

        JButton personalDatabaseButton = new JButton("Personal Database");
        personalDatabaseButton.setPreferredSize(new Dimension(150, 30)); // Set preferred size
        personalDatabaseButton.setBackground(new Color(26,24,82));
        personalDatabaseButton.setForeground(Color.WHITE);


        personalDatabaseButton.addActionListener(e -> {
            dispose(); // Close the current window
            new PersonalDatabase();
        });
         
        

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(150, 30)); // Set preferred size
        logoutButton.setBackground(new Color(26,24,82));
        logoutButton.setForeground(Color.WHITE);;
        logoutButton.addActionListener(e -> {
            dispose(); // Close the current window
            
            new LoginRegistration().setVisible(true); // Open the login window
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Use FlowLayout
        buttonPanel.add(generalDatabaseButton); // Add general database button
        buttonPanel.add(personalDatabaseButton); // Add personal database button
        buttonPanel.add(logoutButton); // Add logout button

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(welcomeLabel, BorderLayout.NORTH); // Add welcome label to the top
        panel.add(buttonPanel, BorderLayout.CENTER); // Add button panel to the center

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
    }
}