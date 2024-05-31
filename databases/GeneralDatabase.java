package databases;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.JTableHeader;
import java.util.List;
import homepages.HomePage;
import login.LoginRegistration;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.AlreadyBoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Arrays;
import java.io.File;
import java.io.BufferedWriter;
import java.util.Vector;

public class GeneralDatabase extends PersonalDatabase {
    ImageIcon img = new ImageIcon("src/icon4.jpg");
    private JFrame jf;
    JScrollPane js;
    JTable jt;
    String[] col;
    Object[][] data;
    private Object[][] originalData;
    JTextField searchField;
    int titleClicks = 0;
    int authorClicks = 0;
   
    public GeneralDatabase() {
        frame.dispose();
        jf = new JFrame("General Database");
        col = new String[] { "Title", "Author", "Rating", "Review" };
        originalData = getData();
        data = originalData.clone(); // Clone the original data to start with
  ratingCalculator();
        jt = new JTable(data, col) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        customizeRowHeadingFont(jt);
        js = new JScrollPane(jt);
        jf.add(js);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.pack();
        jf.setSize(1000, 600);
        jf.setIconImage(img.getImage());
        jf.setVisible(true);
        setRowHeight(50); // Set row height to 50 pixels

        // Adding the search field
        searchField = new JTextField(20);
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
                filterData(searchField.getText().toLowerCase());
            }
        });

        // Creating the search label
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.BLACK);

        JButton selectButton = new JButton("Select");
        selectButton.setPreferredSize(new Dimension(150, 30)); // Set preferred size
        selectButton.setBackground(new Color(26, 24, 82));
        selectButton.setForeground(Color.WHITE); // Set preferred size


      
    

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Call the showAddPersonal method when the button is clicked
                // You need to pass the selected row and the username to this method
                int selectedRow = jt.getSelectedRow();
                if (selectedRow != -1) {

                    String username = LoginRegistration.getLoggedInUsername();
                    showAddPersonal(selectedRow, username, jf);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a row first.", "No Row Selected",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

            private void showAddPersonal(int selectedRow, String username, JFrame jf) {
                DefaultTableModel model = (DefaultTableModel) jt.getModel();
                Vector rowData = (Vector) model.getDataVector().get(selectedRow);
                
                String title = (String) jt.getValueAt(selectedRow, 0);
                String author = (String) jt.getValueAt(selectedRow, 1);
                String rating = (String) jt.getValueAt(selectedRow, 2);
                String review = (String) jt.getValueAt(selectedRow, 3);
            
                String confirmationMessage = String.format(
                        "Are you sure you want to add the following data to your personal database?\n\nTitle: %s\nAuthor: %s\nReview: %s\nRating: %s",
                        title, author, rating,review);
            
                int confirmationResult = JOptionPane.showConfirmDialog(jf, confirmationMessage, "Confirm Addition",
                        JOptionPane.YES_NO_OPTION);
            
                if (confirmationResult == JOptionPane.YES_OPTION) {
                    String personalDatabaseFilename = username + ".csv";
            
                    String personalDatabaseFolderPath = "users";
            
                    File folder = new File(personalDatabaseFolderPath);
            
                    File[] files = folder.listFiles();
            
                    File[] filteredFiles = Arrays.stream(files)
                            .filter(file -> file.getName().equals(personalDatabaseFilename))
                            .toArray(File[]::new);
            
                    if (filteredFiles.length > 0) {
                        File personalDatabaseFile = filteredFiles[0];
            
                        try {
                            // Check if the book already exists in the personal database
                            boolean bookExists = checkIfBookExistsInPersonalDatabase(personalDatabaseFile, title, author);
                            if (bookExists) {
                                JOptionPane.showMessageDialog(jf, "This book already exists in your personal database.", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                // Add the book to the personal database
                                String dataLine = String.format("%s,%s,%s,%s,Not Started,0,,,Add Rating,Add Review\n", title, author, rating, review);
            
                                FileWriter fw = new FileWriter(personalDatabaseFile, true);
                                BufferedWriter bw = new BufferedWriter(fw);
                                bw.write(dataLine);
                                bw.close();
                                fw.close();
            
                                JOptionPane.showMessageDialog(jf, "Book added to personal database successfully!",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(jf, "Error occurred while adding data to personal database.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(jf, "Personal database file not found.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }




    // Define the checkIfBookExistsInPersonalDatabase method outside the ActionListener
    private boolean checkIfBookExistsInPersonalDatabase(File personalDatabaseFile, String title, String author) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(personalDatabaseFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String dbTitle = parts[0].trim();
                String dbAuthor = parts[1].trim();
                if (dbTitle.equalsIgnoreCase(title) && dbAuthor.equalsIgnoreCase(author)) {
                    return true; // Book already exists in the personal database
                }
            }
        }
        return false; // Book doesn't exist in the personal database
    }
    
});



        // Creating the homepageButton and adding ActionListener
        JButton homepageButton = new JButton("Home Page");
        homepageButton.setPreferredSize(new Dimension(150, 30)); // Set preferred size
        homepageButton.setBackground(new Color(26, 24, 82));
        homepageButton.setForeground(Color.WHITE); // Set preferred size
        homepageButton.addActionListener(e -> {
            jf.dispose(); // Close the current window
            navigateToHomePage(); // Call method to navigate to homepage
        });

        // Adding mouse listener to column headers for sorting
        jt.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTableHeader header = (JTableHeader) e.getSource();
                int columnIndex = header.columnAtPoint(e.getPoint());
                if (columnIndex == 0) {
                    titleClicks++;
                    sortDataByTitle();
                } else if (columnIndex == 1) {
                    authorClicks++;
                    sortDataByAuthor();
                }
            }
        });
        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = jt.getColumnModel().getColumnIndex("Review");
                int row = jt.rowAtPoint(e.getPoint());
                if (column == jt.columnAtPoint(e.getPoint())) {
                    String title = (String) jt.getValueAt(row, 0); // Get the title of the book
                    String author = (String) jt.getValueAt(row, 1); // Get the author of the book
                    String username = (String) jt.getValueAt(row, 3); // Get the username from the "Review" column
                    if (!username.equals("No review")) {
                        // If there is a review, display it
                        displayUserReview(title, author, username);
                    }
                }
            }
        });

        // Creating panel for buttons with right alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(selectButton); // add select button
        buttonPanel.add(searchLabel); // Add the search label to the panel
        buttonPanel.add(searchField); // Add the search field to the panel
        buttonPanel.add(homepageButton); // Add the homepage button to the panel

        // Add the buttonPanel to the JFrame at the top (NORTH position)
        jf.add(buttonPanel, BorderLayout.NORTH);
    }

    // Method to navigate to the homepage
    private void navigateToHomePage() {
        new HomePage().setVisible(true);
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
                // If review found, display it in a dialog
                JOptionPane.showMessageDialog(jf, userReview.toString(), "User Review", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(jf, "No review found for this book by the selected user.", "User Review", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(jf, "Error occurred while reading reviews.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




   private void ratingCalculator() {
        RatingCalculator ratingCalculator = new RatingCalculator();
        Map<String, Object[]> ratingInfo = RatingCalculator.calculateAverageRatingsAndCountsForAllBooks();
        
        for (int i = 0; i < data.length; i++) {
            String title = (String) data[i][0];
            if (ratingInfo.containsKey(title)) {
                Object[] info = ratingInfo.get(title);
                String averageRating = String.valueOf(info[0]);
                int countOfRatings = (int) info[1];
                String ratingInfoString = averageRating + " (" + countOfRatings + ")";
                data[i][2] = ratingInfoString;
                String author = (String) data[i][1];
                String rating = averageRating;
                updateRating(title, author, ratingInfoString);
            }
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
    try {
        for (Object[] row : originalData) {
            String title = row[0].toString().toLowerCase();
            String author = row[1].toString().toLowerCase();
            if (title.contains(query) || author.contains(query)) {
                filteredDataList.add(row);
            }
        }
        if (filteredDataList.isEmpty()) {
            JOptionPane.showMessageDialog(jf, "No matching title or author found.", "Search Result",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Convert the filtered data to a 2D array
            Object[][] filteredData = new Object[filteredDataList.size()][4];
            for (int i = 0; i < filteredDataList.size(); i++) {
                filteredData[i] = filteredDataList.get(i);
            }
            // Update the table with the filtered data
            jt.setModel(new javax.swing.table.DefaultTableModel(filteredData, col));
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(jf, "An error occurred while filtering data: " + e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}





    // Method to remove quotation marks from a string
    private String removeQuotationMarks(String s) {
        return s.replace("\"", ""); // Remove all occurrences of double quotation marks
    }

    // Method to sort data by title
    private void sortDataByTitle() {
        if (titleClicks % 3 == 1) {
            // Ascending order
            sortData(0, true);
        } else if (titleClicks % 3 == 2) {
            // Descending order
            sortData(0, false);
        } else {
            // Original order
            jt.setModel(new javax.swing.table.DefaultTableModel(originalData, col));
        }
    }

    // Method to sort data by author
    private void sortDataByAuthor() {
        if (authorClicks % 3 == 1) {
            // Ascending order
            sortData(1, true);
        } else if (authorClicks % 3 == 2) {
            // Descending order
            sortData(1, false);
        } else {
            // Original order
            jt.setModel(new javax.swing.table.DefaultTableModel(originalData, col));
        }
    }

    // Method to sort data by specified column index
    private void sortData(int columnIndex, boolean ascending) {
        Object[][] sortedData = data.clone();
        // Sort the array based on the specified column index and order
        if (ascending) {
            // Ascending order
            java.util.Arrays.sort(sortedData, Comparator.comparing(o -> o[columnIndex].toString()));
        } else {
            // Descending order
            java.util.Arrays.sort(sortedData,
                    Comparator.comparing(o -> o[columnIndex].toString(), Comparator.reverseOrder()));
        }
        // Update the table with the sorted data
        jt.setModel(new javax.swing.table.DefaultTableModel(sortedData, col));
    }
   

    public static void main(String[] args) {
        RatingCalculator.calculateAverageRatingsAndCountsForAllBooks();
        SwingUtilities.invokeLater(() -> {
            new GeneralDatabase();
        });
    }

    public void setVisible(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setVisible'");
    }

}