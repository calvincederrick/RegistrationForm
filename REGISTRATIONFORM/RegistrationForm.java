import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JFrame implements ActionListener {
    private JTextField nameField, addressField, contactField;
    private JRadioButton maleButton, femaleButton;
    private JButton registerButton, exitButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    public RegistrationForm() {
        setTitle("Registration Form");
        setLayout(new BorderLayout());
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(6, 2));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Gender:"));
        JPanel genderPanel = new JPanel(new FlowLayout());
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        formPanel.add(genderPanel);

        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        formPanel.add(new JLabel("Contact:"));
        contactField = new JTextField();
        formPanel.add(contactField);

        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        formPanel.add(registerButton);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        formPanel.add(exitButton);

        add(formPanel, BorderLayout.WEST);

        String[] columnNames = {"ID", "Name", "Gender", "Address", "Contact"};
        tableModel = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(tableModel);
        add(new JScrollPane(dataTable), BorderLayout.CENTER);

        loadUserData();
        setVisible(true);
    }

    private void loadUserData() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "your_username", "your_password");
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String address = rs.getString("address");
                String contact = rs.getString("contact");
                tableModel.addRow(new Object[]{id, name, gender, address, contact});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String name = nameField.getText();
            String gender = maleButton.isSelected() ? "Male" : "Female";
            String address = addressField.getText();
            String contact = contactField.getText();

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "your_username", "your_password");
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users(name, gender, address, contact) VALUES(?, ?, ?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setString(2, gender);
                pstmt.setString(3, address);
                pstmt.setString(4, contact);
                pstmt.executeUpdate();
                tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, name, gender, address, contact});
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistrationForm::new);
    }
}
