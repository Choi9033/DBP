import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminService {

    public boolean registerAdmin(String id, String password, String contact, String address) {
        String sql = "INSERT INTO 상점 (ID, 비밀번호, 연락처, 주소, 상태, 등록날짜) VALUES (?, ?, ?, ?, '영업중', SYSDATE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            pstmt.setString(3, contact);
            pstmt.setString(4, address);
            return pstmt.executeUpdate() > 0; // 성공 시 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean loginAdmin(String id, String password) {
        String sql = "SELECT 상점고유ID FROM 상점 WHERE ID = ? AND 비밀번호 = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getStoreId(String adminId) {
        String sql = "SELECT 상점고유ID FROM 상점 WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adminId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("상점고유ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
