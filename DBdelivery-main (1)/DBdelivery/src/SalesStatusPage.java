import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SalesStatusPage extends JFrame {
    private String storeId;

    public SalesStatusPage(String storeId) {
        this.storeId = storeId;

        setTitle("매출 현황");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // JPanel 생성 및 UI 설정
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        loadSalesData(panel);

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadSalesData(JPanel panel) {
        double todayTotalSales = 0; // 오늘 총 매출 초기화
        String totalSalesQuery = "SELECT SUM(금액) AS 총매출 " +
                "FROM 매출 " +
                "JOIN 주문 ON 매출.주문고유ID = 주문.주문고유ID " +
                "WHERE 주문.상점고유ID = ? AND TRUNC(매출.판매날짜) = TRUNC(SYSDATE)";
        String salesDataQuery = "SELECT 매출.주문고유ID, 매출.금액, TO_CHAR(매출.판매날짜, 'YYYY-MM-DD HH24:MI:SS') AS 판매날짜 " +
                "FROM 매출 " +
                "JOIN 주문 ON 매출.주문고유ID = 주문.주문고유ID " +
                "WHERE 주문.상점고유ID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // 오늘의 총 매출 계산
            try (PreparedStatement pstmt = conn.prepareStatement(totalSalesQuery)) {
                pstmt.setString(1, storeId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        todayTotalSales = rs.getDouble("총매출");
                    }
                }
            }

            // 오늘 총 매출 라벨 추가
            JLabel totalSalesLabel = new JLabel("오늘 총 매출: " + String.format("%,.0f원", todayTotalSales), SwingConstants.CENTER);
            totalSalesLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            panel.add(totalSalesLabel); // 패널에 추가

            // 매출 데이터 표시
            try (PreparedStatement pstmt = conn.prepareStatement(salesDataQuery)) {
                pstmt.setString(1, storeId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String orderId = rs.getString("주문고유ID"); // 주문 고유 ID
                        double amount = rs.getDouble("금액"); // 매출 금액
                        String saleDate = rs.getString("판매날짜"); // 판매 날짜

                        // 매출 데이터 패널 생성
                        JPanel salesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        JLabel salesLabel = new JLabel("주문 ID: " + orderId + " | 금액: " + String.format("%,.0f", amount) + "원 | 날짜: " + saleDate);
                        salesPanel.add(salesLabel);
                        panel.add(salesPanel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "매출 데이터를 로드하는 중 오류가 발생했습니다.");
        }
    }




}
