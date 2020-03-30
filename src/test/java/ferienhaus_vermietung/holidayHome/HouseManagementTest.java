package ferienhaus_vermietung.holidayHome;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.OrderComponent;
import ferienhaus_vermietung.order.Status;
import org.apache.tomcat.jni.Local;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;
import static org.salespointframework.core.Currencies.EURO;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class HouseManagementTest {
	@Autowired
	UserAccountManager userAccountManager;

	@Autowired
	BusinessTime businessTime;

	@Autowired
	CatalogHouses catalogHouses;

	@Autowired
	EventCatalog eventCatalog;

	@Autowired
	OrderComponent orderComponent;

	@Autowired
	OrderManager orderManager;

	@Autowired
	HouseManagement houseManagement;

	UserAccount userAccount;
	House house;
	protected HouseForm houseForm = Mockito.mock(HouseForm.class);
	protected HouseEditForm houseEditForm = Mockito.mock(HouseEditForm.class);
	protected AvailabilityForm availabilityForm = Mockito.mock(AvailabilityForm.class);

	@BeforeEach

	void setUp() {
		Features features = new Features();
		features.put(Features.Feature.BABY_CRIB,true);
		features.put(Features.Feature.TELEVISION,true);

		businessTime.reset();
		if (!userAccountManager.findByUsername("Test").isPresent()) {
			userAccount = userAccountManager.create("Test", "test", Role.of("ROLE_TEST"));
			house = catalogHouses.save(new House("TestHaus", "TestLandlord", "TestLandlord", 3, new String[]{"Am Königsgarten 5", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features));
		} else {
			userAccount = userAccountManager.findByUsername("Test").get();
			house = catalogHouses.findByName("TestHaus").get().findFirst().get();
		}
		catalogHouses.save(house);

	}

	@Test
	void createHouseTest() {
		Mockito.when(houseForm.getNameHouse()).thenReturn("TestHausNeu");
		Mockito.when(houseForm.getBeds()).thenReturn(4);
		Mockito.when(houseForm.getDescription()).thenReturn("Blau");
		Mockito.when(houseForm.getStreet()).thenReturn("Hohe Straße 5");
		Mockito.when(houseForm.getPostcode()).thenReturn("01119");
		Mockito.when(houseForm.getCity()).thenReturn("Dresden");
		Mockito.when(houseForm.getRent()).thenReturn(20.00);
		Mockito.when(houseForm.getMinStay()).thenReturn(2);
		Mockito.when(houseForm.getMaxStay()).thenReturn(7);
		Mockito.when(houseForm.getImage()).thenReturn("");
		Mockito.when(houseForm.getKitchen()).thenReturn(true);
		Mockito.when(houseForm.getAc()).thenReturn(false);
		Mockito.when(houseForm.getWashingMachine()).thenReturn(false);
		Mockito.when(houseForm.getTumbleDryer()).thenReturn(false);
		Mockito.when(houseForm.getWifi()).thenReturn(true);
		Mockito.when(houseForm.getBabyCrib()).thenReturn(false);
		Mockito.when(houseForm.getTelevision()).thenReturn(false);

		houseManagement.createHouse(houseForm, "", userAccount);

		assertTrue(catalogHouses.findByName("TestHausNeu").get().findFirst().isPresent());
	}

	@Test
	void editHouseTest() {
		Mockito.when(houseEditForm.getHouseID()).thenReturn(house.getId());

		Mockito.when(houseEditForm.getNameHouse()).thenReturn("HausNeu");
		Mockito.when(houseEditForm.getBeds()).thenReturn(6);
		Mockito.when(houseEditForm.getDescription()).thenReturn("blau");
		Mockito.when(houseEditForm.getStreet()).thenReturn("Am Königsgarten 20");
		Mockito.when(houseEditForm.getPostcode()).thenReturn("01183");
		Mockito.when(houseEditForm.getCity()).thenReturn("Dresden");
		Mockito.when(houseEditForm.getRent()).thenReturn(20.50);
		Mockito.when(houseEditForm.getMinStay()).thenReturn(1);
		Mockito.when(houseEditForm.getMaxStay()).thenReturn(8);
		Mockito.when(houseEditForm.getImage()).thenReturn("");
		Mockito.when(houseEditForm.getKitchen()).thenReturn(false);
		Mockito.when(houseEditForm.getAc()).thenReturn(false);
		Mockito.when(houseEditForm.getWashingMachine()).thenReturn(false);
		Mockito.when(houseEditForm.getTumbleDryer()).thenReturn(false);
		Mockito.when(houseEditForm.getWifi()).thenReturn(false);
		Mockito.when(houseEditForm.getBabyCrib()).thenReturn(true);
		Mockito.when(houseEditForm.getTelevision()).thenReturn(true);

		Features features = new Features();
		features.put(Features.Feature.BABY_CRIB,true);
		features.put(Features.Feature.TELEVISION,true);

		houseManagement.editHouse(houseEditForm, "", house.getId());
		House compareHouse = catalogHouses.save(new House("HausNeu", "TestLandlord", "TestLandlord", 6, new String[]{"Am Königsgarten 20", "01183", "Dresden"}, "blau", "", 20.50, 1, 8, features));

		assertEquals(house.getNameHouse(), compareHouse.getNameHouse());
		assertEquals(house.getNameLandlord(), compareHouse.getNameLandlord());
		assertEquals(house.getUserNameLandlord(), compareHouse.getUserNameLandlord());
		assertEquals(house.getBeds(), compareHouse.getBeds());
		assertEquals(Arrays.toString(house.getAddress()), Arrays.toString(compareHouse.getAddress()));
		assertEquals(house.getDescription(), compareHouse.getDescription());
		assertEquals(house.getImage(), compareHouse.getImage());
		assertEquals(house.getPrice(), compareHouse.getPrice());
		assertEquals(house.getMinStay(), compareHouse.getMinStay());
		assertEquals(house.getMaxStay(), compareHouse.getMaxStay());
		assertEquals(house.getFeatures(), compareHouse.getFeatures());


	}

	@Test
	void confirmEventTest() {

		Event event = eventCatalog.save(new Event("New Years Eve", 10, "Elbe", "Happy New Year", LocalDate.of(2018, 12, 31), LocalTime.of(22, 0), ""));
		houseManagement.confirmEvent(event, house);
		assertTrue(house.getConfirmedEvents().contains(event));
	}

	@Test
	void rejectEventTest() {

		Event event = eventCatalog.save(new Event("New Years Eve", 10, "Elbe", "Happy New Year", LocalDate.of(2018, 12, 31), LocalTime.of(22, 0), ""));
		houseManagement.rejectEvent(event, house);
		assertFalse(house.getConfirmedEvents().contains(event));
	}

	@Test
	void removeEventTest() {

		Event event = eventCatalog.save(new Event("New Years Eve", 10, "Elbe", "Happy New Year", LocalDate.of(2018, 12, 31), LocalTime.of(22, 0), ""));
		houseManagement.confirmEvent(event, house);
		houseManagement.removeEvent(event, house);
		assertFalse(house.getConfirmedEvents().contains(event));
	}

	@Test
	void findAllTest() {
	}

	@Test
	void findByHouseNameTest() {
	}

	@Test
	void availabilitiesTest() {


		//number of beds, features, number of days okay
		Mockito.when(availabilityForm.getDayStart()).thenReturn(11);
		Mockito.when(availabilityForm.getMonthStart()).thenReturn(6);
		Mockito.when(availabilityForm.getYearStart()).thenReturn(2019);
		Mockito.when(availabilityForm.getDayEnd()).thenReturn(13);
		Mockito.when(availabilityForm.getMonthEnd()).thenReturn(6);
		Mockito.when(availabilityForm.getYearEnd()).thenReturn(2019);
		Mockito.when(availabilityForm.getKitchenSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getKitchenSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getAcSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getWashingMachineSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getTumbleDryerSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getWifiSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getBabyCribSearch()).thenReturn(true);
		Mockito.when(availabilityForm.getTelevisionSearch()).thenReturn(true);
		Mockito.when(availabilityForm.getGuests()).thenReturn(2);
		ArrayList<HashSet<House>> houses = houseManagement.availabilities(availabilityForm);
		assertTrue(houses.get(0).contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		houses.clear();

		//number guests does not equal number beds
		Mockito.when(availabilityForm.getGuests()).thenReturn(10);
		houses = houseManagement.availabilities(availabilityForm);
		assertFalse(houses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		houses.clear();

		//number days does not equal number maxStay
		Mockito.when(availabilityForm.getGuests()).thenReturn(2);
		Mockito.when(availabilityForm.getDayStart()).thenReturn(11);
		Mockito.when(availabilityForm.getMonthStart()).thenReturn(6);
		Mockito.when(availabilityForm.getYearStart()).thenReturn(2019);
		Mockito.when(availabilityForm.getDayEnd()).thenReturn(13);
		Mockito.when(availabilityForm.getMonthEnd()).thenReturn(7);
		Mockito.when(availabilityForm.getYearEnd()).thenReturn(2019);
		houses = houseManagement.availabilities(availabilityForm);
		assertFalse(houses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		houses.clear();

		//required features do not equal features of house
		Mockito.when(availabilityForm.getDayStart()).thenReturn(11);
		Mockito.when(availabilityForm.getMonthStart()).thenReturn(6);
		Mockito.when(availabilityForm.getYearStart()).thenReturn(2019);
		Mockito.when(availabilityForm.getDayEnd()).thenReturn(13);
		Mockito.when(availabilityForm.getMonthEnd()).thenReturn(6);
		Mockito.when(availabilityForm.getYearEnd()).thenReturn(2019);
		Mockito.when(availabilityForm.getTelevisionSearch()).thenReturn(false);
		houses = houseManagement.availabilities(availabilityForm);
		assertFalse(houses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		houses.clear();

		//number guests does not equal number beds & number days does not equal number maxStay
		Mockito.when(availabilityForm.getGuests()).thenReturn(2);
		Mockito.when(availabilityForm.getDayStart()).thenReturn(11);
		Mockito.when(availabilityForm.getMonthStart()).thenReturn(6);
		Mockito.when(availabilityForm.getYearStart()).thenReturn(2019);
		Mockito.when(availabilityForm.getDayEnd()).thenReturn(13);
		Mockito.when(availabilityForm.getMonthEnd()).thenReturn(7);
		Mockito.when(availabilityForm.getYearEnd()).thenReturn(2019);
		Mockito.when(availabilityForm.getTelevisionSearch()).thenReturn(true);
		Mockito.when(availabilityForm.getGuests()).thenReturn(10);
		houses = houseManagement.availabilities(availabilityForm);
		assertFalse(houses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		houses.clear();

		//number days does not equal number maxStay & required features do not equal features of house
		Mockito.when(availabilityForm.getTelevisionSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getGuests()).thenReturn(2);
		houses = houseManagement.availabilities(availabilityForm);
		assertFalse(houses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		houses.clear();

		//required features does not equal features of house & number guests does not equal number beds
		Mockito.when(availabilityForm.getDayStart()).thenReturn(11);
		Mockito.when(availabilityForm.getMonthStart()).thenReturn(6);
		Mockito.when(availabilityForm.getYearStart()).thenReturn(2019);
		Mockito.when(availabilityForm.getDayEnd()).thenReturn(13);
		Mockito.when(availabilityForm.getMonthEnd()).thenReturn(6);
		Mockito.when(availabilityForm.getYearEnd()).thenReturn(2019);
		Mockito.when(availabilityForm.getTelevisionSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getGuests()).thenReturn(10);
		houses = houseManagement.availabilities(availabilityForm);
		assertFalse(houses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		houses.clear();

		//number days does not equal number maxStay & required features does not equal features of house & number guests does not equal number beds
		Mockito.when(availabilityForm.getGuests()).thenReturn(2);
		Mockito.when(availabilityForm.getDayStart()).thenReturn(11);
		Mockito.when(availabilityForm.getMonthStart()).thenReturn(6);
		Mockito.when(availabilityForm.getYearStart()).thenReturn(2019);
		Mockito.when(availabilityForm.getDayEnd()).thenReturn(13);
		Mockito.when(availabilityForm.getMonthEnd()).thenReturn(7);
		Mockito.when(availabilityForm.getYearEnd()).thenReturn(2019);
		Mockito.when(availabilityForm.getTelevisionSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getGuests()).thenReturn(10);
		houses = houseManagement.availabilities(availabilityForm);
		assertFalse(houses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		houses.clear();

	}

	@Test
	void testIfAvailableTest() {
		Features featuresSuccess = new Features();
		featuresSuccess.put(Features.Feature.BABY_CRIB,true);
		featuresSuccess.put(Features.Feature.TELEVISION,true);

		ArrayList<House> houses = getCollection(3, featuresSuccess, 2);
		Booking booking = new Booking(Status.OPEN, userAccountManager.findByUsername("Test").get(), Cash.CASH, LocalDate.of(2019, 6, 9), LocalDate.of(2019, 6, 13), catalogHouses.findByName("TestHaus").get().findFirst().get());
		orderManager.save(booking);
		orderComponent.addBookingToHouse(booking);

		//Booking from 2019-06-09 Until 2019-06-13
		LocalDate dateStartSucessAfter = LocalDate.of(2019, 6, 15);
		LocalDate dateEndSuccessAfter = LocalDate.of(2019, 6, 20);
		LocalDate dateStartSucessBefore = LocalDate.of(2019, 5, 15);
		LocalDate dateEndSuccessBefore = LocalDate.of(2019, 5, 20);

		LocalDate dateStartFailBefore = LocalDate.of(2019, 6, 8);
		LocalDate dateEndFailBefore_After = LocalDate.of(2019, 6, 12);
		LocalDate dateEndFailAfter = LocalDate.of(2019, 6, 19);
		LocalDate dateStartFailBetween = LocalDate.of(2019, 6, 10);
		LocalDate dateEndFailBetween = LocalDate.of(2019, 6, 12);


		//Traveldates after booking
		HashSet<House> availableHouses = houseManagement.testIfAvailable(houses, dateStartSucessAfter, dateEndSuccessAfter);
		assertTrue(availableHouses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		availableHouses.clear();

		//Traveldates before booking
		availableHouses = houseManagement.testIfAvailable(houses, dateStartSucessBefore, dateEndSuccessBefore);
		assertTrue(availableHouses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		availableHouses.clear();

		//Traveldates start before booking but end in booking period
		availableHouses = houseManagement.testIfAvailable(houses, dateStartFailBefore, dateEndFailBefore_After);
		assertFalse(availableHouses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		availableHouses.clear();

		//Traveldates before in booking period but end after booking dates
		availableHouses = houseManagement.testIfAvailable(houses, dateEndFailBefore_After, dateEndFailAfter);
		assertFalse(availableHouses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		availableHouses.clear();

		//Traveldates in between booking period
		availableHouses = houseManagement.testIfAvailable(houses, dateStartFailBetween, dateEndFailBetween);
		assertFalse(availableHouses.contains(catalogHouses.findByName("TestHaus").get().findFirst().get()));
		availableHouses.clear();

	}

	@Test
	void deleteAllHousesOfLandlordTest() {
		Booking booking1 = new Booking(Status.COMPLETED, userAccountManager.findByUsername("Test").get(), Cash.CASH, LocalDate.of(2019, 6, 9), LocalDate.of(2019, 6, 13), catalogHouses.findByName("TestHaus").get().findFirst().get());
		Booking booking2 = new Booking(Status.CANCELLEDLATE, userAccountManager.findByUsername("Test").get(), Cash.CASH, LocalDate.of(2019, 1, 9), LocalDate.of(2019, 1, 13), catalogHouses.findByName("TestHaus").get().findFirst().get());
		Booking booking3 = new Booking(Status.CANCELLED, userAccountManager.findByUsername("Test").get(), Cash.CASH, LocalDate.of(2019, 7, 9), LocalDate.of(2019, 7, 13), catalogHouses.findByName("TestHaus").get().findFirst().get());
		orderManager.save(booking1);
		orderComponent.addBookingToHouse(booking1);
		orderManager.save(booking2);
		orderComponent.addBookingToHouse(booking2);
		orderManager.save(booking3);
		orderComponent.addBookingToHouse(booking3);

		Features features2 = new Features();
		features2.put(Features.Feature.AC,true);
		catalogHouses.save(new House("TestHaus2", "TestLandlord", "TestLandlord", 7, new String[]{"Kohlenstraße", "01189", "Dresden"}, "Neu", "", 100, 1, 4, features2));

		//TODO how do I find Landlord
		//houseManagement.deleteAllHousesOfLandlord(userAccountManager.findByUsername("TestLandlord").get());


	}

	public ArrayList<House> getCollection(long days, Features features, int guests) {

		Iterable<House> houses = catalogHouses.findAll();
		ArrayList<House> houseCollection = new ArrayList<>();

		for (House h : houses) {
			if (days >= h.getMinStay() && days <= h.getMaxStay()
					&& h.getFeatures().satisfies(features)
					&& (guests <= h.getBeds())) {
				houseCollection.add(h);
			}
		}
		return houseCollection;
	}

}
