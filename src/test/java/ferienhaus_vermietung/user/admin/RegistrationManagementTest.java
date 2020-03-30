package ferienhaus_vermietung.user.admin;

import ferienhaus_vermietung.user.UserTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class RegistrationManagementTest extends UserTest {

	@Autowired
	RegistrationManagement registrationManagement;

	@Autowired
	AdminController adminController;

	@Autowired
	UserAccountManager userAccountManager;

	@Test
	void createEventmanagerTest() {
		registrationManagement.createEventmanager(form1);
		assertTrue(registrationManagement.getRegistrations().containsKey(form1));
		assertEquals("ROLE_EVENTMANAGER", registrationManagement.getRegistrations().get(form1));

		adminController.registerNew(form1);
		assertFalse(registrationManagement.getRegistrations().containsKey(form1));
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).isPresent());
		assertTrue(userAccountManager.findByUsername(form1.getEmail()).get().isEnabled());

	}

	@Test
	void createLandlordTest() {
		registrationManagement.createLandlord(form2);
		assertTrue(registrationManagement.getRegistrations().containsKey(form2));
		assertEquals("ROLE_LANDLORD", registrationManagement.getRegistrations().get(form2));

		adminController.registerNew(form2);
		assertFalse(registrationManagement.getRegistrations().containsKey(form2));
		assertTrue(userAccountManager.findByUsername(form2.getEmail()).isPresent());
		assertTrue(userAccountManager.findByUsername(form2.getEmail()).get().isEnabled());
	}

	@Test
	void removeRegistrationTest() {
		registrationManagement.createEventmanager(form1);
		assertTrue(registrationManagement.getRegistrations().containsKey(form1));
		assertEquals("ROLE_EVENTMANAGER", registrationManagement.getRegistrations().get(form1));
		registrationManagement.createLandlord(form2);
		assertTrue(registrationManagement.getRegistrations().containsKey(form2));
		assertEquals("ROLE_LANDLORD", registrationManagement.getRegistrations().get(form2));

		registrationManagement.removeRegistration("");
		assertTrue(registrationManagement.getRegistrations().containsKey(form1));
		assertTrue(registrationManagement.getRegistrations().containsKey(form2));

		adminController.delete(form1);
		assertFalse(registrationManagement.getRegistrations().containsKey(form1));
		assertTrue(registrationManagement.getRegistrations().containsKey(form2));

		adminController.delete(form2);
		assertFalse(registrationManagement.getRegistrations().containsKey(form2));

		registrationManagement.removeRegistration("");
		assertFalse(registrationManagement.getRegistrations().containsKey(form1));
		assertFalse(registrationManagement.getRegistrations().containsKey(form2));
	}

	@Test
	void findUsernameTest() {
		registrationManagement.createEventmanager(form1);
		assertTrue(registrationManagement.findUsername(form1.getEmail()));
		registrationManagement.createLandlord(form2);
		assertTrue(registrationManagement.findUsername(form2.getEmail()));

		assertFalse(registrationManagement.findUsername(""));

		registrationManagement.removeRegistration("");
		assertTrue(registrationManagement.findUsername(form1.getEmail()));
		assertTrue(registrationManagement.findUsername(form2.getEmail()));

		adminController.delete(form1);
		assertFalse(registrationManagement.findUsername(form1.getEmail()));
		assertTrue(registrationManagement.findUsername(form2.getEmail()));

		adminController.delete(form2);
		assertFalse(registrationManagement.findUsername(form1.getEmail()));
		assertFalse(registrationManagement.findUsername(form2.getEmail()));
	}
}