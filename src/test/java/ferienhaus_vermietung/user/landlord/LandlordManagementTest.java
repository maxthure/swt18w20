package ferienhaus_vermietung.user.landlord;

import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.*;
import ferienhaus_vermietung.user.UserTest;
import ferienhaus_vermietung.user.admin.RegistrationManagement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.salespointframework.payment.Cash;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class LandlordManagementTest extends UserTest {

	@Autowired
	LandlordManagement landlordManagement;

	@Autowired
	LandlordRepository landlordRepository;

	@Autowired
	LandlordController landlordController;

	@Autowired
	UserAccountManager userAccountManager;

	@Autowired
	CatalogHouses catalogHouses;

	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	OrderComponent orderComponent;

	@Autowired
	RegistrationManagement registrationManagement;

	@Test
	void createLandlordTest() {
		Errors errors = Mockito.mock(Errors.class);
		Mockito.when(errors.hasErrors()).thenReturn(true);
		assertEquals("register", landlordController.registerNew(form2, errors, model));

		Mockito.when(errors.hasErrors()).thenReturn(false);
		assertEquals("redirect:/", landlordController.registerNew(form2, errors, model));
		assertTrue(registrationManagement.getRegistrations().containsKey(form2));

		assertEquals("register",landlordController.registerNew(form2, errors, model));
		registrationManagement.removeRegistration(form2.getEmail());

		landlordManagement.createLandlord(form1);
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());

		assertEquals("register",landlordController.registerNew(form1, errors, model));
	}

	@Test
	void deleteLandlordTest() {
		landlordManagement.createLandlord(form1);
		landlordController.delete(form1.getEmail());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertFalse(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());
	}

	@Test
	void deleteActiveLandlordTest() {
		landlordManagement.createLandlord(form1);
		House house = catalogHouses.save(new House("HalloHaus", form1.getFirstname(), form1.getEmail(), 3,new String[] {form1.getStreet(), form1.getPostcode(), form1.getCity()}, "schön", "",10,1,30, new Features()));
		Booking booking = bookingRepository.save(new Booking(Status.OPEN, userAccountManager.findByUsername(form1.getEmail()).get(), Cash.CASH, LocalDate.of(2018,12,1), LocalDate.of(2018,12,30), house));
		orderComponent.addBookingToHouse(booking);
		assertTrue(catalogHouses.findById(house.getId()).isPresent());
		assertTrue(house.getBookingPlan().contains(booking));
		assertEquals(Status.OPEN,booking.getStatus());

		landlordManagement.deleteLandlord(form1.getEmail());
		assertFalse(catalogHouses.findById(house.getId()).isPresent());
		assertTrue(house.getBookingPlan().contains(booking));
		assertEquals(Status.CANCELLED, booking.getStatus());
	}

	@Test
	void findAllTest() {
		//Ein UserAccount ex., einer wird abgefragt
		Landlord landlord1 = landlordManagement.createLandlord(form1);
		ArrayList<Landlord> temp = new ArrayList<>();
		temp.add(landlord1);
		assertTrue(testFindAll(temp));

		//Zwei UserAccounts ex., einer wird abgefragt (true)
		Landlord landlord2 = landlordManagement.createLandlord(form2);
		temp = new ArrayList<>();
		temp.add(landlord1);
		assertFalse(testFindAll(temp));
		temp = new ArrayList<>();
		temp.add(landlord2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts ex., zwei werden abgefragt
		temp = new ArrayList<>();
		temp.add(landlord1);
		temp.add(landlord2);
		assertTrue(testFindAll(temp));

		//Ein UserAccount wird gelöscht, zwei werden abgefragt
		landlordManagement.deleteLandlord(landlord1.getUserAccount().getUsername());
		temp = new ArrayList<>();
		temp.add(landlord1);
		temp.add(landlord2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts ex., einer wurde gelöscht, einer wird abgefragt (true)
		temp = new ArrayList<>();
		temp.add(landlord2);
		assertTrue(testFindAll(temp));

		//Zwei UserAccounts ex., einer wurde gelöscht, einer wird abgefragt (false)
		temp = new ArrayList<>();
		temp.add(landlord1);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts wurden gelöscht, einer wird abgefragt
		landlordManagement.deleteLandlord(landlord2.getUserAccount().getUsername());
		temp = new ArrayList<>();
		temp.add(landlord1);
		assertFalse(testFindAll(temp));
		temp = new ArrayList<>();
		temp.add(landlord2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts wurden gelöscht, keiner wird abgefragt
		assertTrue(testFindAll(new ArrayList<>()));
	}

	private boolean testFindAll(ArrayList<Landlord> landlords){
		boolean temp = true;
		for(Landlord landlord : landlordManagement.findAll()){
			if(!landlords.contains(landlord)){
				temp = false;
			}
			else {
				landlords.remove(landlord);
			}
		}
		return temp && landlords.isEmpty();
	}
}