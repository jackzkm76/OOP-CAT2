package RegistrationForm;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegistrationForm extends JFrame {
    // GUI components
    private JTextField txtName, txtMobile, txtAddress;
    private JRadioButton rbMale, rbFemale;
    private JDateChooser dateChooserDOB;
    private JCheckBox chkTerms;
    private JButton btnSubmit, btnReset;
    private JTextArea txtDisplay;

    // Database connection parameters
    private Connection conn;
    private PreparedStatement pst;

    public RegistrationForm() {
        // Set up the JFrame properties
        setTitle("Registration Form");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left panel for form input
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new GridLayout(7, 2));

        // Add Name field
        panelForm.add(new JLabel("Name:"));
        txtName = new JTextField();
        panelForm.add(txtName);

        // Add Mobile field
        panelForm.add(new JLabel("Mobile:"));
        txtMobile = new JTextField();
        panelForm.add(txtMobile);

        // Add Gender field
        panelForm.add(new JLabel("Gender:"));
        JPanel genderPanel = new JPanel();
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        panelForm.add(genderPanel);

        // Add Date of Birth field
        panelForm.add(new JLabel("DOB:"));
        dateChooserDOB = new JDateChooser();
        panelForm.add(dateChooserDOB);

        // Add Address field
        panelForm.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        panelForm.add(txtAddress);

        // Add Terms and Conditions checkbox
        panelForm.add(new JLabel("Accept Terms:"));
        chkTerms = new JCheckBox("I accept the Terms and Conditions");
        panelForm.add(chkTerms);

        // Add Submit and Reset buttons
        btnSubmit = new JButton("Submit");
        btnReset = new JButton("Reset");

        // Action listeners for buttons
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });

        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        // Add buttons to form panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnReset);
        panelForm.add(buttonPanel);

        // Right panel for displaying submitted data
        txtDisplay = new JTextArea(10, 20);
        txtDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtDisplay);

        // Add panels to the main frame
        add(panelForm, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize database connection
        connectDatabase();
    }

    // Method to connect to the database
    private void connectDatabase() {
        try {
            // Establish connection to MySQL
            String url = "jdbc:mysql://localhost:3306/user_registration";
            String username = "root"; // change this according to your DB username
            String password = ""; // change this according to your DB password

            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage());
        }
    }

    // Method to submit form data to the database
    private void submitForm() {
        try {
            // Get form data
            String name = txtName.getText();
            String mobile = txtMobile.getText();
            String gender = rbMale.isSelected() ? "Male" : "Female";
            java.util.Date dob = dateChooserDOB.getDate();
              if (dob == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid date of birth.");
            return;  // Return early if date is not selected
        }
            
            String address = txtAddress.getText();
            boolean termsAccepted = chkTerms.isSelected();

            if (name.isEmpty() || mobile.isEmpty() || !termsAccepted) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields and accept the terms.");
                return;
            }

            // SQL query to insert data into the users table
            String sql = "INSERT INTO users (name, mobile, gender, dob, address, terms) VALUES (?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, mobile);
            pst.setString(3, gender);
            pst.setDate(4, new java.sql.Date(dob.getTime()));
            pst.setString(5, address);
            pst.setBoolean(6, termsAccepted);

            // Execute update
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");

            // Display data on the right side
            displayData();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    // Method to display data from the database
    private void displayData() {
        try {
            String sql = "SELECT * FROM users";
            pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            StringBuilder data = new StringBuilder();
            while (rs.next()) {
                data.append("Name: ").append(rs.getString("name")).append("\n");
                data.append("Mobile: ").append(rs.getString("mobile")).append("\n");
                data.append("Gender: ").append(rs.getString("gender")).append("\n");
                data.append("DOB: ").append(rs.getDate("dob")).append("\n");
                data.append("Address: ").append(rs.getString("address")).append("\n");
                data.append("Accepted Terms: ").append(rs.getBoolean("terms") ? "Yes" : "No").append("\n");
                data.append("------------------------------\n");
            }

            txtDisplay.setText(data.toString());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
        }
    }

    // Method to reset the form
    private void resetForm() {
        txtName.setText("");
        txtMobile.setText("");
        txtAddress.setText("");
        rbMale.setSelected(false);
        rbFemale.setSelected(false);
        dateChooserDOB.setDate(null);
        chkTerms.setSelected(false);
        txtDisplay.setText("");
    }

    public static void main(String[] args) {
        // Run the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationForm().setVisible(true);
            }
        });
    }
}
