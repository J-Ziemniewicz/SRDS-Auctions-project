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