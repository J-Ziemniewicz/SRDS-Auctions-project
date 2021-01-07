package auction.project;

import java.time.LocalTime;

public class Product {
    private int product_id;
    private java.time.LocalTime auction_end;
    private float buy_out_price;
    private float current_price;
    private float starting_price;
    private boolean is_sold;

    public Product(int product_id, java.time.LocalTime auction_end, float buy_out_price, float current_price, float starting_price,boolean is_sold)
    {
        this.product_id = product_id;
        this.auction_end = auction_end;
        this.buy_out_price = buy_out_price;
        this.current_price = current_price;
        this.starting_price = starting_price;
        this.is_sold = is_sold;
    }

    public boolean isIs_sold() {
        return is_sold;
    }

    public float getBuy_out_price() {
        return buy_out_price;
    }

    public float getCurrent_price() {
        return current_price;
    }

    public float getStarting_price() {
        return starting_price;
    }

    public int getProduct_id() {
        return product_id;
    }

    public LocalTime getAuction_end() {
        return auction_end;
    }

    public void setAuction_end(LocalTime auction_end) {
        this.auction_end = auction_end;
    }

    public void setBuy_out_price(float buy_out_price) {
        this.buy_out_price = buy_out_price;
    }

    public void setCurrent_price(float current_price) {
        this.current_price = current_price;
    }

    public void setIs_sold(boolean is_sold) {
        this.is_sold = is_sold;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setStarting_price(float starting_price) {
        this.starting_price = starting_price;
    }
}
