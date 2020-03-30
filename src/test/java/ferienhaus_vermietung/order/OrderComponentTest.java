package ferienhaus_vermietung.order;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import org.junit.jupiter.api.*;
import org.salespointframework.order.Cart;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class OrderComponentTest {

	@Autowired
	UserAccountManager userAccountManager;

	@Autowired
	OrderComponent orderComponent;

	@Autowired
	BusinessTime businessTime;

	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	CatalogHouses catalogHouses;

	@Autowired
	EventCatalog eventCatalog;

	UserAccount userAccount;
	House house;
	Booking booking1;
	Booking booking2;
	Booking booking3;
	Booking booking4;
	Booking booking5;
	Booking booking6;
	Booking booking7;
	Booking booking8;
	Booking booking9;
	Booking booking10;

	@BeforeEach
	void setUp() {
		businessTime.reset();
		if(!userAccountManager.findByUsername("Test").isPresent()){
			userAccount = userAccountManager.create("Test","test", Role.of("ROLE_TEST"));
			house = catalogHouses.save(new House("TestHaus", "TestLandlord", "Tester", 3, new String[]{"Am Königsgarten 5","01193","Dresden"}, "Schön", "",10,1,15,new Features()));
		} else {
			userAccount = userAccountManager.findByUsername("Test").get();
			house = catalogHouses.findByName("TestHaus").get().findFirst().get();
		}
		for (Booking b : bookingRepository.findByUserAccount(userAccount)) {
			bookingRepository.delete(b);
		}
		booking1 = bookingRepository.save(new Booking(Status.OPEN, userAccount, Cash.CASH, LocalDate.now().plusDays(9), LocalDate.now().plusDays(16),house));
		booking2 = bookingRepository.save(new Booking(Status.OPEN, userAccount, Cash.CASH, LocalDate.now().plusDays(10), LocalDate.now().plusDays(17),house));
		booking3 = bookingRepository.save(new Booking(Status.OPEN, userAccount, Cash.CASH, LocalDate.now().plusDays(30), LocalDate.now().plusDays(35),house));
		booking4 = bookingRepository.save(new Booking(Status.RESERVATION, userAccount, Cash.CASH, LocalDate.now().plusDays(30), LocalDate.now().plusDays(35),house));
		booking5 = bookingRepository.save(new Booking(Status.BOOKING, userAccount, Cash.CASH, LocalDate.now().plusDays(1), LocalDate.now().plusDays(35),house));
		booking6 = bookingRepository.save(new Booking(Status.PAID, userAccount, Cash.CASH, LocalDate.now().plusDays(1), LocalDate.now().plusDays(35),house));
		booking7 = bookingRepository.save(new Booking(Status.CANCELLED, userAccount, Cash.CASH, LocalDate.now().plusDays(1), LocalDate.now().plusDays(35),house));
		booking8 = bookingRepository.save(new Booking(Status.CANCELLED, userAccount, Cash.CASH, LocalDate.now().plusDays(1), LocalDate.now().plusDays(35),house));
		booking9 = bookingRepository.save(new Booking(Status.WISHLIST, userAccount, Cash.CASH, LocalDate.now().plusDays(1), LocalDate.now().plusDays(35),house));
		booking10 = bookingRepository.save(new Booking(Status.WISHLISTPOSSIBLE, userAccount, Cash.CASH, LocalDate.now().plusDays(1), LocalDate.now().plusDays(35),house));
	}

	@Test
	void addBookingToHouseTest() {
		assertTrue(house.getBookingPlan().isEmpty());
		orderComponent.addBookingToHouse(booking1);
		assertTrue(house.getBookingPlan().contains(booking1));
		assertFalse(house.getBookingPlan().contains(booking2));
		orderComponent.addBookingToHouse(booking2);
		assertTrue(house.getBookingPlan().contains(booking1));
		assertTrue(house.getBookingPlan().contains(booking2));
		orderComponent.addBookingToHouse(booking7);
		orderComponent.addBookingToHouse(booking8);
		assertTrue(house.getBookingPlan().contains(booking1));
		assertTrue(house.getBookingPlan().contains(booking2));
		assertTrue(house.getBookingPlan().contains(booking7));
		assertTrue(house.getBookingPlan().contains(booking8));
	}

	@Test
	void deleteBookingTest() {
		orderComponent.addBookingToHouse(booking1);
		orderComponent.addBookingToHouse(booking2);
		orderComponent.deleteBooking(booking1);
		assertTrue(house.getBookingPlan().contains(booking1));
		assertTrue(house.getBookingPlan().contains(booking2));
		assertEquals(Status.CANCELLED, bookingRepository.findById(booking1.getId()).get().getStatus());
		assertEquals(Status.OPEN, bookingRepository.findById(booking2.getId()).get().getStatus());
	}

	@Test
	void dueDatesTest() {
		//Testfälle Angezahlte Buchung nicht bezahlt bis Urlaubsbeginn & Fristgerecht bezahlt & Wishlist
		assertEquals(Status.BOOKING, bookingRepository.findById(booking5.getId()).get().getStatus());
		assertEquals(Status.PAID, bookingRepository.findById(booking6.getId()).get().getStatus());
		assertEquals(Status.WISHLIST, bookingRepository.findById(booking9.getId()).get().getStatus());
		assertEquals(Status.WISHLISTPOSSIBLE, bookingRepository.findById(booking10.getId()).get().getStatus());
		orderComponent.dueDates();
		assertEquals(Status.BOOKING, bookingRepository.findById(booking5.getId()).get().getStatus());
		assertEquals(Status.PAID, bookingRepository.findById(booking6.getId()).get().getStatus());
		assertEquals(Status.WISHLIST, bookingRepository.findById(booking9.getId()).get().getStatus());
		assertEquals(Status.WISHLISTPOSSIBLE, bookingRepository.findById(booking10.getId()).get().getStatus());
		businessTime.forward(Duration.of(1, ChronoUnit.DAYS));
		orderComponent.dueDates();
		assertEquals(Status.CANCELLEDLATE, bookingRepository.findById(booking5.getId()).get().getStatus());
		assertEquals(Status.COMPLETED, bookingRepository.findById(booking6.getId()).get().getStatus());
		assertThrows(NullPointerException.class,() -> {bookingRepository.findById(booking9.getId()).orElseThrow(() -> new NullPointerException("Booking not Found"));});
		assertThrows(NullPointerException.class,() -> {bookingRepository.findById(booking10.getId()).orElseThrow(() -> new NullPointerException("Booking not Found"));});
		businessTime.reset();

		//Testfall Anzahlung nicht gezahlt, weniger als 7 Tage bis zu Beginn
		assertEquals(Status.OPEN, bookingRepository.findById(booking1.getId()).get().getStatus());
		assertEquals(Status.OPEN, bookingRepository.findById(booking2.getId()).get().getStatus());
		orderComponent.dueDates();
		businessTime.forward(Duration.of(3, ChronoUnit.DAYS));
		assertEquals(Status.OPEN, bookingRepository.findById(booking1.getId()).get().getStatus());
		assertEquals(Status.OPEN, bookingRepository.findById(booking2.getId()).get().getStatus());
		orderComponent.dueDates();
		businessTime.forward(Duration.of(1, ChronoUnit.DAYS));
		assertEquals(Status.CANCELLEDLATE, bookingRepository.findById(booking1.getId()).get().getStatus());
		assertEquals(Status.OPEN, bookingRepository.findById(booking2.getId()).get().getStatus());
		orderComponent.dueDates();
		businessTime.forward(Duration.of(1, ChronoUnit.DAYS));
		assertEquals(Status.CANCELLEDLATE, bookingRepository.findById(booking1.getId()).get().getStatus());
		assertEquals(Status.CANCELLEDLATE, bookingRepository.findById(booking2.getId()).get().getStatus());
		businessTime.reset();

		//Testfall Anzahlung mahr als 7 Tage nicht gezahlt
		assertEquals(Status.OPEN, bookingRepository.findById(booking3.getId()).get().getStatus());
		orderComponent.dueDates();
		assertEquals(Status.OPEN, bookingRepository.findById(booking3.getId()).get().getStatus());
		businessTime.forward(Duration.of(8, ChronoUnit.DAYS));
		orderComponent.dueDates();
		assertEquals(Status.CANCELLED, bookingRepository.findById(booking3.getId()).get().getStatus());
		businessTime.reset();

		//Testfall Reservierung älter als 30 Tage
		assertEquals(Status.RESERVATION, bookingRepository.findById(booking4.getId()).get().getStatus());
		orderComponent.dueDates();
		assertEquals(Status.RESERVATION, bookingRepository.findById(booking4.getId()).get().getStatus());
		businessTime.forward(Duration.of(31, ChronoUnit.DAYS));
		orderComponent.dueDates();
		assertEquals(Status.CANCELLED, bookingRepository.findById(booking4.getId()).get().getStatus());
		businessTime.reset();

	}

	@Test
	void getUniqueEventsTest() {
		Cart cart = new Cart();
		ArrayList<Event> events = new ArrayList<>();
		Event event1;
		Event event2;
		if(eventCatalog.findByName("Event1").isEmpty()){
			event1 = eventCatalog.save(new Event("Event1",10,"Örtchen","Beschreibung",LocalDate.now(),LocalTime.NOON,"Tester"));
			event2 = eventCatalog.save(new Event("Event1",20,"Örtchen","Beschreibung",LocalDate.now(),LocalTime.NOON,"Tester"));
		} else {
			for (Event e : eventCatalog.findByName("Event1")) {
				eventCatalog.delete(e);
			}
			event1 = eventCatalog.save(new Event("Event1",10,"Örtchen","Beschreibung",LocalDate.now(),LocalTime.NOON,"Tester"));
			event2 = eventCatalog.save(new Event("Event1",20,"Örtchen","Beschreibung",LocalDate.now(),LocalTime.NOON,"Tester"));
		}
		events.add(event1);
		cart.addOrUpdateItem(event1, Quantity.of(10));
		cart.addItemsTo(booking1);
		bookingRepository.save(booking1);
		//Ein Event hinzugefügt, eins wird abgefragt
		assertEquals(events, orderComponent.getUniqueEvents(bookingRepository.findById(booking1.getId()).get()));
		events.add(event2);
		//Ein Event hinzugefügt, zwei werden abgefrgat
		assertNotEquals(events, orderComponent.getUniqueEvents(bookingRepository.findById(booking1.getId()).get()));
		cart.clear();
		cart.addOrUpdateItem(event2, Quantity.of(3));
		cart.addOrUpdateItem(event1, Quantity.of(10));
		cart.addItemsTo(booking2);
		bookingRepository.save(booking2);
		boolean temp = true;
		for (Event e : events) {
			if(!orderComponent.getUniqueEvents(bookingRepository.findById(booking2.getId()).get()).contains(e)){
				temp = false;
			}
		}
		//Zwei Events hinzugefügt, zwei werden abgefragt
		assertTrue(temp);
		events.remove(event2);
		if(events.size() == orderComponent.getUniqueEvents(bookingRepository.findById(booking2.getId()).get()).size()){
			for (Event e : events) {
				if(!orderComponent.getUniqueEvents(bookingRepository.findById(booking2.getId()).get()).contains(e)){
					temp = false;
				}
			}
		} else {
			temp = false;
		}
		//Zwei Events hinzugefügt eins wird abgefragt
		assertFalse(temp);
	}

	@Test
	void getWeeklyEventsTest() {
		Cart cart = new Cart();
		ArrayList<Event> events = new ArrayList<>();
		Event event1;
		Event event2;
		if(eventCatalog.findByName("Event2").isEmpty()){
			event1 = eventCatalog.save(new Event("Event1", "monday","Örtchen","Beschreibung",LocalTime.NOON,"Tester"));
			event2 = eventCatalog.save(new Event("Event2","tuesday","Örtchen","Beschreibung",LocalTime.NOON,"Tester"));
		} else {
			for (Event e : eventCatalog.findByName("Event2")) {
				eventCatalog.delete(e);
			}
			event1 = eventCatalog.save(new Event("Event1", "monday","Örtchen","Beschreibung",LocalTime.NOON,"Tester"));
			event2 = eventCatalog.save(new Event("Event2", "tuesday","Örtchen","Beschreibung",LocalTime.NOON,"Tester"));
		}
		house.addConfirmedEvent(event1);
		events.add(event1);
		cart.addOrUpdateItem(event1, Quantity.of(10));
		cart.addItemsTo(booking1);
		bookingRepository.save(booking1);
		HashMap<LocalDate, ArrayList<Event>> map = orderComponent.getWeeklyEvents(bookingRepository.findById(booking1.getId()).get());
		//Ein Event hinzugefügt, eins wird abgefragt
		assertTrue(isWeeklyEventsCorrect(map,events));
		//Weil erstes Event gelöscht wurde bei Abfrage
		events.add(event1);
		events.add(event2);
		map = orderComponent.getWeeklyEvents(bookingRepository.findById(booking1.getId()).get());
		//Ein Event hinzugefügt, zwei werden abgefrgat
		assertFalse(isWeeklyEventsCorrect(map,events));
		//Weil erstes Event gelöscht wurde bei Abfrage
		events.add(event1);
		house.addConfirmedEvent(event2);
		cart.clear();
		cart.addOrUpdateItem(event2, Quantity.of(3));
		cart.addOrUpdateItem(event1, Quantity.of(10));
		cart.addItemsTo(booking2);
		bookingRepository.save(booking2);
		map = orderComponent.getWeeklyEvents(bookingRepository.findById(booking2.getId()).get());
		//Zwei Events hinzugefügt, zwei werden abgefragt
		assertTrue(isWeeklyEventsCorrect(map,events));
		//Es wurden beide Events gelöscht, aber es soll nur eins hinzugefügt werden
		events.add(event1);
		house.removeConfirmedEvent(event2);
		//Zwei Events hinzugefügt eins wird abgefragt
		assertFalse(isWeeklyEventsCorrect(map,events));
	}

	public boolean isWeeklyEventsCorrect(HashMap<LocalDate, ArrayList<Event>> map, ArrayList<Event> events){
		boolean temp = true;
		ArrayList<Event> events1 = new ArrayList<>();
		for (LocalDate date : map.keySet()) {
			for (Event e : map.get(date)) {
				if(!date.getDayOfWeek().toString().toLowerCase().equals(e.getDay())){
					temp = false;
					break;
				} else if (events.contains(e)){
					events1.add(e);
				} else {
					temp = false;
					break;
				}
			}
		}
		events.removeAll(events1);
		return temp && events.isEmpty();
	}
}