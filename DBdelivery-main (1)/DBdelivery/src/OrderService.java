import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {


    public void updateOrderStatus(String orderId, String status) {
        String query = "UPDATE 주문 SET 상태 = ? WHERE 주문고유ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setString(2, orderId);
            pstmt.executeUpdate();

            if ("주문승인".equals(status)) {
                insertIntoDelivery(orderId); // 배달 정보 삽입
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void insertIntoDelivery(String orderId) {
        String query = "INSERT INTO 배달 (주문고유ID, 상태, 시작시간) VALUES (?, '배달준비중', SYSDATE)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, orderId); // 1번 매개변수로 orderId 설정
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public List<String[]> getOrdersByStore(String storeId) {
        String query = "SELECT 주문고유ID, 사용자고유ID, 상태, 주문시간, 배달예상시간 " +
                "FROM 주문 WHERE 상점고유ID = ? AND 상태 = '대기'";
        List<String[]> orders = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, storeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] order = new String[5];
                order[0] = rs.getString("주문고유ID");
                order[1] = rs.getString("사용자고유ID");
                order[2] = rs.getString("상태");
                order[3] = rs.getString("주문시간");
                order[4] = rs.getString("배달예상시간");
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

}
