import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class OrderCheckPage extends JFrame {
    private JTextArea orderListTextArea;
    private String userId;

    public OrderCheckPage(String userId) {
        this.userId = userId;

        setTitle("주문 내역 확인");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("주문 내역", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        orderListTextArea = new JTextArea();
        orderListTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderListTextArea);
        add(scrollPane, BorderLayout.CENTER);

        loadOrderHistoryFromDatabase();
    }

    private void loadOrderHistoryFromDatabase() {
        String enableDbmsOutput = "BEGIN DBMS_OUTPUT.ENABLE(); END;";
        String callProcedure = "{CALL 사용자_주문제품목록(?)}";
        String getDbmsOutput = "BEGIN DBMS_OUTPUT.GET_LINE(?, ?); END;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement enableStmt = conn.createStatement();
             CallableStatement procedureStmt = conn.prepareCall(callProcedure);
             CallableStatement getOutputStmt = conn.prepareCall(getDbmsOutput)) {

            // DBMS_OUTPUT 활성화
            enableStmt.execute(enableDbmsOutput);

            // 프로시저 실행
            procedureStmt.setString(1, userId);
            procedureStmt.execute();

            // DBMS_OUTPUT 버퍼 읽기
            StringBuilder result = new StringBuilder();
            String line;
            int status;

            do {
                getOutputStmt.registerOutParameter(1, Types.VARCHAR);
                getOutputStmt.registerOutParameter(2, Types.INTEGER);
                getOutputStmt.execute();

                line = getOutputStmt.getString(1);
                status = getOutputStmt.getInt(2);

                if (line != null) {
                    result.append(line).append("\n");
                }
            } while (status == 0);

            // 결과를 텍스트 영역에 출력
            if (result.length() > 0) {
                orderListTextArea.setText(result.toString());
            } else {
                orderListTextArea.setText("주문 내역이 없습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "주문 내역을 불러오는 중 오류가 발생했습니다.");
        }
    }

}
