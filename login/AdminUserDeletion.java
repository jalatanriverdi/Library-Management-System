package login;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import homepages.HomePageAdmin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Comparator;

public class AdminUserDeletion extends JFrame {

    private static final String USER_FILE = "usernames.csv";
    private static final String DATABASE_DIRECTORY = "users/";
    private static final String ADMIN_USERNAME = "admin"; // Admin username

    private DefaultTableModel tableModel;
    private JTable table;
    private JButton deleteButton;
    private JButton homepageButton;
    private JTextField searchField; // Search bar

    private TableRowSorter<DefaultTableModel> sorter;
    private boolean ascending = true;
    private int sortCount = 0;

    public AdminUserDeletion() {
        setTitle("User Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create and configure the table
        tableModel = new DefaultTableModel(new String[]{"Username"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(50); // Set the row height
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Create the delete button and add action listener
        deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedUser();
            }
        });
        deleteButton.setBackground(new Color(26, 24, 82));
        deleteButton.setForeground(Color.WHITE);

        // Create the homepage button and add action listener
        homepageButton = new JButton("Homepage");
        homepageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new HomePageAdmin().setVisible(true);
            }
        });
        homepageButton.setBackground(new Color(26, 24, 82));
        homepageButton.setForeground(Color.WHITE);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Add the search bar
        searchField = new JTextField(20);
        searchField.setToolTipText("Search");
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchField);
        buttonPanel.add(homepageButton);

        // Add the button panel to the top of the window
        add(buttonPanel, BorderLayout.NORTH);

        // Load user data
        loadUsers();

        // Add key listener to the search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchUsers(searchField.getText());
            }
        });

        // Set up sorting functionality
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sortCount++;
                if (sortCount == 3) {
                    sorter.setComparator(0, null);
                    sortCount = 0;
                } else {
                    sorter.setComparator(0, new Comparator<String>() {
                        public int compare(String s1, String s2) {
                            return (ascending ? 1 : -1) * s1.compareToIgnoreCase(s2);
                        }
                    });
                    ascending = !ascending;
                }
            }
        });
    }

    private void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && !parts[0].equals(ADMIN_USERNAME)) {
                    tableModel.addRow(new Object[]{parts[0]});
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = (String) table.getValueAt(selectedRow, 0);
        if (username.equals(ADMIN_USERNAME)) {
            JOptionPane.showMessageDialog(this, "Admin user cannot be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user '" + username + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deleteUserData(username); // Delete user's data
            tableModel.removeRow(selectedRow);
            removeFromUsernamesFile(username); // Remove user from usernames.csv
        }
    }

    private void deleteUserData(String username) {
        String databasePath = DATABASE_DIRECTORY + username + ".csv";
        File databaseFile = new File(databasePath);
        if (databaseFile.exists()) {
            if (databaseFile.delete()) {
                System.out.println("User data deleted: " + username);
            } else {
                System.err.println("Failed to delete user data: " + username);
            }
        } else {
            System.out.println("User data not found: " + username);
        }
    }

    private void removeFromUsernamesFile(String username) {
        StringBuilder updatedContent = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && !parts[0].equals(username)) {
                    updatedContent.append(line).append(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating usernames: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE))) {
            bw.write(updatedContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating usernames: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchUsers(String searchText) {
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching username found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminUserDeletion().setVisible(true);
            }
        });
    }
}
