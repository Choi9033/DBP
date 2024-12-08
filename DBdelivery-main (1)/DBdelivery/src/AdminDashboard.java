import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private String storeId;
    private JPanel orderPanel;
    private JPanel deliveryPanel;
    private JPanel menuPanel;
    private JLabel salesLabel;
    private String userId;

    public AdminDashboard(String storeId) {
        this.storeId = storeId;

        setTitle("내 배달 앱 - 관리자");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 메뉴 바 초기화
        JMenuBar menuBar = new JMenuBar();
        JMenu homeMenu = new JMenu("홈 메뉴");
        JMenu manageMenu = new JMenu("관리자");
        JMenu reviewMenu = new JMenu("리뷰");
        JMenu settingsMenu = new JMenu("설정");
        menuBar.add(homeMenu);
        menuBar.add(manageMenu);
        menuBar.add(reviewMenu);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);


        // "리뷰 관리" 메뉴 항목 추가
        JMenuItem manageReviewItem = new JMenuItem("리뷰 관리");
        manageReviewItem.addActionListener(e -> {
            new ReviewManagementPage(storeId); // 리뷰 관리 페이지 열기
        });
        reviewMenu.add(manageReviewItem); // "리뷰" 메뉴에 추가
        // "관리자" 메뉴에 "매출 현황" 추가
        JMenuItem salesStatusItem = new JMenuItem("매출 현황");
        salesStatusItem.addActionListener(e -> {
            new SalesStatusPage(storeId); // 매출 현황 페이지 열기
        });
        manageMenu.add(salesStatusItem);

        // 상단 제목 패널 추가
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // 메인 패널 초기화
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 3, 10, 10)); // 1행 3열 레이아웃


        orderPanel = createOrderPanel(); // 주문 관리 패널 초기화
        mainPanel.add(orderPanel);

        deliveryPanel = createDeliveryPanel(); // 배달 관리 패널 초기화
        mainPanel.add(deliveryPanel);

        menuPanel = createMenuPanel(); // 메뉴 관리 패널 초기화
        mainPanel.add(menuPanel);

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);

    }

    // 상단 제목 패널 생성
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3)); // 1행 3열 레이아웃

        // 각 제목 추가
        JLabel orderLabel = new JLabel("주문 관리", SwingConstants.CENTER);
        orderLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JLabel deliveryLabel = new JLabel("배달 관리", SwingConstants.CENTER);
        deliveryLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JLabel menuLabel = new JLabel("재고 관리", SwingConstants.CENTER);
        menuLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        // 패널에 제목 추가
        panel.add(orderLabel);
        panel.add(deliveryLabel);
        panel.add(menuLabel);

        return panel;
    }



    // 주문 관리 패널 생성
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        OrderService orderService = new OrderService();
        List<String[]> orders = orderService.getOrdersByStore(storeId);

        if (orders == null || orders.isEmpty()) {
            JLabel noOrdersLabel = new JLabel("현재 주문이 없습니다.");
            panel.add(noOrdersLabel);
        } else {
            for (String[] order : orders) {
                JPanel orderItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel orderLabel = new JLabel("주문 ID: " + order[0] + " (" + order[1] + ")");
                JButton acceptButton = new JButton("주문 수락");
                JButton rejectButton = new JButton("주문 거절");

                acceptButton.addActionListener(e -> {
                    orderService.updateOrderStatus(order[0], "주문승인");
                    JOptionPane.showMessageDialog(this, "주문 수락 완료!");
                    refreshOrderList();
                    refreshDeliveryList(); // 배달 목록 새로 고침
                });

                rejectButton.addActionListener(e -> {
                    orderService.updateOrderStatus(order[0], "주문거절");
                    JOptionPane.showMessageDialog(this, "주문 거절 완료!");
                    refreshOrderList();
                });

                orderItem.add(orderLabel);
                orderItem.add(acceptButton);
                orderItem.add(rejectButton);
                panel.add(orderItem);
            }
        }

        return panel;
    }



    // 배달 관리 패널 생성
    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        DeliveryService deliveryService = new DeliveryService();
        List<String[]> deliveries = deliveryService.getDeliveriesByStore(storeId); // 배달 정보 가져오기

        if (deliveries == null || deliveries.isEmpty()) {
            JLabel noDeliveriesLabel = new JLabel("현재 배달이 없습니다.");
            panel.add(noDeliveriesLabel);
        } else {
            for (String[] delivery : deliveries) {
                JPanel deliveryItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel deliveryLabel = new JLabel("배달 ID: " + delivery[0] + " (상태: " + delivery[2] + ")");

                JButton updateButton = new JButton("배달 완료");



                updateButton.addActionListener(e -> {
                    String orderId = DeliveryService.getOrderIdByDeliveryId(delivery[0]); // 배달 ID를 사용하여 주문 ID 조회
                    if (orderId != null) {
                        completeDelivery(orderId); // 주문 ID를 기반으로 배달 완료 처리
                        JOptionPane.showMessageDialog(this, "배달이 완료되었습니다!");
                        refreshDeliveryList(); // 배달 목록 새로 고침
                    } else {
                        JOptionPane.showMessageDialog(this, "해당 배달 ID에 연결된 주문이 없습니다.");
                    }
                });

                deliveryItem.add(deliveryLabel);
                deliveryItem.add(updateButton);
                panel.add(deliveryItem);
            }
        }

        return panel;
    }


    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        ProductService productService = new ProductService();
        List<String[]> products = productService.getProductsByStore(storeId);

        for (String[] product : products) {
            JPanel productItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel productLabel = new JLabel(product[1] + " (재고: " + product[3] + ")");
            JButton editButton = new JButton("재고 수정");

            editButton.addActionListener(e -> {
                String newStock = JOptionPane.showInputDialog("새 재고 입력:");
                if (newStock != null) {
                    productService.updateProductStock(product[0], Integer.parseInt(newStock));
                    JOptionPane.showMessageDialog(this, "재고 수정 완료!");
                    refreshMenuList(); // 메뉴 목록 새로 고침
                }
            });

            productItem.add(productLabel);
            productItem.add(editButton);
            panel.add(productItem);
        }

        return panel;
    }

    private JPanel createManageMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 리뷰 관리 버튼 추가
        JButton reviewButton = new JButton("리뷰 관리");
        reviewButton.addActionListener(e -> {
            // 상점 ID와 관리자 ID를 넘겨서
            new ReviewManagementPage (storeId); // 상점 아이디와 관리자 아이디를 전달
        });

        panel.add(reviewButton);  // 리뷰 관리 버튼을 패널에 추가

        return panel;
    }


    // 배달 완료 후 매출 추가 및 판매 날짜 기록
    private void completeDelivery(String orderId) {
        Connection conn = null; // 블록 밖에서 선언
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 주문의 총 가격 계산
            String getOrderPriceQuery = "SELECT SUM(가격 * 수량) FROM 주문상세 WHERE 주문고유ID = ?";
            double orderPrice = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(getOrderPriceQuery)) {
                pstmt.setString(1, orderId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        orderPrice = rs.getDouble(1);
                    }
                }
            }

            // 2. 매출 테이블에 기록
            String insertSalesQuery = "INSERT INTO 매출 (주문고유ID, 금액, 판매날짜) VALUES (?, ?, SYSDATE)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSalesQuery)) {
                pstmt.setString(1, orderId);
                pstmt.setDouble(2, orderPrice);
                pstmt.executeUpdate();
            }
            // 3. 배달 테이블의 상태를 "배달완료"로 업데이트
            String updateDeliveryStatusQuery = "UPDATE 배달 SET 상태 = '배달완료' WHERE 주문고유ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateDeliveryStatusQuery)) {
                pstmt.setString(1, orderId);
                pstmt.executeUpdate();
            }
            // 4. 주문 테이블의 상태를 "배달완료"로 업데이트
            String updateOrderStatusQuery = "UPDATE 주문 SET 상태 = '배달완료' WHERE 주문고유ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateOrderStatusQuery)) {
                pstmt.setString(1, orderId);
                pstmt.executeUpdate();
            }
            conn.commit(); // 트랜잭션 커밋
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // 오류 발생 시 롤백
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // 자원 해제
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }


    private void refreshOrderList() {
        JPanel newOrderPanel = createOrderPanel();
        orderPanel.removeAll();
        orderPanel.setLayout(new BorderLayout());
        orderPanel.add(newOrderPanel, BorderLayout.CENTER);
        orderPanel.revalidate();
        orderPanel.repaint();
    }



    private void refreshDeliveryList() {
        JPanel newDeliveryPanel = createDeliveryPanel();
        deliveryPanel.removeAll();
        deliveryPanel.add(newDeliveryPanel, BorderLayout.CENTER);
        deliveryPanel.revalidate();
        deliveryPanel.repaint();

    }


    private void refreshMenuList() {
        JPanel newMenuPanel = createMenuPanel();
        menuPanel.removeAll();
        menuPanel.add(newMenuPanel, BorderLayout.CENTER);
        menuPanel.revalidate();
        menuPanel.repaint();
    }
}

