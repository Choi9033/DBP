import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {


    public String loginUser(String id, String password) {
        String sql = "SELECT 사용자고유ID FROM 사용자 WHERE ID = ? AND 비밀번호 = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("사용자고유ID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getUserIdById(String id) {
        String sql = "SELECT 사용자고유ID FROM 사용자 WHERE ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("사용자고유ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 사용자 등록 시 사용자 고유 ID 자동 생성
    public boolean registerUser(String id, String password, String contact, String address) {
        String sql = "INSERT INTO 사용자 (ID, 비밀번호, 연락처, 주소) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            pstmt.setString(3, contact);
            pstmt.setString(4, address);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
