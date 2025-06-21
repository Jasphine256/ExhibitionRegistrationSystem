import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class ExhibitionRegistrationSystem extends JFrame {
    // Database connection details for SQLite
    private static final String DB_URL = "jdbc:sqlite:VUE_Exhibition.db";
    private Connection connection;

    // Components for forms
    private JTextField txtRegId, txtName, txtFaculty, txtProjectTitle, txtContact, txtEmail;
    private JLabel lblImage;
    private JButton btnRegister, btnSearch, btnUpdate, btnDelete, btnClear, btnExit, btnUpload;
    private String imagePath;

    public ExhibitionRegistrationSystem() {
        setTitle("Victoria University Exhibition Registration System (SQLite)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initializing database connection
        connectToDatabase();

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add form fields
        formPanel.add(new JLabel("Registration ID:"));
        txtRegId = new JTextField();
        formPanel.add(txtRegId);

        formPanel.add(new JLabel("Student Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Faculty:"));
        txtFaculty = new JTextField();
        formPanel.add(txtFaculty);

        formPanel.add(new JLabel("Project Title:"));
        txtProjectTitle = new JTextField();
        formPanel.add(txtProjectTitle);

        formPanel.add(new JLabel("Contact Number:"));
        txtContact = new JTextField();
        formPanel.add(txtContact);

        formPanel.add(new JLabel("Email Address:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Project Image:"));
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnUpload = new JButton("Upload Image");
        lblImage = new JLabel("No image selected");
        imagePanel.add(btnUpload);
        imagePanel.add(lblImage);
        formPanel.add(imagePanel);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnRegister = new JButton("Register");
        btnSearch = new JButton("Search");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");
        btnExit = new JButton("Exit");

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExit);

        // Add components to frame
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add event listeners
        addEventListeners();
    }

    private void connectToDatabase() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
            System.out.println("Connected to SQLite database successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Participants (" +
                "RegID TEXT PRIMARY KEY, " +
                "Name TEXT NOT NULL, " +
                "Faculty TEXT NOT NULL, " +
                "ProjectTitle TEXT NOT NULL, " +
                "Contact TEXT NOT NULL, " +
                "Email TEXT NOT NULL, " +
                "ImagePath TEXT)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);

            // Insert sample data if table is empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Participants");
            if (rs.getInt(1) == 0) {
                insertSampleData();
            }
        }
    }

    private void insertSampleData() throws SQLException {
        String[] sampleData = {
                "INSERT INTO Participants VALUES ('VUE001', 'Atwine Hazel', 'Computer Science', 'AI-Based Weather Prediction', '0712345678', 'john.smith@vu.edu', '')",
                "INSERT INTO Participants VALUES ('VUE002', 'Mawanda Marques', 'Engineering', 'Smart Irrigation System', '0723456789', 'sarah.j@vu.edu', '')",
                "INSERT INTO Participants VALUES ('VUE003', 'Isaac Marvin', 'Biotechnology', 'Bio-Degradable Plastics', '0734567890', 'michael.b@vu.edu', '')",
                "INSERT INTO Participants VALUES ('VUE004', 'Emily Davis', 'Mathematics', 'Quantum Computing Algorithms', '0745678901', 'emily.d@vu.edu', '')",
                "INSERT INTO Participants VALUES ('VUE005', 'David Wilson', 'Physics', 'Solar Energy Optimization', '0756789012', 'david.w@vu.edu', '')"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : sampleData) {
                stmt.execute(sql);
            }
        }
    }

    private void addEventListeners() {
        btnRegister.addActionListener(e -> registerParticipant());
        btnSearch.addActionListener(e -> searchParticipant());
        btnUpdate.addActionListener(e -> updateParticipant());
        btnDelete.addActionListener(e -> deleteParticipant());
        btnClear.addActionListener(e -> clearForm());
        btnExit.addActionListener(e -> System.exit(0));
        btnUpload.addActionListener(e -> uploadImage());
    }

    private void registerParticipant() {
        if (!validateInput()) return;

        try {
            String sql = "INSERT INTO Participants (RegID, Name, Faculty, ProjectTitle, Contact, Email, ImagePath) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setString(1, txtRegId.getText());
            pstmt.setString(2, txtName.getText());
            pstmt.setString(3, txtFaculty.getText());
            pstmt.setString(4, txtProjectTitle.getText());
            pstmt.setString(5, txtContact.getText());
            pstmt.setString(6, txtEmail.getText());
            pstmt.setString(7, imagePath);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Participant registered successfully!");
                clearForm();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchParticipant() {
        String regId = txtRegId.getText().trim();
        if (regId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Registration ID to search",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "SELECT * FROM Participants WHERE RegID = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, regId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                txtName.setText(rs.getString("Name"));
                txtFaculty.setText(rs.getString("Faculty"));
                txtProjectTitle.setText(rs.getString("ProjectTitle"));
                txtContact.setText(rs.getString("Contact"));
                txtEmail.setText(rs.getString("Email"));

                imagePath = rs.getString("ImagePath");
                if (imagePath != null && !imagePath.isEmpty()) {
                    lblImage.setText(new File(imagePath).getName());
                } else {
                    lblImage.setText("No image available");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No participant found with ID: " + regId,
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateParticipant() {
        if (!validateInput()) return;

        try {
            String sql = "UPDATE Participants SET Name = ?, Faculty = ?, ProjectTitle = ?, " +
                    "Contact = ?, Email = ?, ImagePath = ? WHERE RegID = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtFaculty.getText());
            pstmt.setString(3, txtProjectTitle.getText());
            pstmt.setString(4, txtContact.getText());
            pstmt.setString(5, txtEmail.getText());
            pstmt.setString(6, imagePath);
            pstmt.setString(7, txtRegId.getText());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Participant updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No participant found with ID: " + txtRegId.getText(),
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteParticipant() {
        String regId = txtRegId.getText().trim();
        if (regId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Registration ID to delete",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete participant with ID: " + regId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            String sql = "DELETE FROM Participants WHERE RegID = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, regId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Participant deleted successfully!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "No participant found with ID: " + regId,
                        "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePath = selectedFile.getAbsolutePath();
            lblImage.setText(selectedFile.getName());
        }
    }

    private void clearForm() {
        txtRegId.setText("");
        txtName.setText("");
        txtFaculty.setText("");
        txtProjectTitle.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        lblImage.setText("No image selected");
        imagePath = null;
    }

    private boolean validateInput() {
        if (txtRegId.getText().trim().isEmpty() ||
                txtName.getText().trim().isEmpty() ||
                txtFaculty.getText().trim().isEmpty() ||
                txtProjectTitle.getText().trim().isEmpty() ||
                txtContact.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "All fields are required",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!txtEmail.getText().matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!txtContact.getText().matches("^[0-9]{10,15}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid contact number (10-15 digits)",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}

