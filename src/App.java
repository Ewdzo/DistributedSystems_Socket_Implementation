import java.io.File;

public class App {
    public static void main(String[] args) throws Exception {
        String[] pathnames;
        String files = "Files: ";

        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File f = new File("./src/files");

        // Populates the array with names of files and directories
        pathnames = f.list();

        // For each pathname in the pathnames array
        for (String pathname : pathnames) {
            files += pathname + " ";
            // Print the names of files and directories
        }

        System.out.println(files);
    }
}
