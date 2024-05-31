package databases;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.JTableHeader;
import homepages.HomePageAdmin;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GeneralDatabaseAdmin {
    ImageIcon img = new ImageIcon("src/icon4.jpg");
    private JFrame jf;
    JScrollPane js;
    JTable jt;
    String[] col;
    Object[][] originalData;
    Object[][] filteredData; // Separate data for filtered results
    private int selectedRowIndex = -1; // Track selected row index
    JTextField searchField;
    int titleClicks = 0;
    int authorClicks = 0;
    int ratingClicks = 0;
    int reviewClicks = 0;
    public GeneralDatabaseAdmin() {
        jf = new JFrame("General Database");
        col = new String[]{"Title", "Author", "Rating", "Review"};
        originalData = getData();
        filteredData = originalData.clone(); // Initially, filtered data is same as original
        jt = new JTable(filteredData, col); // Use filtered data initially
        customizeRowHeadingFont(jt);
        js = new JScrollPane(jt);
        jf.add(js);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.pack();
        jf.setSize(1000, 600);
        jf.setIconImage(img.getImage());
        jf.setVisible(true);
        setRowHeight(50); // Set row height to 50 pixels

        // Creating buttons for functionalities
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(26, 24, 82));
        addButton.setForeground(Color.WHITE);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(26, 24, 82));
        deleteButton.setForeground(Color.WHITE);
        searchField = new JTextField(20);
        JButton updateAuthorButton = new JButton("Update Author");
        updateAuthorButton.setBackground(new Color(26, 24, 82));
        updateAuthorButton.setForeground(Color.WHITE);
        JButton updateTitleButton = new JButton("Update Title");
        updateTitleButton.setBackground(new Color(26, 24, 82));
        updateTitleButton.setForeground(Color.WHITE);

        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog(jf, "Enter Title:");
                String author = JOptionPane.showInputDialog(jf, "Enter Author:");
                if (title != null && author != null) {
                    Object[] newData = {title, author, "No Rating", "No Review"}; // Default Rating and Review
                    Object[][] newDataArray = new Object[originalData.length + 1][4];
                    System.arraycopy(originalData, 0, newDataArray, 0, originalData.length);
                    newDataArray[originalData.length] = newData;
                    originalData = newDataArray;
                    // Update filtered data if a search filter is applied
                    filterData(searchField.getText().toLowerCase());
                    saveChangesToCSV();
                }
            }
        });

        updateAuthorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRowIndex != -1) {
                    String newAuthor = JOptionPane.showInputDialog(jf, "Enter new Author:");
                    if (newAuthor != null) {
                        originalData[selectedRowIndex][1] = newAuthor; // Update author in the original data array
                        // Update filtered data if a search filter is applied
                        filterData(searchField.getText().toLowerCase());
                        saveChangesToCSV();
                    }
                } else {
                    JOptionPane.showMessageDialog(jf, "Please select a row to update.");
                }
            }
        });
       

        updateTitleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRowIndex != -1) {
                    String newTitle = JOptionPane.showInputDialog(jf, "Enter new Title:");
                    if (newTitle != null) {
                        originalData[selectedRowIndex][0] = newTitle; // Update title in the original data array
                        // Update filtered data if a search filter is applied
                        filterData(searchField.getText().toLowerCase());
                        saveChangesToCSV();
                    }
                } else {
                    JOptionPane.showMessageDialog(jf, "Please select a row to update.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRowIndex != -1) {
                    int option = JOptionPane.showConfirmDialog(jf, "Are you sure you want to delete this book?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        Object[][] newDataArray = new Object[originalData.length - 1][4];
                        int index = 0;
                        for (int i = 0; i < originalData.length; i++) {
                            if (i != selectedRowIndex) {
                                newDataArray[index++] = originalData[i];
                            }
                        }
                        originalData = newDataArray;
                        // Update filtered data if a search filter is applied
                        filterData(searchField.getText().toLowerCase());
                        saveChangesToCSV();
                    }
                } else {
                    JOptionPane.showMessageDialog(jf, "Please select a row to delete.");
                }
            }
        });

        // Adding mouse listener to column headers
        jt.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTableHeader header = (JTableHeader) e.getSource();
                int columnIndex = header.columnAtPoint(e.getPoint());
                if (columnIndex == 0) {
                    titleClicks++;
                    if (titleClicks % 3 == 1) {
                        // Ascending sort by title
                        Arrays.sort(originalData, Comparator.comparing(o -> o[0].toString()));
                    } else if (titleClicks % 3 == 2) {
                        // Descending sort by title
                        Arrays.sort(originalData, Comparator.comparing(o -> o[0].toString(), Comparator.reverseOrder()));
                    } else {
                        // Original order
                        originalData = getData();
                    }
                    filterData(searchField.getText().toLowerCase()); // Apply filter if any
                } else if (columnIndex == 1) {
                    authorClicks++;
                    if (authorClicks % 3 == 1) {
                        // Ascending sort by author
                        Arrays.sort(originalData, Comparator.comparing(o -> o[1].toString()));
                    } else if (authorClicks % 3 == 2) {
                        // Descending sort by author
                        Arrays.sort(originalData, Comparator.comparing(o -> o[1].toString(), Comparator.reverseOrder()));
                    } else {
                        // Original order
                        originalData = getData();
                    }
                    filterData(searchField.getText().toLowerCase()); // Apply filter if any
                }
            }
        });
        
    // Adding mouse listener to column headers
    jt.getTableHeader().addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        JTableHeader header = (JTableHeader) e.getSource();
        int columnIndex = header.columnAtPoint(e.getPoint());
        if (columnIndex == 0) {
            titleClicks++;
            if (titleClicks % 3 == 1) {
                // Ascending sort by title
                Arrays.sort(originalData, Comparator.comparing(o -> o[0].toString()));
            } else if (titleClicks % 3 == 2) {
                // Descending sort by title
                Arrays.sort(originalData, Comparator.comparing(o -> o[0].toString(), Comparator.reverseOrder()));
            } else {
                // Original order
                originalData = getData();
            }
            filterData(searchField.getText().toLowerCase()); // Apply filter if any
        } else if (columnIndex == 1) {
            authorClicks++;
            if (authorClicks % 3 == 1) {
                // Ascending sort by author
                Arrays.sort(originalData, Comparator.comparing(o -> o[1].toString()));
            } else if (authorClicks % 3 == 2) {
                // Descending sort by author
                Arrays.sort(originalData, Comparator.comparing(o -> o[1].toString(), Comparator.reverseOrder()));
            } else {
                // Original order
                originalData = getData();
            }
            filterData(searchField.getText().toLowerCase()); // Apply filter if any
        } else if (columnIndex == 2) {
            ratingClicks++;
            sortRating(); // Call sortRating() when the "Rating" column header is clicked
        } else if (columnIndex == 3) {
            reviewClicks++;
            sortReview(); // Call sortReview() when the "Review" column header is clicked
        }
    }
});

        // Adding a DocumentListener to the search field
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterData(searchField.getText().toLowerCase());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterData(searchField.getText().toLowerCase());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // This is not needed for plain text fields
            }
        });



        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = jt.columnAtPoint(e.getPoint()); // Get the column index of the clicked cell
                int row = jt.rowAtPoint(e.getPoint());
                
                // Check if the clicked cell is in the "Review" column
                if (column == 3) {
                    String username = (String) jt.getValueAt(row, column); // Get the username from the "Review" column
                    if (!"No review".equals(username)) {
                        // If there is a review, display options to view or delete it
                        int option = JOptionPane.showOptionDialog(jf, "Choose an action for this review:",
                                "Review Options",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                new String[] { "View", "Delete" }, null);
                        if (option == JOptionPane.YES_OPTION) {
                            // View the review
                            String title = (String) jt.getValueAt(row, 0); // Get the title of the book
                            String author = (String) jt.getValueAt(row, 1); // Get the author of the book
                            displayUserReview(title, author, username);
                            saveChangesToCSV();
                        } else if (option == JOptionPane.NO_OPTION) {
                            // Delete the review
                            String title = (String) jt.getValueAt(row, 0); // Get the title of the book
                            String author = (String) jt.getValueAt(row, 1); // Get the author of the book
                            deleteReview(username, title, author);
                            jt.setValueAt("No review", row, column);
                            saveChangesToCSV();
                        }
                    } else {
                        JOptionPane.showMessageDialog(jf, "No review available for this book.", "Review Options",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        // Creating panel for buttons and search field with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateAuthorButton);
        buttonPanel.add(updateTitleButton);
        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(searchField);

        // Creating the homepageButton and adding ActionListener
        JButton homepageButton = new JButton("Home Page");
        homepageButton.setPreferredSize(new Dimension(150, 30));
        homepageButton.setBackground(new Color(26, 24, 82));
        homepageButton.setForeground(Color.WHITE);
        homepageButton.addActionListener(e -> {
            jf.dispose();
            navigateToHomePage();
        });
        buttonPanel.add(homepageButton);

        // Adding panel to JFrame
        jf.add(buttonPanel, BorderLayout.NORTH);
        
        // Add listener for row selection
        jt.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRowIndex = jt.getSelectedRow();
            }
        });
        
    }
    
    private void sortRating() {
        if (ratingClicks % 3 == 1) {
            // Ascending sort by rating
            Arrays.sort(originalData, Comparator.comparing(o -> parseRating(o[2].toString())));
        } else if (ratingClicks % 3 == 2) {
            // Descending sort by rating
            Arrays.sort(originalData, Comparator.comparing(o -> parseRating(o[2].toString()), Comparator.reverseOrder()));
        } else {
            // Original order
            originalData = getData();
        }
        filterData(searchField.getText().toLowerCase()); // Apply filter if any
    }

    private void sortReview() {
        if (reviewClicks % 3 == 1) {
            // Ascending sort by review
            Arrays.sort(originalData, Comparator.comparing(o -> o[3].toString()));
        } else if (reviewClicks % 3 == 2) {
            // Descending sort by review
            Arrays.sort(originalData, Comparator.comparing(o -> o[3].toString(), Comparator.reverseOrder()));
        } else {
            // Original order
            originalData = getData();
        }
        filterData(searchField.getText().toLowerCase()); // Apply filter if any
    }

    // Helper method to parse rating to Integer for sorting
    private Integer parseRating(String rating) {
        if (rating.equals("No Rating")) {
            return 0; // Consider "No Rating" as the lowest rating
        }
        return Integer.parseInt(rating.split(" ")[0]);
    }
    

    

    private void navigateToHomePage() {
        new HomePageAdmin().setVisible(true);
    }




    public static Object[][] getData() {
        ArrayList<Object[]> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("files/personal_updated.csv"))) {
            String line;
            boolean isFirstLine = true; // Variable to track if it's the first line
    
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line
                }
    
                String[] parts = line.split(",");
                Object[] newData = new Object[parts.length + 1]; // Increase the size by 1 for the review column
                for (int i = 0; i < parts.length; i++) {
                    newData[i] = parts[i].trim(); // Trim to remove leading/trailing whitespace
                }
                // Read the fourth column from the reviews.csv file
                String review = readReviewFromReviewsCSV(newData[0].toString().trim());
                newData[parts.length] = review.trim(); // Add the review to the newData array
                dataList.add(newData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Convert ArrayList to two-dimensional array
        Object[][] data = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
    
        return data;
    }
    




    private static String readReviewFromReviewsCSV(String title) {
        try (BufferedReader br = new BufferedReader(new FileReader("files/reviews.csv"))) {
            String line;
            boolean isFirstLine = true; // Variable to track if it's the first line
            StringBuilder review = new StringBuilder(); // StringBuilder to store the review
    
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line
                }
    
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].trim().equalsIgnoreCase(title)) {
                    // Append the review to the StringBuilder
                    if (review.length() > 0) {
                        review.append(", "); // Add comma and space if not the first review
                    }
                    review.append(parts[3].trim()); // Append the review part
                }
            }
            // Check if any reviews were found
            if (review.length() > 0) {
                return review.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No review"; // Return "No review" if no matching title is found or no reviews were found
    }
    
   

    private void displayUserReview(String title, String author, String username) {
        try (BufferedReader br = new BufferedReader(new FileReader("files/reviews.csv"))) {
            String line;
            boolean isFirstLine = true;
            StringBuilder userReview = new StringBuilder();
    
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
    
                String[] parts = line.split(",");
                String reviewTitle = parts[0].trim();
                String reviewAuthor = parts[1].trim();
                String reviewer = parts[2].trim();
                String review = parts[3].trim();
    
                // Check if the review is for the same book, author, and user
                if (reviewTitle.equalsIgnoreCase(title) && reviewAuthor.equalsIgnoreCase(author) && reviewer.equalsIgnoreCase(username)) {
                    userReview.append(review).append("\n");
                }
            }
    
            if (userReview.length() > 0) {
                // Create a custom dialog window with review text and delete button
                JFrame frame = new JFrame("User Review");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JTextArea textArea = new JTextArea(userReview.toString());
                textArea.setEditable(false);
    
                JButton deleteButton = new JButton("Delete Review");
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteReview(username, title, author);
                        frame.dispose();
                    }
                });
    
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
                // panel.add(deleteButton, BorderLayout.SOUTH);
                frame.add(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(jf, "No review found for this book by the selected user.", "User Review", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(jf, "Error occurred while reading reviews.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    


private void deleteReview(String reviewer, String title, String author) {
        try {
            // Construct reviewer's file path
            String reviewerFilePath = "users/" + reviewer + ".csv";
            System.out.println("Reviewer File Path: " + reviewerFilePath); // Print file path
            
            // Read the reviewer's file
            File file = new File(reviewerFilePath);
            List<String> lines = Files.readAllLines(file.toPath());
            
            // Write the updated file, replacing the content of the 10th column with "Deleted by admin"
            FileWriter writer = new FileWriter(file);
            for (String line : lines) {
                String[] parts = line.split(",");
                String reviewTitle = parts[0].trim();
                String reviewAuthor = parts[1].trim();
                if (reviewTitle.equalsIgnoreCase(title) && reviewAuthor.equalsIgnoreCase(author)) {
                    // Replace the content of the 10th column with "Deleted by admin"
                    parts[9] = "Deleted by admin";
                    // Concatenate the parts back into a line
                    String updatedLine = String.join(",", parts);
                    writer.write(updatedLine + System.lineSeparator());
                } else {
                    writer.write(line + System.lineSeparator());
                }
            }
            writer.close();
            JOptionPane.showMessageDialog(jf, "Review deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(jf, "Error occurred while deleting the review.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
      




    private void setRowHeight(int height) {
        jt.setRowHeight(height); // Set the row height for the JTable
    }

    private void customizeRowHeadingFont(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16)); // Changing heading font
    }

    // Method to filter data based on the search query
    private void filterData(String query) {
        ArrayList<Object[]> filteredDataList = new ArrayList<>();
        for (Object[] row : originalData) { // Use original data array
            String title = row[0].toString().toLowerCase();
            String author = row[1].toString().toLowerCase();
            if (title.contains(query) || author.contains(query)) {
                filteredDataList.add(row);
            }
        }
        if (filteredDataList.isEmpty()) {
            JOptionPane.showMessageDialog(jf, "No matching title or author found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Convert the filtered data to a 2D array
            filteredData = new Object[filteredDataList.size()][4];
            for (int i = 0; i < filteredDataList.size(); i++) {
                filteredData[i] = filteredDataList.get(i);
            }
            // Update the table with the filtered data
            jt.setModel(new javax.swing.table.DefaultTableModel(filteredData, col));
        }
    }

    // Method to write data to the CSV file after adding a book
    private void writeDataToCSV(String title, String author) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("files/personal_updated.csv", true));
            writer.write(title + "," + author + ",\n"); // Append rating and review as empty
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to save changes to the CSV file
    private void saveChangesToCSV() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("files/personal_updated.csv"));
            for (Object[] row : originalData) {
                writer.write(row[0] + "," + row[1] + "," + row[2] + "," + row[3] + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void showReviewWindow(String reviewUser) {
        JFrame reviewFrame = new JFrame("Review for " + reviewUser);
        reviewFrame.setSize(400, 300);
        reviewFrame.setLocationRelativeTo(null); // Center the frame on the screen

        JTextArea reviewTextArea = new JTextArea();
        reviewTextArea.setEditable(false); // Make the text area read-only

        // Read the review from the CSV file based on the review user
        String review = readReviewFromCSV(reviewUser);

        if (review != null && !review.isEmpty()) {
            reviewTextArea.setText(review);
        } else {
            reviewTextArea.setText("No review found for " + reviewUser);
        }

        JScrollPane scrollPane = new JScrollPane(reviewTextArea);
        reviewFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        reviewFrame.setVisible(true);
    }
    

    private String readReviewFromCSV(String reviewUser) {
        String filePath = "reviews.csv"; // Path to your reviews CSV file
        String line;
        String review = null;
    
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equalsIgnoreCase(reviewUser.trim())) {
                    // Check if the first column matches the review user
                    review = parts[1].trim(); // Assuming the review is in the second column
    
                    // If the review contains slashes, split it and return the first reviewer's review
                    if (review.contains("/")) {
                        review = review.split("/")[0].trim();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return review;
    }
    
    public static void main(String[] args) {
        new GeneralDatabaseAdmin();
    }

    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("Unimplemented method 'setVisible'");
    }
}
