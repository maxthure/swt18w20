package ferienhaus_vermietung.user.eventmanager;

import ferienhaus_vermietung.AbstractIntegrationTests;
import ferienhaus_vermietung.user.RegistrationForm;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.*;

class EventmanagerControllerTest extends AbstractIntegrationTests {

	@Autowired
	EventmanagerController eventmanagerController;

	@Test
	void rejectsUnauthenticatedAccessToEventmanagerController() {

		assertThatExceptionOfType(AuthenticationException.class) //
				.isThrownBy(() -> eventmanagerController.eventmanager(new ExtendedModelMap()));
	}

	@Test
	@WithMockUser(authorities = "ROLE_ADMIN")
	void allowsAuthenticatedAccessToEventmanagerController() {

		ExtendedModelMap model = new ExtendedModelMap();

		eventmanagerController.eventmanager(model);

		assertThat(model.get("userList")).isNotNull();
	}

	@Test
	void registerTest(){

		Model model = new ExtendedModelMap();

		RegistrationForm form1 = Mockito.mock(RegistrationForm.class);
		Mockito.when(form1.getFirstname()).thenReturn("Firstname");
		Mockito.when(form1.getLastname()).thenReturn("Lastname");
		Mockito.when(form1.getEmail()).thenReturn("beispiel@example.com");
		Mockito.when(form1.getPassword()).thenReturn("pw");
		Mockito.when(form1.getStreet()).thenReturn("Beispielstr. 17");
		Mockito.when(form1.getPostcode()).thenReturn("00000");
		Mockito.when(form1.getCity()).thenReturn("Dresden");
		Mockito.when(form1.getCountry()).thenReturn("germany");
		Mockito.when(form1.getCompany()).thenReturn("Entmt");

		assertThat(eventmanagerController.register(model, form1)).isEqualTo("register");
	}

}