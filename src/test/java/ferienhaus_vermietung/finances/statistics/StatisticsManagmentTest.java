package ferienhaus_vermietung.finances.statistics;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.event.EventManagement;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.BookingManager;
import ferienhaus_vermietung.order.Status;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.OrderLine;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class StatisticsManagmentTest {

	@Autowired
	CatalogHouses catalogHouses;
	@Autowired
	UserAccountManager userAccountManager;
	@Autowired
	StatisticsManagment statisticsManagment;
	@Autowired
	BookingManager bookingManager;
	@Autowired
	EventCatalog eventCatalog;

	@Test
	void getBookingsTest() {
		Features features1 = new Features();
		features1.put(Features.Feature.BABY_CRIB,true);
		features1.put(Features.Feature.TELEVISION,true);
		House house1 = catalogHouses.save(new House("TestHaus1", "TestLandlord", "TestLandlord", 3, new String[]{"Am Königsgarten 5", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features1));
		Features features2 = new Features();
		features2.put(Features.Feature.BABY_CRIB,true);
		features2.put(Features.Feature.TELEVISION,true);
		House house2 = catalogHouses.save(new House("TestHaus2", "TestLandlord", "TestLandlord", 3, new String[]{"Am Königsgarten 6", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features2));

		UserAccount userAccount = userAccountManager.create("test","test",Role.of("Test"));

		Booking b1 = bookingManager.save(new Booking(Status.BOOKING, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house1));
		Booking b2 = bookingManager.save(new Booking(Status.PAID, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house1));
		Booking b3 = bookingManager.save(new Booking(Status.BOOKING, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house1));
		Booking b4 = bookingManager.save(new Booking(Status.PAID, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house2));
		Booking b5 = bookingManager.save(new Booking(Status.COMPLETED, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house2));
		Booking b6 = bookingManager.save(new Booking(Status.BOOKING, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house2));

		house1.getBookingPlan().add(b1);
		house1.getBookingPlan().add(b2);
		house1.getBookingPlan().add(b3);
		house2.getBookingPlan().add(b4);
		house2.getBookingPlan().add(b5);
		house2.getBookingPlan().add(b6);

		catalogHouses.save(house1);
		catalogHouses.save(house2);

		ArrayList<Booking> bookingsBooking = new ArrayList<>();

		bookingsBooking.add(b1);
		bookingsBooking.add(b3);
		bookingsBooking.add(b6);

		ArrayList<Booking> bookingsPaid = new ArrayList<>();

		bookingsPaid.add(b2);
		bookingsPaid.add(b4);

		ArrayList<Booking> bookingsCompleted = new ArrayList<>();

		bookingsCompleted.add(b5);

		ArrayList<Booking> bookingsCancelledLate = new ArrayList<>();

		b1.setPaid(Money.of(110, EURO));
		b2.setPaid(Money.of(120, EURO));
		b3.setPaid(Money.of(130, EURO));
		b4.setPaid(Money.of(140, EURO));
		b5.setPaid(Money.of(150, EURO));
		b6.setPaid(Money.of(160, EURO));

		UserAccount landlordAccount = userAccountManager.create("TestLandlord", "TestLandlord", Role.of("ROLE_LANDLORD"));
		Optional<UserAccount> landlord = Optional.of(landlordAccount);

		//Testing getBookings for different OrderStatus:
		assertEquals(statisticsManagment.getBookings(landlord,Status.BOOKING).size(), bookingsBooking.size());				// 3
		assertEquals(statisticsManagment.getBookings(landlord,Status.PAID).size(), bookingsPaid.size());					// 2
		assertEquals(statisticsManagment.getBookings(landlord,Status.COMPLETED).size(), bookingsCompleted.size());			// 1
		assertEquals(statisticsManagment.getBookings(landlord,Status.CANCELLEDLATE).size(), bookingsCancelledLate.size());	// 0
	}

	@Test
	void getTotalTest() {
		Features features1 = new Features();
		features1.put(Features.Feature.BABY_CRIB,true);
		features1.put(Features.Feature.TELEVISION,true);
		House house1 = catalogHouses.save(new House("TestHaus1", "TestLandlord", "TestLandlord", 3, new String[]{"Am Königsgarten 5", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features1));
		Features features2 = new Features();
		features2.put(Features.Feature.BABY_CRIB,true);
		features2.put(Features.Feature.TELEVISION,true);
		House house2 = catalogHouses.save(new House("TestHaus2", "TestLandlord", "TestLandlord", 3, new String[]{"Am Königsgarten 6", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features2));

		UserAccount userAccount = userAccountManager.create("test","test",Role.of("Test"));

		Booking b1 = bookingManager.save(new Booking(Status.BOOKING, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house1));
		Booking b2 = bookingManager.save(new Booking(Status.PAID, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house1));
		Booking b3 = bookingManager.save(new Booking(Status.BOOKING, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house1));
		Booking b4 = bookingManager.save(new Booking(Status.PAID, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house2));
		Booking b5 = bookingManager.save(new Booking(Status.COMPLETED, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house2));
		Booking b6 = bookingManager.save(new Booking(Status.BOOKING, userAccount, Cash.CASH, LocalDate.now(), LocalDate.now(), house2));

		house1.getBookingPlan().add(b1);
		house1.getBookingPlan().add(b2);
		house1.getBookingPlan().add(b3);
		house2.getBookingPlan().add(b4);
		house2.getBookingPlan().add(b5);
		house2.getBookingPlan().add(b6);

		catalogHouses.save(house1);
		catalogHouses.save(house2);

		ArrayList<Booking> bookingsBooking = new ArrayList<>();

		bookingsBooking.add(b1);
		bookingsBooking.add(b3);
		bookingsBooking.add(b6);

		ArrayList<Booking> bookingsPaid = new ArrayList<>();

		bookingsPaid.add(b2);
		bookingsPaid.add(b4);

		ArrayList<Booking> bookingsCompleted = new ArrayList<>();

		bookingsCompleted.add(b5);

		ArrayList<Booking> bookingsCancelledLate = new ArrayList<>();

		b1.setPaid(Money.of(110, EURO));
		b2.setPaid(Money.of(120, EURO));
		b3.setPaid(Money.of(130, EURO));
		b4.setPaid(Money.of(140, EURO));
		b5.setPaid(Money.of(150, EURO));
		b6.setPaid(Money.of(160, EURO));

		UserAccount landlordAccount = userAccountManager.create("TestLandlord", "TestLandlord", Role.of("ROLE_LANDLORD"));
		Optional<UserAccount> landlord = Optional.of(landlordAccount);

		//Testing getTotal for different bookingsArrayLists
		assertEquals(statisticsManagment.getTotal(statisticsManagment.getBookings(landlord,Status.BOOKING)), Money.of(400, EURO)); 		// b1 + b3 + b6 = 110 + 130 + 160=400
		assertEquals(statisticsManagment.getTotal(statisticsManagment.getBookings(landlord,Status.PAID)), Money.of(260, EURO));			// b2 + b4 = 120 + 140 = 260
		assertEquals(statisticsManagment.getTotal(statisticsManagment.getBookings(landlord,Status.COMPLETED)), Money.of(150, EURO));	// b5 = 150
		assertEquals(statisticsManagment.getTotal(statisticsManagment.getBookings(landlord,Status.CANCELLEDLATE)), Money.of(0, EURO));	// = 0
	}

	@Test
	void getUniqueMapTest() {
		House house = Mockito.mock(House.class);
		UserAccount userAccount = userAccountManager.create("test","test",Role.of("Test"));
		Booking b1 = bookingManager.save(new Booking(Status.PAID, userAccount, Cash.CASH, LocalDate.now(),LocalDate.now(), house));
		Booking b2 = bookingManager.save(new Booking(Status.COMPLETED, userAccount, Cash.CASH, LocalDate.now(),LocalDate.now(), house));
		Event e1 = eventCatalog.save(new Event("TestEvent1", 100, "TestPlace1", "TestDescription1", LocalDate.now(),
				LocalTime.now(), "TestCreator1"));
		Event e2 = eventCatalog.save(new Event("TestEvent2", 200, "TestPlace2", "TestDescription2", LocalDate.now(),
				LocalTime.now(), "TestCreator2"));
		OrderLine ol1 = Mockito.mock(OrderLine.class);
		OrderLine ol2 = Mockito.mock(OrderLine.class);
		b1.add(ol1);
		b2.add(ol2);
		Mockito.when(ol1.getProductIdentifier()).thenReturn(e1.getId());
		Mockito.when(ol2.getProductIdentifier()).thenReturn(e2.getId());
		Mockito.when(ol1.getQuantity()).thenReturn(Quantity.of(2));
		Mockito.when(ol2.getQuantity()).thenReturn(Quantity.of(3));
		Mockito.when(ol1.getPrice()).thenReturn(e1.getPrice().multiply(2));
		Mockito.when(ol2.getPrice()).thenReturn(e2.getPrice().multiply(3));
/*
		for(Event e: statisticsManagment.getUniqueMap().keySet()){
			assertEquals(statisticsManagment.getUniqueMap().get(e).getFirst(), Quantity.of(2));
			assertEquals(statisticsManagment.getUniqueMap().get(e).getSecond(), Quantity.of(3));
		}
*/

	}

	@Test
	void getUniqueTotalTest() {
		Map<Event, org.springframework.data.util.Pair<Quantity, MonetaryAmount>> map = new HashMap<>();
		Event e1=Mockito.mock(Event.class);
		Event e2=Mockito.mock(Event.class);
		Event e3=Mockito.mock(Event.class);
		Pair<Quantity, MonetaryAmount> p1 = Pair.of(Quantity.of(1), Money.of(10, EURO));
		Pair<Quantity, MonetaryAmount> p2 = Pair.of(Quantity.of(1), Money.of(15, EURO));
		Pair<Quantity, MonetaryAmount> p3 = Pair.of(Quantity.of(1), Money.of(20, EURO));
		map.put(e1,p1);
		map.put(e2,p2);
		map.put(e3,p3);


		assertEquals(statisticsManagment.getUniqueTotal(map), Money.of(45, EURO));
	}

	@Test
	void findAllByLandlordTest() {
		Features features = new Features();
		features.put(Features.Feature.BABY_CRIB,true);
		features.put(Features.Feature.TELEVISION,true);
		House house1 = catalogHouses.save(new House("TestHaus1", "TestLandlord1", "TestLandlord1", 3, new String[]{"Am Königsgarten 5", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features));
		House house2 = catalogHouses.save(new House("TestHaus2", "TestLandlord1", "TestLandlord1", 3, new String[]{"Am Königsgarten 6", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features));
		House house3 = catalogHouses.save(new House("TestHaus3", "TestLandlord2", "TestLandlord2", 3, new String[]{"Am Königsgarten 7", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features));
		catalogHouses.save(house1);
		catalogHouses.save(house2);
		catalogHouses.save(house3);
		UserAccount landlordAccount1 = userAccountManager.create("TestLandlord1", "TestLandlord1", Role.of("ROLE_LANDLORD"));
		Optional<UserAccount> landlord1 = Optional.of(landlordAccount1);
		UserAccount landlordAccount2 = userAccountManager.create("TestLandlord2", "TestLandlord2", Role.of("ROLE_LANDLORD"));
		Optional<UserAccount> landlord2 = Optional.of(landlordAccount2);

		//Testing findAllByLandlord with different users
		assertEquals(((List<House>) statisticsManagment.findAllByLandlord(landlordAccount1)).size(), 2);
		assertEquals(((List<House>) statisticsManagment.findAllByLandlord(landlordAccount2)).size(), 1);
	}
}