package auction.project;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuctionTest {
    @Test
    public void FizzBuzzNormalNumbers() {

        Auction fb = new Auction();
        Assert.assertEquals("1", fb.convert(1));
        Assert.assertEquals("2", fb.convert(2));
    }

    @Test
    public void FizzBuzzThreeNumbers() {

        Auction fb = new Auction();
        Assert.assertEquals("Fizz", fb.convert(3));
    }

    @Test
    public void FizzBuzzFiveNumbers() {

        Auction fb = new Auction();
        Assert.assertEquals("Buzz", fb.convert(5));
    }

    @Test
    public void FizzBuzzThreeAndFiveNumbers() {

        Auction fb = new Auction();
        Assert.assertEquals("Buzz", fb.convert(5));
    }
}