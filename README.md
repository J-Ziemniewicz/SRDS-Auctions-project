# SRDS-Auctions-project
Projekt przygotowany na laboratoria z przedmiotu Systemy Rozproszone Dużej Skali.

## Konfiguracja projektu
Projekt wlaczyc przy pomocy biblioteki ccm.
``` bash
ccm create auction_cluster -v 3.11.0
ccm populate -n 3
ccm start
ccm node1 cqlsh -f Auction-project/Schema/create_schema.cql
```


## Problem

Analizowany przez nas problem dotyczy licytacji produktów na aukcji. Jak wiemy, na aukcjach jest pełno osób, które często 
chcą zlicytować jakiś produkt. Serwer musi obsłużyć wszystkie żądania licytacji (lub kupienia produktu) tak by 
żadne podniesienie ceny nie zostało pominięte.

## Schemat
``` CQL 
CREATE TABLE Auctions (
    product_id UUID,
    buy_out_price int,
    current_price int,
    auction_end time,
    is_sold boolean,
    buyer_id bigint,
    PRIMARY KEY (product_id)
);
```


Uznaliśmy, iż do rozwiązania tego problemu wystarczy jedna tabela przechowująca produkty i informacje dotyczące ceny, 
czasu końca aukcji, kupującego, oraz tego, czy przedmiot jest już sprzedany. Dla uprostrzenia nie tworzyliśmy tabeli 
dla użytkowników (osób licytujących).

## Wnioski

Osoba licytująca może zdecydować czy podbija cenę dla danego produktu, czy też kupuję go za cenę wykupu.
Każda z tych operacji zwraca wartość prawda/fałsz, która określa czy udało się wykupić, lub podnieść cenę produktu.
Naszym celem jest sprawienie, by ceny produktów zawsze rosły, czy produkt przypadkiem nie został już sprzedany, 
oraz czy aukcja nie została zakończona.



