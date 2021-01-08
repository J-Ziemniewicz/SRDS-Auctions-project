package auction.project;

import auction.project.backend.BackendException;
import auction.project.backend.BackendSession;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

public class User {
    BackendSession session;
    int id;

    User(BackendSession session, int id)
    {
        this.session = session;
        this.id = id;
    }

    boolean bidTheProduct(int priceToBid, @NotNull Product product) throws BackendException {

        Product productToBid = session.selectProduct(product.getProduct_id());
        LocalTime time = java.time.LocalTime.now();
        if(productToBid.isIs_sold())
            return productToBid.getBuyer_id() == id;


        System.out.println("Local time = " + time + " auction time " + productToBid.getAuction_end());
        if(time.isBefore(productToBid.getAuction_end()))// sprwadzić czy działa porównanie czasu
        {
            if(product.getCurrent_price() >= priceToBid)
                return false;

            if(priceToBid >= productToBid.getBuy_out_price())
            {
                System.out.println("???");
            }
            else {
                session.updateProductPrice(priceToBid, productToBid.getProduct_id());
                return true;
            }
        }
        return productToBid.getBuyer_id() == id;
    }

    boolean buyOutProduct(int priceToBid, @NotNull Product product) throws BackendException {
        Product productToBuy = session.selectProduct(product.getProduct_id());
        LocalTime time = java.time.LocalTime.now();
        if(productToBuy.isIs_sold())
            return productToBuy.getBuyer_id() == id;

        if(time.isBefore(productToBuy.getAuction_end()))
        {
            if(priceToBid >= productToBuy.getBuy_out_price())
            {
                session.updateProductBuyOut(true, productToBuy.getBuy_out_price(), productToBuy.getProduct_id());
                return true;
            }
            else
                return false;
        }
        return productToBuy.getBuyer_id() == id;
    }
}
