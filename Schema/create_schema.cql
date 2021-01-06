CREATE KEYSPACE IF NOT EXISTS Auction WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

USE Auction;

CREATE TABLE Auctions (
product_id UUID,
buy_out_price int,
starting_price int,
current_price int,
auction_end time,
is_sold boolean,
buyer_id int,
PRIMARY KEY (product_id)
);