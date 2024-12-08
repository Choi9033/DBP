import oracle.jdbc.driver.OracleConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

public class CustomerPage extends JFrame {
    private JPanel productPanel;
    private JTextArea orderDetailsTextArea;
    private JTextField addressField;
    private JTextField messageField;
    private JTextField utensilsField;
    private JButton orderButton;
    private JButton reviewButton;
    private JButton backButton;
    private JButton orderCheckButton;
    private List<CartItem> cart = new ArrayList<>();
    private String storeId;
    private String userId;

    public CustomerPage(String userId, String storeId) {
        this.userId = userId;
        this.storeId = storeId;

        setTitle("상품 목록 - " + storeId);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navBar.setBackground(Color.DARK_GRAY);
        navBar.setPreferredSize(new Dimension(0, 50));

        JLabel titleLabel = new JLabel("디비상점");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        navBar.add(titleLabel);

        reviewButton = new JButton("리뷰 달기");
        reviewButton.setForeground(Color.WHITE);
        reviewButton.setBackground(Color.BLUE);
        reviewButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        reviewButton.setEnabled(true);
        reviewButton.addActionListener(e -> openReviewPage());
        navBar.add(reviewButton);

        orderCheckButton = new JButton("주문확인");
        orderCheckButton.addActionListener(e -> openOrderCheckPage());
        navBar.add(orderCheckButton);


        backButton = new JButton("뒤로 가기");
        backButton.addActionListener(e -> goBackToStoreSelectionPage());
        navBar.add(backButton);

        add(navBar, BorderLayout.NORTH);

        productPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        productPanel.setBackground(new Color(240, 240, 255));
        add(new JScrollPane(productPanel), BorderLayout.CENTER);

        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBorder(BorderFactory.createTitledBorder("주문 세부 정보"));

        JLabel addressLabel = new JLabel("배송 주소:");
        addressField = new JTextField(20);
        JLabel messageLabel = new JLabel("하고 싶은 말:");
        messageField = new JTextField(20);
        JLabel utensilsLabel = new JLabel("수저/포크 여부:");
        utensilsField = new JTextField(20);

        orderDetailsTextArea = new JTextArea(10, 20);
        orderDetailsTextArea.setEditable(false);
        JScrollPane orderScrollPane = new JScrollPane(orderDetailsTextArea);

        orderButton = new JButton("주문하기");
        orderButton.addActionListener(e -> placeOrder());

        orderPanel.add(addressLabel);
        orderPanel.add(addressField);
        orderPanel.add(Box.createVerticalStrut(10));
        orderPanel.add(messageLabel);
        orderPanel.add(messageField);
        orderPanel.add(Box.createVerticalStrut(10));
        orderPanel.add(utensilsLabel);
        orderPanel.add(utensilsField);
        orderPanel.add(Box.createVerticalStrut(10));
        orderPanel.add(orderScrollPane);
        orderPanel.add(Box.createVerticalStrut(10));
        orderPanel.add(orderButton);

        add(orderPanel, BorderLayout.EAST);

        loadProductsFromDatabase();
    }

    private void openReviewPage() {
        String checkOrderQuery = "SELECT COUNT(*) FROM 주문 WHERE 사용자고유ID = ? AND 상점고유ID = ? AND 상태 = '배달완료'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkOrderQuery)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    new ReviewPage(userId, storeId);
                } else {
                    JOptionPane.showMessageDialog(this, "배달완료된 주문이 없어 리뷰를 작성할 수 없습니다.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "리뷰 확인 중 오류가 발생했습니다.");
        }
    }

    private void goBackToStoreSelectionPage() {
        this.dispose();
        new StoreSelectionPage(userId).setVisible(true);
    }

    private void loadProductsFromDatabase() {
        String query = "SELECT 제품고유ID, 이름, 가격 FROM 제품 WHERE 상점고유ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, storeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String productId = rs.getString("제품고유ID");
                    String name = rs.getString("이름");
                    int price = rs.getInt("가격");

                    addProduct(productId, name, "설명없음", price);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "상품을 로드하는 중 오류가 발생했습니다.");
        }
    }

    private void addProduct(String productId, String name, String description, int price) {
        JPanel productCard = new JPanel();
        productCard.setLayout(new BoxLayout(productCard, BoxLayout.Y_AXIS));
        productCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        productCard.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JLabel descriptionLabel = new JLabel("<html>" + description + "</html>");
        descriptionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

        JLabel priceLabel = new JLabel(String.valueOf(price));
        priceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        priceLabel.setForeground(Color.BLUE);

        JButton addToCartButton = new JButton("장바구니에 담기");
        addToCartButton.addActionListener(e -> {
            cart.add(new CartItem(productId, name, price));
            updateOrderDetails();
        });

        productCard.add(nameLabel);
        productCard.add(descriptionLabel);
        productCard.add(priceLabel);
        productCard.add(addToCartButton);

        productPanel.add(productCard);
        productPanel.revalidate();
        productPanel.repaint();
    }

    private void updateOrderDetails() {
        StringBuilder details = new StringBuilder();
        for (CartItem item : cart) {
            details.append(item.getName()).append(" - ").append(item.getQuantity()).append("개\n");
        }
        orderDetailsTextArea.setText(details.toString());
    }




    private void placeOrder() {
        String address = addressField.getText();

        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "주소를 입력하세요.");
            return;
        }

        String callProcedure = "{CALL 주문_생성(?, ?, ?, ?)}"; // 주문_생성 프로시저 파라미터4개임
        try (Connection conn = DatabaseConnection.getConnection()) { // 디비 연결

            // 배열 설명자 생성
            ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("제품목록테이블", conn); // 임시데이터 저장용

            // 장바구니 데이터 준비
            Object[] productStructs = new Object[cart.size()];
            for (int i = 0; i < cart.size(); i++) {
                CartItem item = cart.get(i);
                productStructs[i] = new Object[]{item.getProductId(), item.getQuantity()};
            }

            // ARRAY 객체 생성
            ARRAY productArray = new ARRAY(descriptor, conn, productStructs);

            try (CallableStatement cstmt = conn.prepareCall(callProcedure)) {
                // 프로시저 파라미터 설정
                cstmt.setString(1, userId); // 사용자 ID
                cstmt.setString(2, storeId); // 상점 ID
                cstmt.setString(3, "대기");  // 상태 (예: "대기")
                cstmt.setArray(4, productArray); // 제품 목록

                // 프로시저 실행
                cstmt.execute();

                JOptionPane.showMessageDialog(this, "주문이 성공적으로 접수되었습니다.");
                cart.clear();
                updateOrderDetails();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "주문 중 오류가 발생했습니다.");
        }
    }
    private void openOrderCheckPage() {
        new OrderCheckPage(userId).setVisible(true);
    }



}
