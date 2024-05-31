package login;
import javax.swing.*;

import homepages.HomePage;
import homepages.HomePageAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LoginRegistration extends JFrame {
    private Map<String, String> usersDatabase;
    private File csvFile;
    private FileWriter csvWriter;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private static String loggedInUsername; // Changed from static

    public LoginRegistration() {
        setTitle("Login & Registration");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usersDatabase = new HashMap<>();
        csvFile = new File("usernames.csv");
        loadUsersFromCSV();

        try {
            csvWriter = new FileWriter(csvFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 40));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 40));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);
        
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setBackground(new Color(26, 24, 82));
        loginButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 40));
        registerButton.setBackground(new Color(26, 24, 82));
        registerButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(registerButton, gbc);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
        
                if (username.equals("admin") && password.equals("admin")) {
                    JOptionPane.showMessageDialog(null, "Login successfully as admin!");
                    dispose(); // Close the login window
                    new HomePageAdmin().setVisible(true); // Open the admin home page
                } else if (usersDatabase.containsKey(username) && usersDatabase.get(username).equals(password)) {
                    JOptionPane.showMessageDialog(null, "Login successfully!");
                    loggedInUsername = username; // Set loggedInUsername when user logs in
                    dispose(); // Close the login window
                    new HomePage().setVisible(true); // Open the home page
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            }
        });
        

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
        
                if (username.length() < 4 || password.length() < 4) {
                    JOptionPane.showMessageDialog(null, "Username and password must be at least 4 characters long.");
                } else {
                    // Extract the first name from the username
                    String[] nameParts = username.split("\\s+");
                    String firstName = nameParts[0];
        
                    if (usersDatabase.containsKey(firstName)) {
                        JOptionPane.showMessageDialog(null, "User with this first name is already registered.");
                    } else {
                        usersDatabase.put(firstName, password);
                        try {
                            csvWriter.append(firstName).append(",").append(password).append("\n");
                            csvWriter.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(null, "User registered successfully!");
                        saveUser(firstName, password);
                    }
                }
            }
        });

        add(panel); // Add this line to add the panel to the JFrame
    }

    private void loadUsersFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    usersDatabase.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  
    private boolean saveUser(String username, String password) {
        String folderPath = "users";
        String filePath = folderPath + "/" + username + ".csv"; 
        
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdir();
            }
    
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(filePath))) {
                printWriter.println("Title,Author,Rating,Review,Status,TimeSpent,StartDate,EndDate,UserRating,UserReview");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginRegistration().setVisible(true);
            }
        });
    }

    // Method to retrieve the username of the logged-in user
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }
}