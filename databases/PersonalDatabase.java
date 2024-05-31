package databases;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import homepages.HomePage;
import login.LoginRegistration;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
public class PersonalDatabase {

    private DefaultTableModel model;
    private String[] columnHeaders = { "Title", "Author", "Rating", "Review", "Status", "Time Spent",
            "Start Date", "End Date", "User Rating", "User Review" };
    private String username;
    public JFrame frame;
    private JTextField searchField; // Search bar
    private LocalDate currentDate = LocalDate.now();

    // Format the current date
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
    private String formattedDate = currentDate.format(formatter);

    public PersonalDatabase() {
        frame = new JFrame("Personal Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon img = new ImageIcon("src/icon4.jpg");

       // Create a table model with no data initially
       model = new DefaultTableModel(columnHeaders, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Make only specific cells editable
            return column == 6 || column == 7 || column == 8 || column == 9;
        }
    };

    
        // Create a JTable with the model
        JTable table = new JTable(model);
        table.setRowHeight(50);
        initializeSorting(table);
     
        // Set font size for table headers
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Times New Roman", Font.BOLD, 16)); // Adjust font size as needed
        header.setForeground(Color.WHITE); // Set text color
        header.setBackground(new Color(26, 24, 82)); // Set background color

        username = LoginRegistration.getLoggedInUsername();
        String path = "users/" + username + ".csv";

        // Load data from CSV file
        loadDataFromCSV(path);

        // Add mouse listener to the table
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if (row != -1 && column != -1) {
                    handleTableClick(table, row, column, path);
                }
            }
        });

        // Create search bar
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(searchField.getText(), table);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(searchField.getText(), table);
            }

            private void search(String text, JTable table) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'search'");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(searchField.getText(), table);
            }
        });

        // Create buttons for home and return book
        JButton addButton = new JButton("Add Book");
        JButton homeButton = new JButton("Home page");
        JButton returnButton = new JButton("Return Book");

        // Set fonts and colors for buttons
        Font buttonFont = new Font("Times New Roman", Font.BOLD, 16);
        Color buttonBackgroundColor = new Color(26, 24, 82);
        addButton.setFont(buttonFont);
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(buttonBackgroundColor);

        returnButton.setFont(buttonFont);
        returnButton.setForeground(Color.WHITE);
        returnButton.setBackground(buttonBackgroundColor);

        homeButton.setFont(buttonFont);
        homeButton.setForeground(Color.WHITE);
        homeButton.setBackground(buttonBackgroundColor);

        // Create the "Add Own Book" button
JButton addOwnBookButton = new JButton("Add Own Book");
addOwnBookButton.setFont(buttonFont);
addOwnBookButton.setForeground(Color.WHITE);
addOwnBookButton.setBackground(buttonBackgroundColor);


  // Add action listener for the "Add Own Book" button
    addOwnBookButton.addActionListener(e -> {
    // Open a dialog or form to collect book details from the user
    String title = JOptionPane.showInputDialog(frame, "Enter book title:");
    String author = JOptionPane.showInputDialog(frame, "Enter book author:");
    
    // Add the book to the table with default rating and review
    if (title != null && author != null && !title.isEmpty() && !author.isEmpty()) {
        Object[] rowData = {title, author, "Unavailable", "Unavailable", "Not Started", "0", "", "", "Add Rating", "Add Review"};
        model.addRow(rowData);
        
        // Save the newly added book to the user's CSV file
        saveDataToCSV(path);
    } else {
        JOptionPane.showMessageDialog(frame, "Please enter valid title and author.");
    }
            });
        // Add action listener for the homeButton
        homeButton.addActionListener(e -> {
            // Close the current frame
            frame.dispose();
            // Create a new instance of the home page
            navigateToHomePage();
        });

        returnButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to return the book?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    model.removeRow(selectedRow); // Remove the selected row from the table model
                    saveDataToCSV(path); // Save the updated data to the CSV file
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to return the book.");
            }
        });

        addButton.addActionListener(e -> {
            frame.dispose();
            new GeneralDatabase();
        });

        // Create a panel to hold the buttons and search field
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(addOwnBookButton);
        buttonPanel.add(searchField);
        buttonPanel.add(homeButton);
    
      
        // Add the button panel to the top of the frame
        frame.add(buttonPanel, BorderLayout.NORTH);

        // Add the table to the center of the frame
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // Set frame properties
        frame.setSize(1000, 600);
        frame.setIconImage(img.getImage());
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);

        // Save data to CSV file when the application is closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveDataToCSV(path);
            }
        });

        
       
    }

    // Load data from CSV file
    private void loadDataFromCSV(String filename) {
        // Clear existing data from the table model
        model.setRowCount(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Skip the first line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");
                // Check if the row has user rating and user review columns
                if (rowData.length >= 10) {
                    // Check if user rating is empty, if so, set default value
                    if (rowData[8].isEmpty()) {
                        rowData[8] = "Add rating";
                    }
                    // Check if user review is empty, if so, set default value
                    if (rowData[9].isEmpty()) {
                        rowData[9] = "Add review";
                    }
                }
                model.addRow(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save data to CSV file
    private void saveDataToCSV(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write column headers
            for (int i = 0; i < columnHeaders.length - 1; i++) {
                writer.write(columnHeaders[i] + ",");
            }
            writer.write(columnHeaders[columnHeaders.length - 1] + "\n");

            // Write data
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    writer.write(model.getValueAt(row, col) + ",");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTableAfterRating(String path) {
        // Save the data to the CSV file
        saveDataToCSV(path);

        // Reload data from CSV file
        loadDataFromCSV(path);
    }

    private void navigateToHomePage() {
        new HomePage().setVisible(true);
    }

    private void handleTableClick(JTable table, int row, int column, String path) {
        int ratingColumn = table.getColumnModel().getColumnIndex("User Rating");
        int reviewColumn = table.getColumnModel().getColumnIndex("User Review");
        int startDateColumn = table.getColumnModel().getColumnIndex("Start Date");
        int endDateColumn = table.getColumnModel().getColumnIndex("End Date");
        int statusColumn = table.getColumnModel().getColumnIndex("Status");

        if (column == ratingColumn) {
            // Display an input dialog to get the rating from the user
            String userRating = JOptionPane.showInputDialog(frame,
                    "Enter rating (given number should be between 1-5):");
            if (userRating != null) {
                try {
                    int rating = Integer.parseInt(userRating);
                    if (rating < 1 || rating > 5) {
                        throw new IllegalArgumentException(
                                "Given rating is not acceptable. Rating should be between 1 and 5.");
                    }
                    table.setValueAt(userRating, row, ratingColumn);

                    // Update the table after rating
                    updateTableAfterRating(path);
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid rating input: Please enter a number between 1 and 5.");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid rating input: " + ex.getMessage());
                }
            }
        } else if (column == reviewColumn) {
            // User chose to write a review
            String userReview = JOptionPane.showInputDialog(frame, "Enter your review:");
            if (userReview != null) {
                // Set the user review in the selected cell
                table.setValueAt(userReview, row, reviewColumn);
                // Update the data model
                model.setValueAt(userReview, row, reviewColumn);
                // Save the data to the CSV file
                saveDataToCSV(path);
            }
        } else if (column == startDateColumn || column == endDateColumn) {
            // Handle date input
            handleDateInput(table, row, column, path, startDateColumn, endDateColumn, statusColumn);
        }
    }

    private void initializeSorting(JTable table) {
       
    
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
    
        sorter.setComparator(2, new Comparator<String>() {
            @Override
            public int compare(String rating1, String rating2) {
                // Convert ratings to integers for comparison
                int ratingValue1 = Integer.parseInt(rating1);
                int ratingValue2 = Integer.parseInt(rating2);
                // Compare the ratings as integers
                return Integer.compare(ratingValue1, ratingValue2);
            }
        });
    
        sorter.setComparator(3, (Object o1, Object o2) -> {
            String review1 = o1.toString();
            String review2 = o2.toString();
            return review1.compareToIgnoreCase(review2);
        });
    
        sorter.setComparator(6, (Object o1, Object o2) -> {
            String date1 = o1.toString();
            String date2 = o2.toString();
            return LocalDate.parse(date1).compareTo(LocalDate.parse(date2));
        });
    
        sorter.setComparator(7, (Object o1, Object o2) -> {
            String date1 = o1.toString();
            String date2 = o2.toString();
            return LocalDate.parse(date1).compareTo(LocalDate.parse(date2));
        });
    
        sorter.setComparator(8, (Object o1, Object o2) -> {
            String userRating1 = o1.toString();
            String userRating2 = o2.toString();
            return Integer.compare(Integer.parseInt(userRating1), Integer.parseInt(userRating2));
        });
    }
    

    private void handleDateInput(JTable table, int row, int column, String path, int startDateColumn, int endDateColumn,
    int statusColumn) {
// Display a dialog to choose the date
String selectedDate = JOptionPane.showInputDialog(frame,
        "Enter the date (YYYY-MM-DD):", "Date", JOptionPane.PLAIN_MESSAGE);

if (selectedDate != null && !selectedDate.isEmpty()) {
    // Convert selected date to LocalDate
    LocalDate date = LocalDate.parse(selectedDate);

    // Set the value in the table
    table.setValueAt(selectedDate, row, column);

    // Update the status
    if (column == startDateColumn) {
        // If the start date is entered, set status to "Ongoing"
        table.setValueAt("Ongoing", row, statusColumn);
    } else {
        // If the end date is entered, update status based on start and end dates
        updateStatus(table, row, startDateColumn, endDateColumn, statusColumn);

        // Calculate and display time spent
        calculateAndDisplayTimeSpent(table, row, startDateColumn, endDateColumn);
    }

    saveDataToCSV(path);
}
}




private void calculateAndDisplayTimeSpent(JTable table, int row, int startDateColumn, int endDateColumn) {
    String startDateStr = (String) table.getValueAt(row, startDateColumn);
    String endDateStr = (String) table.getValueAt(row, endDateColumn);

    if (startDateStr != null && endDateStr != null && !startDateStr.isEmpty() && !endDateStr.isEmpty()) {
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        // Calculate duration between start and end date in minutes
        Duration duration = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay());
        long minutes = duration.toMinutes();

        // Set the time spent in minutes in the table
        table.setValueAt(minutes + " minutes", row, 5); // Assuming the time spent column is at index 5
    }
}



    private void updateStatus(JTable table, int row, int startDateColumn, int endDateColumn, int statusColumn) {
        String startDate = (String) table.getValueAt(row, startDateColumn);
        String endDate = (String) table.getValueAt(row, endDateColumn);

        if ((startDate != null && !startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            // If start date is provided but end date is not, status should be "Ongoing"
            table.setValueAt("Ongoing", row, statusColumn);
        } else if ((startDate != null && !startDate.isEmpty()) && (endDate != null && !endDate.isEmpty())) {
            // If both start and end dates are provided, status should be "Completed"
            table.setValueAt("Completed", row, statusColumn);
        } else if ((startDate == null || startDate.isEmpty()) && (endDate != null && !endDate.isEmpty())) {
            // If no start date but there is an end date, status should be "Completed"
            table.setValueAt("Completed", row, statusColumn);
        } else {
            // If neither start nor end date is provided, status should be "Not Started"
            table.setValueAt("Not Started", row, statusColumn);
        }
    }

  
    public void updateRating(String title, String author, String rating) {
        // Iterate over the rows in the model
        for (int i = 0; i < model.getRowCount(); i++) {
            // Get the title and author from the current row
            String rowTitle = (String) model.getValueAt(i, 0);
            String rowAuthor = (String) model.getValueAt(i, 1);
            
            // Check if the title and author match the parameters
            if (rowTitle.equals(title) && rowAuthor.equals(author)) {
                // Update the rating in the model
                model.setValueAt(rating, i, 2); // Assuming the rating column is at index 2
                // Save the changes to the CSV file
                saveDataToCSV("users/" + LoginRegistration.getLoggedInUsername() + ".csv");
                break; // Exit the loop once the rating is updated
            }
        }
    }
    

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PersonalDatabase::new);
    }
}








