import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReviewPage extends JFrame {
    private String userId;
    private String storeId;
    private JComboBox<Integer> ratingComboBox;
    private JTextArea reviewTextArea;
    private JButton submitButton;

    public ReviewPage(String userId, String storeId) {
        this.userId = userId;
        this.storeId = storeId;

        setTitle("리뷰 작성");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel ratingLabel = new JLabel("평점 (1~5):");
        ratingComboBox = new JComboBox<>();
        for (int i = 1; i <= 5; i++) {
            ratingComboBox.addItem(i);
        }

        JLabel reviewLabel = new JLabel("리뷰 내용:");
        reviewTextArea = new JTextArea(5, 20);
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(reviewTextArea);

        submitButton = new JButton("리뷰 작성");
        submitButton.addActionListener(e -> submitReview());

        panel.add(ratingLabel);
        panel.add(ratingComboBox);
        panel.add(reviewLabel);
        panel.add(scrollPane);
        panel.add(submitButton);

        add(panel, BorderLayout.CENTER);


        if (!isDeliveryCompleted()) {
            JOptionPane.showMessageDialog(this, "배달 완료 후 리뷰를 작성할 수 있습니다.");
            submitButton.setEnabled(false);
        }

        setVisible(true);
    }

    private boolean isDeliveryCompleted() {
        String query = "SELECT 상태 FROM 주문 WHERE 사용자고유ID = ? AND 상점고유ID = ? AND 상태 = '배달완료'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void submitReview() {
        int rating = (int) ratingComboBox.getSelectedItem();
        String reviewContent = reviewTextArea.getText();

        if (reviewContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "리뷰 내용을 입력하세요.");
            return;
        }


        String insertQuery = "INSERT INTO 리뷰 (사용자고유ID, 상점고유ID, 평점, 내용) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, storeId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, reviewContent);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "리뷰가 등록되었습니다.");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "리뷰 등록 중 오류가 발생했습니다.");
        }
    }
}