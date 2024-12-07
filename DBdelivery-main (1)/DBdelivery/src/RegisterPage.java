import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPage extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JTextField contactField;
    private JTextField addressField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;

    private UserService userService = new UserService();
    private AdminService adminService = new AdminService();

    public RegisterPage() {
        setTitle("회원가입 페이지");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel roleLabel = new JLabel("역할:");
        roleLabel.setBounds(30, 30, 80, 25);
        add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"사용자", "관리자"});
        roleComboBox.setBounds(120, 30, 140, 25);
        add(roleComboBox);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(30, 70, 80, 25);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(120, 70, 140, 25);
        add(idField);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(30, 110, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 110, 140, 25);
        add(passwordField);

        JLabel contactLabel = new JLabel("연락처:");
        contactLabel.setBounds(30, 150, 80, 25);
        add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(120, 150, 140, 25);
        add(contactField);

        JLabel addressLabel = new JLabel("주소:");
        addressLabel.setBounds(30, 190, 80, 25);
        add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(120, 190, 140, 25);
        add(addressField);

        registerButton = new JButton("회원가입");
        registerButton.setBounds(90, 230, 100, 25);
        add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        setVisible(true);
    }

    private void handleRegister() {
        String role = (String) roleComboBox.getSelectedItem();
        String id = idField.getText();
        String password = new String(passwordField.getPassword());
        String contact = contactField.getText();
        String address = addressField.getText();

        System.out.println("Role: " + role);
        System.out.println("ID: " + id);
        System.out.println("Password: " + password);
        System.out.println("Contact: " + contact);
        System.out.println("Address: " + address);

        if (role.equals("사용자")) {
            if (userService.registerUser(id, password, contact, address)) {
                JOptionPane.showMessageDialog(this, "사용자 회원가입 성공!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "회원가입 실패!");
            }
        } else if (role.equals("관리자")) {
            System.out.println("Calling registerAdmin...");
            if (adminService.registerAdmin(id, password, contact, address)) {
                JOptionPane.showMessageDialog(this, "관리자 회원가입 성공!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "회원가입 실패!");
            }
        }
    }
}
