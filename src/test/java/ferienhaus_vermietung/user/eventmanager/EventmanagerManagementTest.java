package ferienhaus_vermietung.user.eventmanager;


import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.*;
import ferienhaus_vermietung.user.UserTest;
import ferienhaus_vermietung.user.admin.RegistrationManagement;
import ferienhaus_vermietung.user.landlord.LandlordManagement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.salespointframework.order.Cart;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class EventmanagerManagementTest extends UserTest {

	@Autowired
	EventmanagerManagement eventmanagerManagement;

	@Autowired
	EventmanagerRepository eventmanagerRepository;

	@Autowired
	EventmanagerController eventmanagerController;

	@Autowired
	UserAccountManager userAccountManager;

	@Autowired
	EventCatalog eventCatalog;

	@Autowired
	CatalogHouses catalogHouses;

	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	BookingManager bookingManager;

	@Autowired
	LandlordManagement landlordManagement;

	@Autowired
	OrderComponent orderComponent;

	@Autowired
	RegistrationManagement registrationManagement;

	@Test
	void createEventmanagerTest() {
		Errors errors = Mockito.mock(Errors.class);

		Mockito.when(errors.hasErrors()).thenReturn(true);
		assertEquals("register", eventmanagerController.registerNew(form1, errors, model));

		Mockito.when(errors.hasErrors()).thenReturn(false);
		assertEquals("redirect:/", eventmanagerController.registerNew(form1, errors, model));
		assertTrue(registrationManagement.getRegistrations().containsKey(form1));

		assertEquals("register",eventmanagerController.registerNew(form1, errors, model));
		registrationManagement.removeRegistration(form1.getEmail());

		eventmanagerManagement.createEventmanager(form1);
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());

		assertEquals("register",eventmanagerController.registerNew(form1, errors, model));
	}

	@Test
	void deleteEventmanagerTest() {
		eventmanagerManagement.createEventmanager(form1);
		eventmanagerController.delete(form1.getEmail());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertFalse(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());
	}

	@Test
	void deleteActiveEventmanagerTest() {
		eventmanagerManagement.createEventmanager(form1);
		landlordManagement.createLandlord(form2);

		Event event1 = eventCatalog.save(new Event("Event1",10,"hier","schön", LocalDate.of(2018,12,24), LocalTime.NOON, form1.getEmail()));
		Event event2 = eventCatalog.save(new Event("Event2","monday","hier","schön", LocalTime.NOON, form1.getEmail()));

		House house = catalogHouses.save(new House("HalloHaus", form2.getFirstname(), form2.getEmail(), 3,new String[] {form2.getStreet(), form2.getPostcode(), form2.getCity()}, "schön", "",10,1,30, new Features()));

		house.addConfirmedEvent(event1);
		house.addRequestedEvent(event2);

		Booking booking = bookingRepository.save(new Booking(Status.OPEN, userAccountManager.findByUsername(form2.getEmail()).get(), Cash.CASH, LocalDate.of(2018,12,1), LocalDate.of(2018,12,30), house));

		Cart cart = new Cart();
		cart.addOrUpdateItem(house, Quantity.of(1));
		cart.addOrUpdateItem(event1,Quantity.of(2));
		cart.addItemsTo(booking);

		bookingManager.setOpen(booking);

		orderComponent.addBookingToHouse(booking);

		cart.clear();

		boolean temp = false;
		for (Event e : booking.getOrderLines().filter(orderLine -> eventCatalog.findById(orderLine.getProductIdentifier()).isPresent()).map(orderLine -> eventCatalog.findById(orderLine.getProductIdentifier()).get())){
			temp = true;
		}
		assertTrue(temp);
		assertTrue(eventCatalog.existsById(event1.getId()));
		assertTrue(eventCatalog.existsById(event2.getId()));
		assertTrue(house.getConfirmedEvents().contains(event1));
		assertTrue(house.getRequestedEvents().contains(event2));

		eventmanagerManagement.deleteEventmanager(form1.getEmail());

		temp = false;
		for (Event e : booking.getOrderLines().filter(orderLine -> orderLine.getClass().equals(Event.class)).map(orderLine -> eventCatalog.findById(orderLine.getProductIdentifier()).orElseThrow(() -> new NullPointerException("Event not found")))){
			temp = true;
		}
		assertFalse(temp);
		assertFalse(eventCatalog.existsById(event1.getId()));
		assertFalse(eventCatalog.existsById(event2.getId()));
		assertFalse(house.getConfirmedEvents().contains(event1));
		assertFalse(house.getRequestedEvents().contains(event2));

	}

	@Test
	void findAllTest() {
		//Ein UserAccount ex., einer wird abgefragt
		Eventmanager eventmanager1 = eventmanagerManagement.createEventmanager(form1);
		ArrayList<Eventmanager> temp = new ArrayList<>();
		temp.add(eventmanager1);
		assertTrue(testFindAll(temp));

		//Zwei UserAccounts ex., einer wird abgefragt (true)
		Eventmanager eventmanager2 = eventmanagerManagement.createEventmanager(form3);
		temp = new ArrayList<>();
		temp.add(eventmanager1);
		assertFalse(testFindAll(temp));
		temp = new ArrayList<>();
		temp.add(eventmanager2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts ex., zwei werden abgefragt
		temp = new ArrayList<>();
		temp.add(eventmanager1);
		temp.add(eventmanager2);
		assertTrue(testFindAll(temp));

		//Ein UserAccount wird gelöscht, zwei werden abgefragt
		eventmanagerManagement.deleteEventmanager(eventmanager1.getUserAccount().getUsername());
		temp = new ArrayList<>();
		temp.add(eventmanager1);
		temp.add(eventmanager2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts ex., einer wurde gelöscht, einer wird abgefragt (true)
		temp = new ArrayList<>();
		temp.add(eventmanager2);
		assertTrue(testFindAll(temp));

		//Zwei UserAccounts ex., einer wurde gelöscht, einer wird abgefragt (false)
		temp = new ArrayList<>();
		temp.add(eventmanager1);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts wurden gelöscht, einer wird abgefragt
		eventmanagerManagement.deleteEventmanager(eventmanager2.getUserAccount().getUsername());
		temp = new ArrayList<>();
		temp.add(eventmanager1);
		assertFalse(testFindAll(temp));
		temp = new ArrayList<>();
		temp.add(eventmanager2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts wurden gelöscht, keiner wird abgefragt
		assertTrue(testFindAll(new ArrayList<>()));
	}

	private boolean testFindAll(ArrayList<Eventmanager> eventmanagers){
		boolean temp = true;
		for(Eventmanager eventmanager : eventmanagerManagement.findAll()){
			if(!eventmanagers.contains(eventmanager)){
				temp = false;
			}
			else {
				eventmanagers.remove(eventmanager);
			}
		}
		return temp && eventmanagers.isEmpty();
	}
}