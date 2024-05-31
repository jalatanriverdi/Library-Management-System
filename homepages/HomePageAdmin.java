package homepages;
import javax.swing.*;

import databases.GeneralDatabaseAdmin;
import login.AdminUserDeletion;
import login.LoginRegistration;

import java.awt.*;

public class HomePageAdmin extends JFrame {
    public HomePageAdmin() {
        setTitle("Home Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to the Home Page!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton generalDatabaseButton = new JButton("General Database");
        generalDatabaseButton.setPreferredSize(new Dimension(150, 30)); 
        generalDatabaseButton.setBackground(new Color(26, 24, 82));
        generalDatabaseButton.setForeground(Color.WHITE);
        generalDatabaseButton.addActionListener(e -> {
            dispose(); 
            new GeneralDatabaseAdmin().setVisible(true);
        });

        JButton userManagementButton = new JButton("User Management");
        userManagementButton.setPreferredSize(new Dimension(150, 30)); 
        userManagementButton.setBackground(new Color(26, 24, 82));
        userManagementButton.setForeground(Color.WHITE);

        userManagementButton.addActionListener(e -> {
            dispose(); 
            new AdminUserDeletion().setVisible(true); 
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(150, 30)); 
        logoutButton.setBackground(new Color(26, 24, 82));
        logoutButton.setForeground(Color.WHITE);

        logoutButton.addActionListener(e -> {
            dispose(); 
            new LoginRegistration().setVisible(true); 
        });
      
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        buttonPanel.add(generalDatabaseButton); 
        buttonPanel.add(userManagementButton); 
        buttonPanel.add(logoutButton); 

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(welcomeLabel, BorderLayout.NORTH); 
        panel.add(buttonPanel, BorderLayout.CENTER); 

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePageAdmin().setVisible(true);
        });
    }
}
