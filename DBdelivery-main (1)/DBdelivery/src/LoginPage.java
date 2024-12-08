import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JComboBox<String> roleComboBox;

    private UserService userService = new UserService();
    private AdminService adminService = new AdminService();

    public LoginPage() {
        setTitle("로그인 페이지");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel roleLabel = new JLabel("역할:");
        roleLabel.setBounds(30, 20, 80, 25);
        add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"사용자", "관리자"});
        roleComboBox.setBounds(120, 20, 140, 25);
        add(roleComboBox);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(30, 60, 80, 25);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(120, 60, 140, 25);
        add(idField);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setBounds(30, 100, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 100, 140, 25);
        add(passwordField);

        loginButton = new JButton("로그인");
        loginButton.setBounds(30, 140, 100, 25);
        add(loginButton);



        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });



        setVisible(true);
    }

    private void handleLogin() {
        String role = (String) roleComboBox.getSelectedItem();
        String id = idField.getText();
        String password = new String(passwordField.getPassword());

        if (role.equals("사용자")) {
            String userId = userService.loginUser(id, password);

            if (userId != null) {
                JOptionPane.showMessageDialog(this, "사용자 로그인 성공!");


                new StoreSelectionPage(userId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "ID 또는 비밀번호가 잘못되었습니다.");
            }
        } else if (role.equals("관리자")) {
            if (adminService.loginAdmin(id, password)) {
                JOptionPane.showMessageDialog(this, "관리자 로그인 성공!");


                new AdminDashboard(adminService.getStoreId(id)).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "ID 또는 비밀번호가 잘못되었습니다.");
            }
        }
    }

}
