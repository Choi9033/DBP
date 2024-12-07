import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StoreSelectionPage extends JFrame {
    private String userId;

    public StoreSelectionPage(String userId) {
        this.userId = userId;

        setTitle("상점 선택");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 2, 10, 10));

        loadStoresFromDatabase();

        setLocationRelativeTo(null);
    }

    private void loadStoresFromDatabase() {
        String query = "SELECT 상점고유ID, 상점명 FROM 상점";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String storeId = rs.getString("상점고유ID");
                String storeName = rs.getString("상점명");

                JButton storeButton = new JButton(storeName);
                storeButton.addActionListener(e -> {
                    new CustomerPage(userId, storeId).setVisible(true);
                    dispose();
                });

                add(storeButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "상점을 불러오는 중 오류가 발생했습니다.");
        }
    }
}
