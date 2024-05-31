package databases;
import java.io.*;
import java.util.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class RatingCalculator {

    private static final String PERSONS_FOLDER_PATH = "users"; // Path to the folder containing CSV files

    public static Map<String, Double> calculateAverageRatingsForAllBooks() {
        Map<String, Integer> totalRatingsMap = new HashMap<>();
        Map<String, Integer> numberOfRatingsMap = new HashMap<>();

        File personsFolder = new File(PERSONS_FOLDER_PATH);
        File[] files = personsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
                    readRatingsFromFile(file, totalRatingsMap, numberOfRatingsMap);
                }
            }
        }

        Map<String, Double> averageRatingsMap = new HashMap<>();
        for (String book : totalRatingsMap.keySet()) {
            int totalRatings = totalRatingsMap.get(book);
             int  numberOfRatings = numberOfRatingsMap.get(book);
            double averageRating = numberOfRatings > 0 ? (double) totalRatings / numberOfRatings : 0;
            averageRatingsMap.put(book, averageRating);
        }
        return averageRatingsMap;
    }



    private static void readRatingsFromFile(File file, Map<String, Integer> totalRatingsMap, Map<String, Integer> numberOfRatingsMap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip the header line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 10) { // Assuming the title is in the first column and the rating is in the ninth column
                    String bookTitle = fields[0].trim();
                    String ratingStr = fields[8].trim(); // Assuming the rating is a string
                    if (!ratingStr.equalsIgnoreCase("Add rating")) { // Skip if rating is "NA"
                        try {
                            int rating = Integer.parseInt(ratingStr); // Assuming the rating is an integer
                            totalRatingsMap.put(bookTitle, totalRatingsMap.getOrDefault(bookTitle, 0) + rating);
                            numberOfRatingsMap.put(bookTitle, numberOfRatingsMap.getOrDefault(bookTitle, 0) + 1);
                        } catch (NumberFormatException e) {
                            // Skip if the rating is not a valid integer
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Map<String, Object[]> calculateAverageRatingsAndCountsForAllBooks() {
        Map<String, Integer> totalRatingsMap = new HashMap<>();
        Map<String, Integer> numberOfRatingsMap = new HashMap<>();

        File personsFolder = new File(PERSONS_FOLDER_PATH);
        File[] files = personsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) { //getting user's personal databases
                    readRatingsFromFile(file, totalRatingsMap, numberOfRatingsMap);
                }
            }
        }

        Map<String, Object[]> result = new HashMap<>();
        for (String book : totalRatingsMap.keySet()) {
            int totalRatings = totalRatingsMap.get(book);
            int numberOfRatings = numberOfRatingsMap.getOrDefault(book, 0);
            double averageRating = numberOfRatings > 0 ? (double) totalRatings / numberOfRatings : 0;
            Object[] info = { averageRating, numberOfRatings };
            result.put(book, info);
        }

        return result;
    }

    public static Map<String, Map<String, String>> findReviewersAndReviews() {
        Map<String, Map<String, String>> reviewMap = new HashMap<>();
    
        File personsFolder = new File(PERSONS_FOLDER_PATH);
        File[] files = personsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
                    readReviewsAndUsernamesFromFile(file, reviewMap);
                }
            }
        }
        return reviewMap;
    }
    
    private static void readReviewsAndUsernamesFromFile(File file, Map<String, Map<String, String>> reviewMap) {
        String fileName = file.getName(); // Get the filename
        String userName = fileName.substring(0, fileName.lastIndexOf('.')); // Extract username from filename
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip the header line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 10) {
                    String bookTitle = fields[0].trim(); // Assuming the book title is in the first column
                    String review = fields[9].trim(); // Assuming the review is in the 10th column
                    if (!review.isEmpty() && !review.equalsIgnoreCase("Add Review")) {
                        // Append the review to the existing reviews for the book
                        Map<String, String> userReviewMap = reviewMap.getOrDefault(bookTitle, new HashMap<>());
                        userReviewMap.put(userName, review);
                        reviewMap.put(bookTitle, userReviewMap);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    



    private static void readReviewsFromFile(File file, Map<String, String> totalRatingsMap) {
        String fileName = file.getName(); // Get the filename
        String userName = fileName.substring(0, fileName.lastIndexOf('.')); // Extract username from filename
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip the header line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 10) {
                    String bookTitle = fields[0].trim(); // Assuming the book title is in the first column
                    String review = fields[9].trim(); // Assuming the review is in the 10th column
                    if (!review.isEmpty() && !review.equalsIgnoreCase("Add Review")) {
                        // Append the new reviewer to the existing reviewers for the book
                        String existingReviewers = totalRatingsMap.getOrDefault(bookTitle, "");
                        if (!existingReviewers.isEmpty()) {
                            existingReviewers += ", ";
                        }
                        existingReviewers += userName;
                        totalRatingsMap.put(bookTitle, existingReviewers);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeRatingsToTheFile(Map<String, Double> averageRatingsMap, Map<String, Integer> numberOfRatingsMap, Map<String, String> username, File originalFile, File newFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 4) { // Ensure there are at least four columns
                    String bookTitle = fields[0].trim();
                    if (averageRatingsMap.containsKey(bookTitle)) {
                        double averageRating = averageRatingsMap.get(bookTitle);
                        int numberOfRatings = numberOfRatingsMap.getOrDefault(bookTitle, 0);
                        String ratingInfo = String.format("%.1f(%d)", averageRating, numberOfRatings);
                        // Update the third column with the average rating and count of ratings
                        fields[2] = ratingInfo;
                        // Get the usernames for the book
                        String usernames = username.getOrDefault(bookTitle, "");
                        // Convert usernames to HTML links
                        String[] usernameArray = usernames.split(", ");
                        StringBuilder linkBuilder = new StringBuilder();
                        for (String user : usernameArray) {
                            linkBuilder.append(user);
                        }
                        String usernameLinks = linkBuilder.toString();
                        // Update the fourth column with the HTML links for usernames
                        fields[3] = usernameLinks;
                    }
                }
                // Write the updated line back to the file
                writer.write(String.join(",", fields) + "\n");
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public static Map<String, Integer> countOfPeopleTo() {
        Map<String, Integer> totalRatingsMap = new HashMap<>();
        Map<String, Integer> numberOfRatingsMap = new HashMap<>();

        File personsFolder = new File(PERSONS_FOLDER_PATH);
        File[] files = personsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
                    readRatingsFromFile(file, totalRatingsMap, numberOfRatingsMap);
                }
            }
        }

        Map<String, Integer> countOfPeople = new HashMap<>();
        for (String book : totalRatingsMap.keySet()) {
            int numberOfRatings = numberOfRatingsMap.getOrDefault(book, 0);
            countOfPeople.put(book, numberOfRatings);
        }

        return countOfPeople;
    }
    public static Map<String, String> findReviewers() {
        Map<String, String> totalRatingsMap = new HashMap<>();
    
            File personsFolder = new File(PERSONS_FOLDER_PATH);
            File[] files = personsFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
                        readReviewsFromFile(file, totalRatingsMap);
                    }
                }
            }
           return totalRatingsMap;    
    }
    public static String readReviewsBasedOnusername(String username) {
        File personsFolder = new File(PERSONS_FOLDER_PATH);
        File[] files = personsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
                    String fileName = file.getName();
                    String fileUsername = fileName.substring(0, fileName.lastIndexOf('.'));
                    if (fileUsername.equals(username)) {
                        System.out.println("Reviews for " + username + ":");
                        return readReviewsFromFile(file);
                    }
                }
            }
        }
        return "No reviews found for " + username;
    }
    
    private static String readReviewsFromFile(File file) {
        StringBuilder reviewsBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip the header line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 10) {
                    String review = fields[9].trim(); // Assuming the review is in the 10th column
                    if (!review.isEmpty() && !review.equalsIgnoreCase("Add Review")) {
                        reviewsBuilder.append(review).append("\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviewsBuilder.toString();
    }
    
    
   
    public static void main(String[] args) {
        // Calculate average ratings for all books
        Map<String, Double> averageRatingsMap = calculateAverageRatingsForAllBooks();
        
        // Find reviewers and their reviews
        Map<String, Map<String, String>> reviewersAndReviews = findReviewersAndReviews();
        
        // Print the map of reviewers and their reviews
        System.out.println("Reviewers and Reviews:");
        System.out.println(reviewersAndReviews);
        
        // Open a new file to save title, author, username, and review
        try (PrintWriter writer = new PrintWriter(new FileWriter("files/reviews.csv"))) {
            // Write header
            writer.println("Title, Author, Username, Review");
            
            // Iterate over each book
            for (String bookTitle : reviewersAndReviews.keySet()) {
                // Get author of the book
                String author = null;
                for (File file : new File(PERSONS_FOLDER_PATH).listFiles()) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] fields = line.split(",");
                                if (fields.length >= 2 && fields[0].trim().equalsIgnoreCase(bookTitle)) {
                                    author = fields[1].trim(); // Assuming the author is in the second column
                                    break;
                                }
                            }
                        }
                    }
                    if (author != null) {
                        break; // Break out of the loop if author is found
                    }
                }
                
                // Get reviewers and their reviews for the current book
                Map<String, String> reviewers = reviewersAndReviews.get(bookTitle);
                
                // Iterate over each reviewer and their review
                for (Map.Entry<String, String> entry : reviewers.entrySet()) {
                    // Write book title, author, username, and review
                    writer.printf("%s,%s,%s,%s%n", bookTitle, author != null ? author : "Unknown", entry.getKey(), entry.getValue());
                }
            }
            
            System.out.println("Data successfully written to reviews.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Double> averageRatingsMap2 = calculateAverageRatingsForAllBooks();
        Map<String, String> username = findReviewers(); 




  
        File originalFile = new File("files/personal.csv");
        File newFile = new File("files/personal_updated.csv");
    
        // Write the ratings to the new file
        writeRatingsToTheFile(averageRatingsMap2, countOfPeopleTo(), username, originalFile, newFile);
    }



    
      
}