import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReviewManagementPage extends JFrame {
    private String storeId;

    public ReviewManagementPage(String storeId) {
        this.storeId = storeId;

        setTitle("리뷰 관리");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // JPanel 생성 및 UI 설정
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        loadReviews(panel);

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadReviews(JPanel panel) {
        String query = "SELECT * FROM 리뷰 WHERE 상점고유ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String reviewId = rs.getString("리뷰고유ID");
                    String customerName = rs.getString("사용자고유ID");
                    int rating = rs.getInt("평점");
                    String content = rs.getString("내용");

                    // 리뷰 패널 생성
                    JPanel reviewPanel = new JPanel();
                    reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
                    reviewPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                    JLabel reviewLabel = new JLabel("고객: " + customerName + " | 평점: " + rating);
                    JTextArea contentArea = new JTextArea(content);
                    contentArea.setEditable(false);

                    JButton deleteButton = new JButton("삭제");
                    deleteButton.addActionListener(e -> deleteReview(reviewId));

                    JButton replyButton = new JButton("답변");
                    replyButton.addActionListener(e -> addReply(reviewId));

                    reviewPanel.add(reviewLabel);
                    reviewPanel.add(new JScrollPane(contentArea));
                    reviewPanel.add(deleteButton);
                    reviewPanel.add(replyButton);

                    panel.add(reviewPanel);
                    panel.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteReview(String reviewId) {
        String query = "DELETE FROM 리뷰 WHERE 리뷰고유ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, reviewId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "리뷰가 삭제되었습니다.");
            dispose();  // 리뷰 삭제 후 페이지 닫기
            new ReviewManagementPage(storeId);  // 리뷰 목록 갱신
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "리뷰 삭제 중 오류가 발생했습니다.");
        }
    }

    private void addReply(String reviewId) {
        String reply = JOptionPane.showInputDialog(this, "답변을 작성하세요:");
        if (reply != null && !reply.trim().isEmpty()) {
            String query = "UPDATE 리뷰 SET 답변 = ? WHERE 리뷰고유ID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, reply);
                pstmt.setString(2, reviewId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "답변이 등록되었습니다.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "답변 등록 중 오류가 발생했습니다.");
            }
        }
    }
}
