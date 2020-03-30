package ferienhaus_vermietung.event;

import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.holidayHome.HouseManagement;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.BookingManager;
import ferienhaus_vermietung.order.Status;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.OrderLine;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;

@Service
@Transactional
public class EventManagement {

	private final EventCatalog eventCatalog;
	private final HouseManagement houseManagement;
	private final BookingManager bookingManager;

	/**
	 * @param eventCatalog must not be {@literal null}.
	 * @param houseManagement must not be {@literal null}.
	 * @param bookingManager must not be {@literal null}.
	 */

	public EventManagement(EventCatalog eventCatalog, HouseManagement houseManagement, BookingManager bookingManager) {

		Assert.notNull(eventCatalog, "Event Catalog must not be null!");
		Assert.notNull(houseManagement, "Event Catalog must not be null!");

		this.eventCatalog = eventCatalog;
		this.houseManagement = houseManagement;
		this.bookingManager = bookingManager;
	}

	/**
	 * Creates a new unique {@link Event} using the information given in the {@link UniqueEventCreationForm}.
	 *
	 * @param uEventForm must not be {@literal null}.
	 * @param creatorName must not be {@literal null}.
	 * @return {@link Event}
	 */
	public Event createUniqueEvent(UniqueEventCreationForm uEventForm, String creatorName) {

		String name = uEventForm.getName();
		double price = uEventForm.getPrice();
		String place = uEventForm.getPlace();
		String description = uEventForm.getDescription();

		int year = uEventForm.getYear();
		int month = uEventForm.getMonth();
		int day = uEventForm.getDay();
		LocalDate date = LocalDate.of(year, month, day);

		int hours = uEventForm.getHours();
		int minutes = uEventForm.getMinutes();
		LocalTime time = LocalTime.of(hours, minutes);

		Event event = new Event(name, price, place, description, date, time, creatorName);
		Set<House> houses = uEventForm.getHouses();

		for (House h : houses) {
			h.addRequestedEvent(event);
		}

		return eventCatalog.save(event);

	}

	/**
	 * Creates a new weekly {@link Event} using the information given in the {@link WeeklyEventCreationForm}.
	 *
	 * @param wEventForm must not be {@literal null}.
	 * @param creatorName must not be {@literal null}.
	 * @return {@link Event}
	 */
	public Event createWeeklyEvent(WeeklyEventCreationForm wEventForm, String creatorName) {

		String name = wEventForm.getName();
		String day = wEventForm.getDay();
		String place = wEventForm.getPlace();
		String description = wEventForm.getDescription();

		int hours = wEventForm.getHours();
		int minutes = wEventForm.getMinutes();

		LocalTime time = LocalTime.of(hours, minutes);

		Event event = new Event(name, day, place, description, time, creatorName);

		Set<House> houses = wEventForm.getHouses();

		for (House h : houses) {
			h.addRequestedEvent(event);
		}

		return eventCatalog.save(event);

	}

	/**
	 * Editing of an unique {@link Event} and saving changes in {@param eventCatalog}.
	 *
	 * @param uniqueEventEditForm must not be {@literal null}.
	 * @param id must not be {@literal null}.
	 */
	public void editUniqueEvent(UniqueEventEditForm uniqueEventEditForm, ProductIdentifier id) {

		Optional<Event> changedEventOptional = eventCatalog.findById(id);
		Event changedEvent = changedEventOptional.orElseThrow(() -> new NullPointerException("Event not present!"));

		String name = uniqueEventEditForm.getName();
		String description = uniqueEventEditForm.getDescription();
		String place = uniqueEventEditForm.getPlace();
		Double price = uniqueEventEditForm.getPrice();
		int year = uniqueEventEditForm.getYear();
		int month = uniqueEventEditForm.getMonth();
		int day = uniqueEventEditForm.getDay();
		int hour = uniqueEventEditForm.getHours();
		int minute = uniqueEventEditForm.getMinutes();

		changedEvent.setName(name);
		changedEvent.setPrice(Money.of(price, EURO));
		changedEvent.setDescription(description);
		changedEvent.setPlace(place);

		LocalDate date_new = LocalDate.of(year, month, day);
		changedEvent.setDate(date_new);

		LocalTime time_new = LocalTime.of(hour, minute);
		changedEvent.setTime(time_new);


		eventCatalog.save(changedEvent);

	}

	/**
	 * Editing of weekly {@link Event} and saving changes in {@param catalogHouses}.
	 *
	 * @param weeklyEventEditForm must not be {@literal null}.
	 * @param id must not be {@literal null}.
	 */
	public void editWeeklyEvent(WeeklyEventEditForm weeklyEventEditForm, ProductIdentifier id) {

		Optional<Event> changedEventOptional = eventCatalog.findById(id);
		Event changedEvent = changedEventOptional.orElseThrow(() -> new NullPointerException("Event not present!"));

		String name = weeklyEventEditForm.getName();
		String description = weeklyEventEditForm.getDescription();
		String place = weeklyEventEditForm.getPlace();
		String day = weeklyEventEditForm.getDay();
		int hour = weeklyEventEditForm.getHours();
		int minute = weeklyEventEditForm.getMinutes();

		changedEvent.setName(name);
		changedEvent.setDescription(description);
		changedEvent.setPlace(place);
		changedEvent.setDay(day);

		LocalTime time_new = LocalTime.of(hour, minute);
		changedEvent.setTime(time_new);


		eventCatalog.save(changedEvent);

	}

	/**
	 * Deletion of an {@link Event} of.
	 *
	 * @param event must not be {@literal null}.
	 */
	public void deleteEvent(Event event){
		for (House house : houseManagement.findAll()) {
			houseManagement.removeEvent(eventCatalog.findById(event.getId())
					.orElseThrow(() -> new NullPointerException("Event not present!")), house);
			houseManagement.rejectEvent(eventCatalog.findById(event.getId())
					.orElseThrow(() -> new NullPointerException("Event not present!")), house);
		}
		Streamable<Booking> bookings = bookingManager.findBy(Status.OPEN).and(bookingManager.findBy(Status.BOOKING))
				.and(bookingManager.findBy(Status.PAID));
		for (Booking b : bookings ) {
			ArrayList<OrderLine> orderLines = new ArrayList<>();
			for(OrderLine ol : b.getOrderLines()){
				if(ol.getProductIdentifier().equals(event.getId())){
					orderLines.add(ol);
				}
			}
			for (OrderLine ol : orderLines){
				if(b.getStatus().equals(Status.PAID)){
					b.setPaid(b.getPaid().subtract(ol.getPrice()));
				}
				b.remove(ol);
			}
		}
		eventCatalog.delete(event);
	}

}