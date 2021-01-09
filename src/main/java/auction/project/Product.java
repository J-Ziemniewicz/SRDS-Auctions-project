package auction.project;

import java.time.LocalTime;
import java.util.UUID;

public class Product {
    private UUID product_id;
    private java.time.LocalTime auction_end;
    private int buy_out_price;
    private int current_price;
    private boolean is_sold;
    private long buyer_id;

    public Product(UUID product_id, java.time.LocalTime auction_end, int buy_out_price, int starting_price, boolean is_sold )
    {
        this.product_id = product_id;
        this.auction_end = auction_end;
        this.buy_out_price = buy_out_price;
        this.current_price = starting_price;
        this.is_sold = is_sold;
        this.buyer_id = -1;
    }

    public Product(UUID product_id, java.time.LocalTime auction_end, int buy_out_price, int starting_price, boolean is_sold, long buyer_id)
    {
        this.product_id = product_id;
        this.auction_end = auction_end;
        this.buy_out_price = buy_out_price;
        this.current_price = starting_price;
        this.is_sold = is_sold;
        this.buyer_id = buyer_id;
    }

    public boolean isIs_sold() {
        return is_sold;
    }

    public int getBuy_out_price() {
        return buy_out_price;
    }

    public int getCurrent_price() {
        return current_price;
    }


    public UUID getProduct_id() {
        return product_id;
    }

    public LocalTime getAuction_end() {
        return auction_end;
    }

    public long getBuyer_id(){ return buyer_id;}

    public void setAuction_end(LocalTime auction_end) {
        this.auction_end = auction_end;
    }

    public void setBuy_out_price(int buy_out_price) {
        this.buy_out_price = buy_out_price;
    }

    public void setCurrent_price(int current_price) {
        this.current_price = current_price;
    }

    public void setIs_sold(boolean is_sold) {
        this.is_sold = is_sold;
    }

    public void setProduct_id(UUID product_id) {
        this.product_id = product_id;
    }

    public void setBuyer_id(int buyer_id) {
        this.buyer_id = buyer_id;
    }
}
