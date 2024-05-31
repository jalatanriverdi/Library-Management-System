# Main
The Main class is starting point of our program. Purpose is to start the application by creating and displaying the 'LoginRegistration' window. 
When you run the program, it starts by executing the main method. This method creates an instance of the LoginRegistration class and makes it visible on the screen. As a result, a login and registration form appears, allowing users to either log in or sign up.  

# LoginRegistration
This class is crucial for managing user logins and registrations. It has a user interface with input fields for username and password, and buttons for login and registration actions. When a user attempts to log in, the system checks their login information against a 'HashMap' loaded from 'usernames.csv'. If the login information matches, users are sent to either an admin-specific or general homepage based on their roles. For registration, the system ensures that usernames and passwords are at least 4 characters long and the username isn't already taken. Successful registrations add the new user's data to the CSV file and create a personalized CSV for them. Finally, the class also handles exceptions and provides feedback on dialog boxes.

# HomePage
The HomePage class in this application extends JFrame. It represents its role as a window. The interface includes a welcome label displayed at the top with the text "Welcome to the HomePage". Three main buttons are provided: "General Database", "Personal Database", and "Logout". 

# HomePageAdmin
The HomePageAdmin class is a component of this application. It provides a user interface specifically designed for system admins. It's built using Java's Swing library. The class extends 'JFrame' by making it a window that can be displayed on our desktop.

This class is responsible for providing the initial interface that admin sees after logging into the system. Also here three main buttons are provided but different buttons: "General Database", "User Management", and "Logout".


# AdminUserDeletion
This class provides a interface with table listing all users. Admins can easily find users by typing in a search bar, sort the list by clicking on the column headers and delete users with a simple click of a button. The deletion process includes one confirmation step to avoid wrong removals. There's also a "HomePage" button that lets admins quickly return to the main admin panel.

# GeneralDatabase
This is a simplest Java Swing GUI application which uses computer tool Java Swing for viewing and managing books. You will have on a screen the list of books which displays the book's title, author name, rating, and the review. You can not reorder the list in the program directly, but you can do some other things, for example, search the list by title or author, sort the list, and add books to your own list. First, you have to ensure that Java is on your computer and you have personal_updated files in it. csv and reviews. csv should be located in a folder which is the files. When you enter the program, you browse the books list and then you select a book to add to your own book list. Also, the back button that brings you back to the main homepage of the program has been added. The program is simple and you can chose books but changing book details is not possible directly and with the tool.

# GeneralDatabaseAdmin
The General Database Admin is a Java application that can be used to manage database with a GUI. It is responsible for storing information like books titles, authors, rating and reviews. You can write, erase, change, and look up words. The table in the application varies entries according to click on column headers and it narrows entries down as the search input is typed. First, you will need a Java and an IDE like Eclipse. You can go ahead and click the ‘download project’ button, open it in your IDE and then run the main method from the GeneralDatabaseAdmin class to launch it. The main window is built around this table which is loaded from the CSV file. For example, you can add new entries that already have ratings and reviews, change or remove existing entries after confirming, and quickly find entries by typing in a search box. In case of any doubt or in order to get more information, it is possible to follow the project documentation or contact via the email.

# PersonalDatabase
This app will keep a track of your books. You may sign in to add, modify or erase books, rate them, and post reviews. It has an added feature of allowing you to keep the record of when you begin and when you finish reading the book, and at the same time saving your data for future reference. If you want to use the program, make sure you have Java installed on your PC, download the app, and open PersonalDatabase. open the sample java file to get started. When you do the login, you can see a table of your books, add new books, edit existing entries or use the search tool to easily locate books. If you are interested in tweaking the app, you can edit the code and make it available to the public. The app is open-source and you may use it for free under MIT License. 

# FileCopier
It is for copying brodsky file and making proper adjustments for databases to new csv file (personal.csv).

# RatingCalculator
The RatingCalculator is a Java program which will use reviews and ratings from the CSV files to see how the readers rate books. It determines the average rating for each book and how many have been rated. Furthermore, it also collects all the comments posted by contributors. If you want to run this application, there should be a folder called users and all the CSV files should be in them and Java installed in your machine. It is when you run the program then the program will come up with the average ratings for every book as well as the comments. It helps a lot if you are looking for which books most people read and how they discuss about them.

# Code Structure
This project consists of several classes that manage different functions. Together these classes handles user management, data processing and provides interactive tools for both regular users and admins.

# Thanks
Thank you for using the application!
