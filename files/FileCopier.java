package files;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;

public class FileCopier {

    private void processCSV(String filename, DefaultTableModel model) {
        boolean isFirstLine = true;
        String outputFilename = "files/personal.csv"; // New file name
        try (BufferedReader br = new BufferedReader(new FileReader(filename));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename))) {
            // Write header to the new file
            bw.write("Title,Author,Rating,Review\n");

            String line;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line
                }
                // Split the line by ","
                String[] parts = line.split(",");
                // If the line ends with a comma, it means no author
                if (parts.length == 1) {
                    String title = parts[0].trim();
                    model.addRow(
                            new Object[]{removeQuotationMarks(title), "Unknown", "No Rating",
                                    "No Review"});
                    bw.write(String.format("%s,Unknown,No Rating,No Review\n",
                            removeQuotationMarks(title)));
                }
                // If the line starts with a comma, it means no title
                else if (parts[0].isEmpty()) {
                    String author = parts[1].trim();
                    model.addRow(new Object[]{"Unknown", author, "No Rating", "No Review"});
                    bw.write(String.format("Unknown,%s,No Rating,No Review\n",
                            author));
                }
                // Multiple titles for a single author
                else {
                    String author = parts[parts.length - 1].trim();
                    if (author.isEmpty()) {
                        author = "Unknown";
                    }
                    for (int i = 0; i < parts.length - 1; i++) {
                        String title = parts[i].trim();
                        if (title.isEmpty()) {
                            continue;
                        }
                        model.addRow(
                                new Object[]{removeQuotationMarks(title), author, "No Rating", "No Review"});
                        bw.write(String.format("%s,%s,No Rating,No Review\n",
                                removeQuotationMarks(title), author));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to remove quotation marks from a string
    private String removeQuotationMarks(String s) {
        return s.replace("\"", ""); // Remove all occurrences of double quotation marks
    }

    public static void main(String[] args) {
        FileCopier fileCopier = new FileCopier();
        fileCopier.processCSV("brodsky.csv", new DefaultTableModel());
    }
}
