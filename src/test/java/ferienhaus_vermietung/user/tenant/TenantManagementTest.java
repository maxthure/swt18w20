package ferienhaus_vermietung.user.tenant;

import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.BookingRepository;
import ferienhaus_vermietung.order.OrderComponent;
import ferienhaus_vermietung.order.Status;
import ferienhaus_vermietung.user.UserTest;
import org.junit.jupiter.api.*;
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
class TenantManagementTest extends UserTest {

	@Autowired
	TenantManagement tenantManagement;

	@Autowired
	TenantRepository tenantRepository;

	@Autowired
	TenantController tenantController;

	@Autowired
	UserAccountManager userAccountManager;

	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	CatalogHouses catalogHouses;

	@Autowired
	OrderComponent orderComponent;

	@Test
	void createTenantTest() {
		Errors errors = Mockito.mock(Errors.class);
		Mockito.when(errors.hasErrors()).thenReturn(true);
		assertEquals("register", tenantController.registerNew(form1, errors, model));

		Mockito.when(errors.hasErrors()).thenReturn(false);
		assertEquals("redirect:/", tenantController.registerNew(form1, errors, model));
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());

		assertEquals("register",tenantController.registerNew(form1, errors, model));
	}

	@Test
	void deleteTenantTest() {
		tenantManagement.createTenant(form1);
		tenantController.delete(form1.getEmail());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertFalse(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());
	}

	@Test
	void deleteActiveTenant() {
		tenantManagement.createTenant(form1);
		House house = catalogHouses.save(new House("HalloHaus", form1.getFirstname(), form1.getEmail(), 3,new String[] {form1.getStreet(), form1.getPostcode(), form1.getCity()}, "schön", "",10,1,30, new Features()));
		Booking booking = bookingRepository.save(new Booking(Status.OPEN, userAccountManager.findByUsername(form1.getEmail()).get(), Cash.CASH, LocalDate.of(2018,12,1), LocalDate.of(2018,12,30), house));
		orderComponent.addBookingToHouse(booking);
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());
		assertTrue(bookingRepository.findById(booking.getId()).isPresent());
		assertEquals(Status.OPEN ,bookingRepository.findById(booking.getId()).get().getStatus());

		tenantManagement.deleteTenant(form1.getEmail());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertFalse(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());
		assertTrue(bookingRepository.findById(booking.getId()).isPresent());
		assertEquals(Status.CANCELLED ,bookingRepository.findById(booking.getId()).get().getStatus());
	}

	@Test
	void findAllTest() {
		//Ein UserAccount ex., einer wird abgefragt
		Tenant tenant1 = tenantManagement.createTenant(form1);
		ArrayList<Tenant> temp = new ArrayList<>();
		temp.add(tenant1);
		assertTrue(testFindAll(temp));

		//Zwei UserAccounts ex., einer wird abgefragt (true)
		Tenant tenant2 = tenantManagement.createTenant(form2);
		temp = new ArrayList<>();
		temp.add(tenant1);
		assertFalse(testFindAll(temp));
		temp = new ArrayList<>();
		temp.add(tenant2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts ex., zwei werden abgefragt
		temp = new ArrayList<>();
		temp.add(tenant1);
		temp.add(tenant2);
		assertTrue(testFindAll(temp));

		//Ein UserAccount wird gelöscht, zwei werden abgefragt
		tenantManagement.deleteTenant(tenant1.getUserAccount().getUsername());
		temp = new ArrayList<>();
		temp.add(tenant1);
		temp.add(tenant2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts ex., einer wurde gelöscht, einer wird abgefragt (true)
		temp = new ArrayList<>();
		temp.add(tenant2);
		assertTrue(testFindAll(temp));

		//Zwei UserAccounts ex., einer wurde gelöscht, einer wird abgefragt (false)
		temp = new ArrayList<>();
		temp.add(tenant1);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts wurden gelöscht, einer wird abgefragt
		tenantManagement.deleteTenant(tenant2.getUserAccount().getUsername());
		temp = new ArrayList<>();
		temp.add(tenant1);
		assertFalse(testFindAll(temp));
		temp = new ArrayList<>();
		temp.add(tenant2);
		assertFalse(testFindAll(temp));

		//Zwei UserAccounts wurden gelöscht, keiner wird abgefragt
		assertTrue(testFindAll(new ArrayList<Tenant>()));

	}

	private boolean testFindAll(ArrayList<Tenant> tenants){
		boolean temp = true;
		for(Tenant tenant : tenantManagement.findAll()){
			if(!tenants.contains(tenant)){
				temp = false;
			}
			else {
				tenants.remove(tenant);
			}
		}
		return temp && tenants.isEmpty();
	}
}