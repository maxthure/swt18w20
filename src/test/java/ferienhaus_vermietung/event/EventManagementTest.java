package ferienhaus_vermietung.event;

import ferienhaus_vermietung.event.Event.EventType;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.BookingManager;
import ferienhaus_vermietung.order.Status;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.salespointframework.order.OrderLine;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class EventManagementTest {

	@Autowired
	EventCatalog eventCatalog;

	@Autowired
	EventManagement eventManagement;

	@Autowired
	CatalogHouses catalogHouses;

	@Autowired
	BookingManager bookingManager;

	private UniqueEventCreationForm uniqueEventCreationForm = Mockito.mock(UniqueEventCreationForm.class);
	private UniqueEventEditForm uniqueEventEditForm = Mockito.mock(UniqueEventEditForm.class);

	private WeeklyEventCreationForm weeklyEventCreationForm = Mockito.mock(WeeklyEventCreationForm.class);
	private WeeklyEventEditForm weeklyEventEditForm = Mockito.mock(WeeklyEventEditForm.class);

	private String uniqueCreatorName;
	private String weeklyCreatorName;

	private House house1;
	private House house2;

	private Event eventDummyUnique;
	private Event eventDummyWeekly;

	private UserAccount userAccount;

	@BeforeEach
	void setUp() {

		house1 = catalogHouses.save(new House("house1", "Carol", "Landlord", 2, new String[]{"123 Fake Street","01234","Springfield"}, "Lorem ipsum 1", "", 20.00, 3, 8, new Features()));
		house2 = catalogHouses.save(new House("house2", "Darrel", "Landlord", 2, new String[]{"54 Evergreen Terrace","01234","Springfield"}, "Lorem ipsum 1", "", 35.00, 1, 12, new Features()));

		eventDummyUnique = eventCatalog.save(new Event("Party", 20.50, "APB E006", "Hier geht's ab!", LocalDate.of(2019, 1, 31), LocalTime.of(22, 0), ""));
		eventDummyWeekly = eventCatalog.save(new Event("Bingo", "monday", "Domizil am Zoo", "Hütet euch!", LocalTime.of(22, 0), ""));

		house1.addRequestedEvent(eventDummyWeekly);
		house1.addConfirmedEvent(eventDummyUnique);
		house2.addRequestedEvent(eventDummyUnique);
		house2.addConfirmedEvent(eventDummyWeekly);

		Mockito.when(uniqueEventCreationForm.getName()).thenReturn("UniqueEventName");
		Mockito.when(uniqueEventCreationForm.getPrice()).thenReturn(5.99);
		Mockito.when(uniqueEventCreationForm.getPlace()).thenReturn("UniquePlace");
		Mockito.when(uniqueEventCreationForm.getDescription()).thenReturn("Unique Lorem ipsum dolor sit amet.");
		Mockito.when(uniqueEventCreationForm.getYear()).thenReturn(2019);
		Mockito.when(uniqueEventCreationForm.getMonth()).thenReturn(1);
		Mockito.when(uniqueEventCreationForm.getDay()).thenReturn(25);
		Mockito.when(uniqueEventCreationForm.getHours()).thenReturn(15);
		Mockito.when(uniqueEventCreationForm.getMinutes()).thenReturn(30);
		Mockito.when(uniqueEventCreationForm.getHouses()).thenReturn(new HashSet<House>(Arrays.asList(house1, house2)));

		uniqueCreatorName = "Alice";

		Mockito.when(weeklyEventCreationForm.getName()).thenReturn("EventName");
		Mockito.when(weeklyEventCreationForm.getPlace()).thenReturn("WeeklyPlace");
		Mockito.when(weeklyEventCreationForm.getDescription()).thenReturn("Weekly Lorem Ipsum dolor sit amet.");
		Mockito.when(weeklyEventCreationForm.getDay()).thenReturn("Monday");
		Mockito.when(weeklyEventCreationForm.getHours()).thenReturn(15);
		Mockito.when(weeklyEventCreationForm.getMinutes()).thenReturn(30);
		Mockito.when(weeklyEventCreationForm.getHouses()).thenReturn(new HashSet<House>(Arrays.asList(house1, house2)));

		weeklyCreatorName = "Bob";

		Mockito.when(uniqueEventEditForm.getName()).thenReturn("New unique name");
		Mockito.when(uniqueEventEditForm.getPrice()).thenReturn(399.99);
		Mockito.when(uniqueEventEditForm.getPlace()).thenReturn("New unique place");
		Mockito.when(uniqueEventEditForm.getDescription()).thenReturn("New Unique description");
		Mockito.when(uniqueEventEditForm.getYear()).thenReturn(2020);
		Mockito.when(uniqueEventEditForm.getMonth()).thenReturn(8);
		Mockito.when(uniqueEventEditForm.getDay()).thenReturn(15);
		Mockito.when(uniqueEventEditForm.getHours()).thenReturn(23);
		Mockito.when(uniqueEventEditForm.getMinutes()).thenReturn(45);

		Mockito.when(weeklyEventEditForm.getName()).thenReturn("New weekly name");
		Mockito.when(weeklyEventEditForm.getPlace()).thenReturn("New weekly place");
		Mockito.when(weeklyEventEditForm.getDescription()).thenReturn("New weekly description");
		Mockito.when(weeklyEventEditForm.getDay()).thenReturn("tuesday");
		Mockito.when(weeklyEventEditForm.getHours()).thenReturn(12);
		Mockito.when(weeklyEventEditForm.getMinutes()).thenReturn(15);

	}

	@Test
	void createUniqueEventTest() {
		Event uEvent = eventManagement.createUniqueEvent(uniqueEventCreationForm, uniqueCreatorName);
		assertTrue(eventCatalog.findById(uEvent.getId()).isPresent());
		assertEquals(eventCatalog.findById(uEvent.getId()).get().getType(), EventType.UNIQUE);

		for (House house : uniqueEventCreationForm.getHouses()) {
			assertTrue(house.getRequestedEvents().contains(uEvent));
		}

	}

	@Test
	void createWeeklyEventTest() {
		Event wEvent = eventManagement.createWeeklyEvent(weeklyEventCreationForm, weeklyCreatorName);
		assertTrue(eventCatalog.findById(wEvent.getId()).isPresent());
		assertEquals(eventCatalog.findById(wEvent.getId()).get().getType(), EventType.WEEKLY);

		for (House house : weeklyEventCreationForm.getHouses()) {
			assertTrue(house.getRequestedEvents().contains(wEvent));
		}
	}

	@Test
	void editUniqueEventTest() {
		eventManagement.editUniqueEvent(uniqueEventEditForm, eventDummyUnique.getId());

		assertTrue(eventCatalog.findById(eventDummyUnique.getId()).isPresent());

		assertEquals(eventDummyUnique.getName(), uniqueEventEditForm.getName());
		assertEquals(eventDummyUnique.getDescription(), uniqueEventEditForm.getDescription());
		assertEquals(eventDummyUnique.getPlace(), uniqueEventEditForm.getPlace());
		assertTrue(eventDummyUnique.getPrice().isEqualTo(Money.of(uniqueEventEditForm.getPrice(), EURO)));
		assertEquals(eventDummyUnique.getActualYear(), (int) uniqueEventEditForm.getYear());
		assertEquals(eventDummyUnique.getActualMonth(), (int) uniqueEventEditForm.getMonth());
		assertEquals(eventDummyUnique.getActualDay(), (int) uniqueEventEditForm.getDay());
		assertEquals(eventDummyUnique.getActualHours(), (int) uniqueEventEditForm.getHours());
		assertEquals(eventDummyUnique.getActualMinutes(), (int) uniqueEventEditForm.getMinutes());
	}

	@Test
	void editWeeklyEventTest() {
		eventManagement.editWeeklyEvent(weeklyEventEditForm, eventDummyWeekly.getId());

		assertTrue(eventCatalog.findById(eventDummyWeekly.getId()).isPresent());

		assertEquals(eventDummyWeekly.getName(), weeklyEventEditForm.getName());
		assertEquals(eventDummyWeekly.getDescription(), weeklyEventEditForm.getDescription());
		assertEquals(eventDummyWeekly.getDay(), weeklyEventEditForm.getDay());
		assertEquals(eventDummyWeekly.getPlace(), weeklyEventEditForm.getPlace());
		assertEquals(eventDummyWeekly.getActualHours(), (int) weeklyEventEditForm.getHours());
		assertEquals(eventDummyWeekly.getActualMinutes(), (int) weeklyEventEditForm.getMinutes());
	}

	@Test
	void deleteUniqueEventTest() {
		eventManagement.deleteEvent(eventDummyUnique);

		assertFalse(eventCatalog.existsById(eventDummyUnique.getId()));

		assertFalse(house1.getRequestedEvents().contains(eventDummyUnique) || house1.getConfirmedEvents().contains(eventDummyUnique));
		assertFalse(house2.getRequestedEvents().contains(eventDummyUnique) || house2.getConfirmedEvents().contains(eventDummyUnique));

		for (Booking b : bookingManager.findBy(Status.OPEN).and(bookingManager.findBy(Status.BOOKING)).and(bookingManager.findBy(Status.PAID))) {
			for (OrderLine ol : b.getOrderLines()) {
				assertNotEquals(eventDummyUnique.getId(), ol.getProductIdentifier());
			}
		}
	}

	@Test
	void deleteWeeklyEventTest() {
		eventManagement.deleteEvent(eventDummyWeekly);

		assertFalse(eventCatalog.existsById(eventDummyWeekly.getId()));

		assertFalse(house1.getRequestedEvents().contains(eventDummyWeekly) || house1.getConfirmedEvents().contains(eventDummyWeekly));
		assertFalse(house2.getRequestedEvents().contains(eventDummyWeekly) || house2.getConfirmedEvents().contains(eventDummyWeekly));

		Streamable<Booking> bookings = bookingManager.findBy(Status.OPEN).and(bookingManager.findBy(Status.BOOKING)).and(bookingManager.findBy(Status.PAID));

		for (Booking b : bookingManager.findBy(Status.OPEN).and(bookingManager.findBy(Status.BOOKING)).and(bookingManager.findBy(Status.PAID))) {
			for (OrderLine ol : b.getOrderLines()) {
				assertNotEquals(eventDummyWeekly.getId(), ol.getProductIdentifier());
			}
		}
	}
}