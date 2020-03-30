package ferienhaus_vermietung.order;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import org.salespointframework.order.OrderLine;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

@Component
@Transactional
public class OrderComponent {

	private final CatalogHouses houses;
	private final BookingManager bookingManager;
	private final EventCatalog eventCatalog;
	private final BusinessTime businessTime;

	private final int week = 7;
	private final int month = 30;

	/**
	 * @param houses must not be {@literal null}.
	 * @param bookingManager must not be {@literal null}.
	 * @param eventCatalog must not be {@literal null}.
	 */
	OrderComponent(CatalogHouses houses, BookingManager bookingManager, EventCatalog eventCatalog,
				   BusinessTime businessTime){

		Assert.notNull(houses, "Houses must not be null!");
		Assert.notNull(bookingManager, "BookingManager must not be null!");
		Assert.notNull(eventCatalog, "EventCatalog must not be null!");

		this.houses = houses;
		this.bookingManager = bookingManager;
		this.eventCatalog = eventCatalog;
		this.businessTime = businessTime;
	}

	public void addBookingToHouse(Booking booking){
		booking.getHouse().getBookingPlan().add(booking);
		houses.save(booking.getHouse());
	}


	private void removeBookingFromHouse(Booking booking){
		booking.getHouse().getBookingPlan().remove(booking);
		houses.save(booking.getHouse());
	}

	public void deleteBooking(Booking booking){
		//removeBookingFromHouse(booking);
		if(!(booking.getStatus().equals(Status.CANCELLED) || booking.getStatus().equals(Status.CANCELLEDLATE)
				|| booking.getStatus().equals(Status.COMPLETED))) {
			bookingManager.cancelBooking(booking);
		}
	}

	//Nur zu Test- und Vorführzwecken
	//@Scheduled(cron = "* * * * * *")
	@Scheduled(cron = "0 0 12 * * *")
	public void dueDates(){
		//businessTime.forward(Duration.of(1,ChronoUnit.DAYS));
		//System.out.println(businessTime.getTime().toLocalDate());
		Streamable<Booking> openBookings = bookingManager.findBy(Status.OPEN);
		for(Booking b : openBookings){
			if(businessTime.getTime().toLocalDate().until(b.getFirstDate(), ChronoUnit.DAYS) < week){
				//System.out.println(b.getStatus());
				bookingManager.cancelLateBooking(b);
			}
			if(b.getDateCreated().until(businessTime.getTime(), ChronoUnit.DAYS) > week){
				//System.out.println(b.getStatus());
				bookingManager.cancelBooking(b);
			}
		}
		Streamable<Booking> reservationBookings = bookingManager.findBy(Status.RESERVATION);
		for(Booking b : reservationBookings){
			if(b.getDateCreated().until(businessTime.getTime(), ChronoUnit.DAYS) > month){
				//System.out.println(b.getStatus());
				bookingManager.cancelBooking(b);
			}
		}
		Streamable<Booking> bookedBookings = bookingManager.findBy(Status.BOOKING);
		for(Booking b : bookedBookings){
			if(businessTime.getTime().toLocalDate().until(b.getFirstDate(), ChronoUnit.DAYS) <= 0){
				//System.out.println(b.getStatus());
				bookingManager.cancelLateBooking(b);
			}
		}
		Streamable<Booking> paidBookings = bookingManager.findBy(Status.PAID);
		for(Booking b : paidBookings){
			if(businessTime.getTime().toLocalDate().until(b.getFirstDate(), ChronoUnit.DAYS) <= 0){
				//System.out.println(b.getStatus());
				bookingManager.completeBooking(b);
			}
		}

		Streamable<Booking> wishListBookings = bookingManager.findBy(Status.WISHLIST)
				.and(bookingManager.findBy(Status.WISHLISTPOSSIBLE));
		for(Booking b : wishListBookings){
			if(businessTime.getTime().toLocalDate().until(b.getFirstDate(), ChronoUnit.DAYS) <= 0){
				//System.out.println(b.getStatus());
				deleteBooking(b);
				bookingManager.delete(b);
			}
		}
	}

	public ArrayList<Event> getUniqueEvents(Booking booking){
		ArrayList<Event> temp = new ArrayList<>();
		for (OrderLine ol: booking.getOrderLines()) {
			if(eventCatalog.findById(ol.getProductIdentifier()).isPresent()
					&& eventCatalog.findById(ol.getProductIdentifier()).get().getType().equals(Event.EventType.UNIQUE)){
				temp.add(eventCatalog.findById(ol.getProductIdentifier()).get());
			}
		}
		return temp;
	}

	public HashMap<LocalDate,ArrayList<Event>> getWeeklyEvents(Booking booking){
		LocalDate date = booking.getFirstDate();
		HashMap<LocalDate,ArrayList<Event>> temp = new HashMap<>();

		while(date.isBefore(booking.getLastDate())){
			ArrayList<Event> events = new ArrayList<>();
			for (Event e: booking.getHouse().getConfirmedEvents()) {
				if((e.getType().equals(Event.EventType.WEEKLY)) && (date.getDayOfWeek().toString().toLowerCase()
						.equals(e.getDay()))){
					events.add(e);
				}
			}
			temp.put(date,events);
			date = date.plusDays(1);
		}
		return temp;
	}

}
