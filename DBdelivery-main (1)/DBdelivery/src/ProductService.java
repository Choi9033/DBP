import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    public List<String[]> getProductsByStore(String storeId) {
        List<String[]> products = new ArrayList<>();
        String sql = "SELECT 제품고유ID, 이름, 가격, 재고수량 FROM 제품 WHERE 상점고유ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new String[]{
                            rs.getString("제품고유ID"),
                            rs.getString("이름"),
                            rs.getString("가격"),
                            rs.getString("재고수량")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean updateProductStock(String productId, int newStock) {
        String sql = "UPDATE 제품 SET 재고수량 = ? WHERE 제품고유ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setString(2, productId);
            return pstmt.executeUpdate() > 0; // 업데이트 성공 시 true 반환
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // 실패 시 false 반환
    }


}
