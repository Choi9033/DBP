public class CartItem {
    private String productId;
    private String name;
    private int quantity;
    private int price;

    public CartItem(String productId, String name, int price) {
        this.productId = productId;
        this.name = name;
        this.quantity = 1;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public void increaseQuantity() {
        quantity++;
    }
}
