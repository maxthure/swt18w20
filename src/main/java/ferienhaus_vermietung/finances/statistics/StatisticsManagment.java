package ferienhaus_vermietung.finances.statistics;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.BookingManager;
import ferienhaus_vermietung.order.Status;
import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;
@Service
class StatisticsManagment {

	private final CatalogHouses catalogHouses;
	private final EventCatalog eventCatalog;
	private final BookingManager bookingManager;
	private final StatisticsComponent statisticsComponent;

	public StatisticsManagment(CatalogHouses catalogHouses, EventCatalog eventCatalog, BookingManager bookingManager,
							   StatisticsComponent statisticsComponent) {
		Assert.notNull(catalogHouses, "Event Catalog must not be null!");

		this.statisticsComponent = statisticsComponent;
		this.catalogHouses = catalogHouses;
		this.eventCatalog =eventCatalog;
		this.bookingManager = bookingManager;
	}

	public ArrayList<Booking> getBookings(@LoggedIn Optional< UserAccount > userAccount, Status status) {
		ArrayList<Booking> bookings = new ArrayList<>();
		for (House h : catalogHouses.findByUserNameLandlord(userAccount
				.orElseThrow(() -> new NullPointerException("UserAccount not present!")).getUsername())) {
			for(Booking b : h.getBookingPlan()){
				if(b.getStatus().equals(status)){
					bookings.add(b);
				}
			}
		}
		return bookings;
	}

	public Iterable<House> findAllByLandlord(UserAccount userAccount){
		return catalogHouses.findByUserNameLandlord(userAccount.getUsername());
	}

	public MonetaryAmount getTotal(ArrayList<Booking> bookings) {
		MonetaryAmount total = Money.of(0, EURO);
		for (Booking b: bookings) {
			total = total.add(b.getPaid());
		}
		return total;
	}

	public Map<Event, Pair<Quantity, MonetaryAmount>> getUniqueMap(UserAccount userAccount) {
		Map<Event, Pair<Quantity,MonetaryAmount>> uniqueMap=new HashMap<>();

		for (Booking b : bookingManager.findBy(Status.PAID).and(bookingManager.findBy(Status.COMPLETED))){
			for (Event e : b.getOrderLines().filter(orderLine ->
					eventCatalog.findById(orderLine.getProductIdentifier()).isPresent()).map(orderLine ->
					{ return eventCatalog.findById(orderLine.getProductIdentifier()).orElseThrow(() ->
						new NullPointerException("OrderLine not found"));
					}).filter(event ->
					event.getCreatorName().equals(userAccount.getUsername()))) {
				Quantity visitors = statisticsComponent.getEventFactsOfBooking(e,b).getFirst();
				MonetaryAmount monetaryAmount = statisticsComponent.getEventFactsOfBooking(e,b).getSecond();
				if(!uniqueMap.containsKey(e)){
					uniqueMap.put(e,Pair.of(Quantity.of(0), Money.of(0,EURO)));
				}
				uniqueMap.put(e, Pair.of(uniqueMap.get(e).getFirst().add(visitors),
						uniqueMap.get(e).getSecond().add(monetaryAmount)));
			}
		}
		return uniqueMap;
	}

	public MonetaryAmount getUniqueTotal(Map<Event, Pair<Quantity,MonetaryAmount>> uniqueMap) {
		MonetaryAmount uniqueTotal = Money.of(0, EURO);
		for (Event e : uniqueMap.keySet()) {
			uniqueTotal = uniqueTotal.add(uniqueMap.get(e).getSecond());
		}
		return uniqueTotal;
	}
}
