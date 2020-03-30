package ferienhaus_vermietung.order;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.salespointframework.order.Cart;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class CalenderComponentTest {

	@Autowired
	CalenderComponent calenderComponent;

	@Autowired
	EventCatalog eventCatalog;

	@BeforeEach
	void setUp() {
	}

	@Test
	void writeTest() {
		House house = new House("HalloHaus", "None", "None", 3,new String[] {"-", "-", "-"}, "schön", "",10,1,30, new Features());
		Event event1 = eventCatalog.save(new Event("Event1",10,"hier","schön", LocalDate.of(2018,12,24), LocalTime.NOON, "None"));
		Booking booking = new Booking(Status.OPEN, new UserAccount(), Cash.CASH, LocalDate.of(2018,12,1), LocalDate.of(2018,12,30), house);

		Cart cart = new Cart();
		cart.addOrUpdateItem(event1, Quantity.of(1));

		cart.addItemsTo(booking);

		File file = new File("./src/main/webapp/calendarEntries");
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		long lM = file.lastModified();
		int fS1 = 0;
		for (File f : file.listFiles()) {
			fS1++;
		}

		calenderComponent.write(booking);

		int fS2 = 0;
		for (File f : file.listFiles()) {
			fS2++;
		}

		assertTrue(lM < file.lastModified());
		assertTrue(fS1 < fS2);

	}
}