package ferienhaus_vermietung.finances.statistics;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.order.Booking;
import org.javamoney.moneta.Money;
import org.salespointframework.order.OrderLine;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;

import static org.salespointframework.core.Currencies.EURO;

@Component
public class StatisticsComponent {

	public Pair<Quantity, MonetaryAmount> getEventFactsOfBooking(Event e, Booking b){
		Quantity visitors = Quantity.of(0);
		MonetaryAmount monetaryAmount = Money.of(0,EURO);

		for (OrderLine ol : b.getOrderLines()) {
			if (ol.getProductIdentifier().equals(e.getId())) {
				visitors = visitors.add(ol.getQuantity());
				monetaryAmount = monetaryAmount.add(ol.getPrice());
			}
		}
		return Pair.of(visitors,monetaryAmount);
	}


}
