package ferienhaus_vermietung.order;

import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.Booking;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.salespointframework.order.Cart;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class BookingManagerTest {
	@Autowired
	CatalogHouses catalogHouses;

	@Autowired
	BookingManager bookingManager;

	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	UserAccountManager userAccountManager;

	private House house1;

	private Booking b1;
	private Booking b2;
	private Booking b3;
	private Booking b4;
	private Booking b5;

	@BeforeEach
	void setUp() {
		UserAccount userAccount = userAccountManager.create("Test","test", Role.of("ROLE_TEST"));
		House house = catalogHouses.save(new House("TestHaus", "TestLandlord", "Tester", 3, new String[]{"Am Königsgarten 5","01193","Dresden"}, "Schön", "",10,1,15,new Features()));
		b1 = bookingRepository.save(new Booking(Status.RESERVATION, userAccount, Cash.CASH, LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), house));
		b2 = bookingRepository.save(new Booking(Status.OPEN, userAccount, Cash.CASH, LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), house));
		b3 = bookingRepository.save(new Booking(Status.BOOKING, userAccount, Cash.CASH, LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), house));
		b4 = bookingRepository.save(new Booking(Status.PAID, userAccount, Cash.CASH, LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), house));
		b5 = bookingRepository.save(new Booking(Status.COMPLETED, userAccount, Cash.CASH, LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), house));
		house1 = catalogHouses.save(new House("house1", "Carol", "Landlord", 2, new String[]{"123 Fake Street","01234","Springfield"}, "Lorem ipsum 1", "", 20.00, 3, 8, new Features()));
		Cart cart1 = new Cart();

		for (Booking b: bookingRepository.findAll()) {
			cart1.addOrUpdateItem(house1,b.getRange());
			cart1.addOrUpdateItem(house1,b.getRange());

		}

	}

	@Test
	void setReserved() {
		for (Booking b: bookingRepository.findAll()) {
			if(b.getStatus().equals(Status.RESERVATION)) {
				assertTrue(bookingManager.setReserved(b));
				assertEquals(Money.of(0, EURO),b1.getPaid());
			} else {
				assertFalse(bookingManager.setReserved(b));
			}
		}
	}

	@Test
	void setOpen() {
		for (Booking b: bookingRepository.findAll()) {
			if(b.getStatus().equals(Status.OPEN) || b.getStatus().equals(Status.RESERVATION)) {
				assertTrue(bookingManager.setOpen(b));
				assertEquals(Money.of(0, EURO),b.getPaid());
			} else {
				assertFalse(bookingManager.setOpen(b));
			}
		}
	}

	@Test
	void payDeposit() {
		for (Booking b: bookingRepository.findAll()) {
			if(b.getStatus().equals(Status.OPEN) || b.getStatus().equals(Status.RESERVATION)) {
				assertTrue(bookingManager.payDeposit(b));
				assertEquals(b.getTotalPrice().multiply(0.1), b.getPaid());
			} else {
				assertFalse(bookingManager.payDeposit(b));
			}
		}
	}

	@Test
	void payBooking() {
		for (Booking b: bookingRepository.findAll()) {
			if(b.getStatus() == Status.RESERVATION || b.getStatus() == Status.OPEN || b.getStatus() == Status.BOOKING) {
				assertTrue(bookingManager.payBooking(b));
				assertEquals(b.getTotalPrice(), b.getPaid());
			} else {
				assertFalse(bookingManager.payBooking(b));
			}
		}
	}

	@Test
	void completeBooking() {
		for (Booking b: bookingRepository.findAll()) {
			if(b.getStatus() == Status.PAID) {
				assertTrue(bookingManager.completeBooking(b));
			} else {
				assertFalse(bookingManager.completeBooking(b));
			}
		}
	}

	@Test
	void cancelBooking() {
		for (Booking b: bookingRepository.findAll()) {
			if(b.getStatus() != Status.CANCELLED || b.getStatus() != Status.CANCELLEDLATE || b.getStatus() != Status.COMPLETED ) {
				assertTrue(bookingManager.cancelBooking(b));

			} else {
				assertFalse(bookingManager.cancelBooking(b));
			}
		}
	}

	@Test
	void cancelLateBooking() {
		for (Booking b: bookingRepository.findAll()) {
			if(b.getStatus() != Status.CANCELLED || b.getStatus() != Status.CANCELLEDLATE || b.getStatus() != Status.COMPLETED) {
				assertTrue(bookingManager.cancelLateBooking(b));
			} else {
				assertFalse(bookingManager.cancelLateBooking(b));
			}
		}
	}
}