package ferienhaus_vermietung.user.eventmanager;

import ferienhaus_vermietung.user.UserTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
public class GetterSetterTest extends UserTest {

	@Autowired
	EventmanagerManagement eventmanagerManagement;

	@Test
	void getAdressTest(){
		Eventmanager eventmanager = eventmanagerManagement.createEventmanager(form1);
		assertEquals(form1.getStreet(), eventmanager.getAdress()[0]);
		assertEquals(form1.getPostcode(), eventmanager.getAdress()[1]);
		assertEquals(form1.getCity(), eventmanager.getAdress()[2]);
		assertEquals(form1.getCountry(), eventmanager.getAdress()[3]);
	}

	@Test
	void getRoleTest(){
		Eventmanager eventmanager = eventmanagerManagement.createEventmanager(form1);
		assertEquals("ROLE_EVENTMANAGER", eventmanager.getRole());
	}

	@Test
	void getCompanyTest(){
		Eventmanager eventmanager = eventmanagerManagement.createEventmanager(form1);
		assertEquals(form1.getCompany(), eventmanager.getCompany());
	}

}
