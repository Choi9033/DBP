import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DeliveryManagementPanel extends JPanel {
    private DeliveryService deliveryService = new DeliveryService();
    private String storeId;

    public DeliveryManagementPanel(String storeId) {
        this.storeId = storeId;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        List<String[]> deliveries = deliveryService.getDeliveriesByStore(storeId);
        for (String[] delivery : deliveries) {
            String deliveryId = delivery[0];
            String orderId = delivery[1];
            String status = delivery[2];

            JPanel deliveryPanel = new JPanel();
            deliveryPanel.add(new JLabel("배달 ID: " + deliveryId));
            deliveryPanel.add(new JLabel("주문 ID: " + orderId));
            deliveryPanel.add(new JLabel("상태: " + status));

            JButton trackButton = new JButton("배송 추적");
            JButton updateButton = new JButton("상태 변경");

            trackButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "배송 추적 기능: 배달 ID " + deliveryId);
            });

            updateButton.addActionListener(e -> {
                String newStatus = JOptionPane.showInputDialog("새 상태 입력 (예: 배달 중, 완료):");
                if (newStatus != null) {
                    deliveryService.updateDeliveryStatus(deliveryId, newStatus);
                    JOptionPane.showMessageDialog(this, "상태 변경 완료!");
                }
            });

            deliveryPanel.add(trackButton);
            deliveryPanel.add(updateButton);
            add(deliveryPanel);
        }
    }
}
