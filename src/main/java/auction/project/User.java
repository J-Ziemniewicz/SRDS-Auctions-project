package auction.project;

import auction.project.backend.BackendException;
import auction.project.backend.BackendSession;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class User {
    private final BackendSession session;
    private final long id;
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    User(BackendSession session, long id)
    {
        this.session = session;
        this.id = id;
    }

    long getId(){ return id;}

    boolean bidTheProduct(int priceToBid, @NotNull Product product) throws BackendException {

        Product productToBid = session.selectProduct(product.getProduct_id());
        LocalTime time = java.time.LocalTime.now();
        if (productToBid.isIs_sold()) {
            logger.info("Product " + productToBid.getProduct_id() + " already sold");
            return productToBid.getBuyer_id() == id;
        }
        if (time.isBefore(productToBid.getAuction_end()))
        {
            if (product.getCurrent_price() >= priceToBid) {
                return false;
            }
            else if(priceToBid >= productToBid.getBuy_out_price())
            {
                session.updateProductBuyOut(true, productToBid.getBuy_out_price(), productToBid.getProduct_id(), id);
            }
            else {
                session.updateProductPrice(priceToBid, productToBid.getProduct_id(), id);
            }
            Product afterBidProduct = session.selectProduct(product.getProduct_id());

            return afterBidProduct.getBuyer_id() == id;
        }
        else {
            System.out.println("Auctions on this product has ended");
            if (productToBid.getBuyer_id() != -1)
                session.updateProductSold(true, productToBid.getProduct_id());
        }

        return productToBid.getBuyer_id() == id;
    }

    boolean buyOutProduct(@NotNull Product product) throws BackendException, InterruptedException {
        Product productToBuy = session.selectProduct(product.getProduct_id());
        LocalTime time = java.time.LocalTime.now();
        if (productToBuy.isIs_sold()) {
            logger.info("Product " + productToBuy.getProduct_id() + " already sold");
            return productToBuy.getBuyer_id() == id;
        }
        if (time.isBefore(productToBuy.getAuction_end()))
        {
            session.updateProductBuyOut(true, productToBuy.getBuy_out_price(), productToBuy.getProduct_id(), id);
            Product afterBidProduct = session.selectProduct(product.getProduct_id());
            return afterBidProduct.getBuyer_id() == id;
        }
        else if (productToBuy.getBuyer_id() != -1) {
            session.updateProductSold(true, productToBuy.getProduct_id());
        }
        return productToBuy.getBuyer_id() == id;
    }
}
