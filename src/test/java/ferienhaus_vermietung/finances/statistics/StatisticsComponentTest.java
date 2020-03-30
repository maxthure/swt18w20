package ferienhaus_vermietung.finances.statistics;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.Status;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.salespointframework.order.OrderLine;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class StatisticsComponentTest {

	@Autowired
	UserAccountManager userAccountManager;
	@Autowired
	CatalogHouses catalogHouses;
	@Autowired
	StatisticsComponent statisticsComponent;

	@Test
	void getEventFactsOfBookingTest() {
		Event e = new Event("TestEvent", 100, "TestPlace", "TestDescription", LocalDate.now(),
				LocalTime.now(), "TestCreator");
		House house = Mockito.mock(House.class);
		UserAccount userAccount = userAccountManager.create("test","test",Role.of("Test"));
		Booking b = new Booking(Status.PAID,userAccount,Cash.CASH,LocalDate.now(),LocalDate.now(),house);
		OrderLine ol1 = Mockito.mock(OrderLine.class);
		OrderLine ol2 = Mockito.mock(OrderLine.class);
		b.add(ol1);
		b.add(ol2);
		Mockito.when(ol1.getProductIdentifier()).thenReturn(e.getId());
		Mockito.when(ol2.getProductIdentifier()).thenReturn(e.getId());
		Mockito.when(ol1.getQuantity()).thenReturn(Quantity.of(2));
		Mockito.when(ol2.getQuantity()).thenReturn(Quantity.of(3));
		Mockito.when(ol1.getPrice()).thenReturn(e.getPrice().multiply(2));
		Mockito.when(ol2.getPrice()).thenReturn(e.getPrice().multiply(3));

		assertEquals(statisticsComponent.getEventFactsOfBooking(e,b).getFirst(), Quantity.of(5));
		assertEquals(statisticsComponent.getEventFactsOfBooking(e,b).getSecond(), Money.of(500, EURO));


	}
}