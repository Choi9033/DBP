import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryService {


    public List<String[]> getDeliveriesByStore(String storeId) {
        List<String[]> deliveries = new ArrayList<>();
        // 상점 테이블과 조인하여 배달 정보를 가져오는 쿼리
        String query = "SELECT b.배달ID, b.주문고유ID, b.상태, b.시작시간, b.완료시간 " +
                "FROM 배달 b " +
                "JOIN 주문 o ON b.주문고유ID = o.주문고유ID " +
                "WHERE o.상점고유ID = ? AND b.상태 = '배달준비중'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, storeId);  // 상점 고유 ID를 바인딩

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String deliveryId = rs.getString("배달ID");
                    String orderId = rs.getString("주문고유ID");
                    String status = rs.getString("상태");
                    String startTime = rs.getString("시작시간");
                    String endTime = rs.getString("완료시간");

                    deliveries.add(new String[]{deliveryId, orderId, status, startTime, endTime});
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deliveries;
    }

    public static String getOrderIdByDeliveryId(String deliveryId) {
        String orderId = null; // 기본값을 null로 설정
        String query = "SELECT 주문고유ID FROM 배달 WHERE 배달ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, deliveryId); // 배달 ID 설정
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    orderId = rs.getString("주문고유ID"); // 주문 ID 가져오기
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId; // 주문 ID 반환
    }



    public void updateDeliveryStatus(String deliveryId, String newStatus) {
        String updateQuery = "UPDATE 배달 SET 상태 = ? WHERE 배달ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, deliveryId);
            pstmt.executeUpdate();

            System.out.println("배달 상태가 '" + newStatus + "'로 업데이트되었습니다.");


            if ("배달완료".equals(newStatus)) {
                updateOrderStatus(deliveryId, "배달완료");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateOrderStatus(String deliveryId, String newStatus) {
        String selectQuery = "SELECT 주문고유ID FROM 배달 WHERE 배달ID = ?";
        String updateQuery = "UPDATE 주문 SET 상태 = ? WHERE 주문고유ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {

            pstmt.setString(1, deliveryId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String orderId = rs.getString("주문고유ID");


                try (PreparedStatement updatePstmt = conn.prepareStatement(updateQuery)) {
                    updatePstmt.setString(1, newStatus);
                    updatePstmt.setString(2, orderId);
                    updatePstmt.executeUpdate();

                    System.out.println("주문 상태가 '" + newStatus + "'로 업데이트되었습니다.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
