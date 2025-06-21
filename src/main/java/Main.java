public class Main {
    public static void main(String[] args) {
        // Load SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found");
            System.exit(1);
        }

        // Creating and showing the GUI on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            ExhibitionRegistrationSystem app = new ExhibitionRegistrationSystem();
            app.setVisible(true);
        });
    }
}
