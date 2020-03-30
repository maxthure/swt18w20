package ferienhaus_vermietung.user;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

public class UserTest {

	protected RegistrationForm form1 = Mockito.mock(RegistrationForm.class);
	protected RegistrationForm form2 = Mockito.mock(RegistrationForm.class);
	protected RegistrationForm form3 = Mockito.mock(RegistrationForm.class);
	protected Model model = new ExtendedModelMap();

	@BeforeEach
	void setUp() {
		Mockito.when(form1.getFirstname()).thenReturn("Firstname");
		Mockito.when(form1.getLastname()).thenReturn("Lastname");
		Mockito.when(form1.getEmail()).thenReturn("beispiel@example.com");
		Mockito.when(form1.getPassword()).thenReturn("pw");
		Mockito.when(form1.getStreet()).thenReturn("Beispielstr. 17");
		Mockito.when(form1.getPostcode()).thenReturn("00000");
		Mockito.when(form1.getCity()).thenReturn("Dresden");
		Mockito.when(form1.getCountry()).thenReturn("germany");
		Mockito.when(form1.getCompany()).thenReturn("Eentim");
		Mockito.when(form2.getFirstname()).thenReturn("Firstname");
		Mockito.when(form2.getLastname()).thenReturn("Lastname");
		Mockito.when(form2.getEmail()).thenReturn("beispiel2@example.com");
		Mockito.when(form2.getPassword()).thenReturn("pw");
		Mockito.when(form2.getStreet()).thenReturn("Beispielstr. 17");
		Mockito.when(form2.getPostcode()).thenReturn("00000");
		Mockito.when(form2.getCity()).thenReturn("Dresden");
		Mockito.when(form2.getCountry()).thenReturn("germany");
		Mockito.when(form1.getFirstname()).thenReturn("Firstname");
		Mockito.when(form3.getLastname()).thenReturn("Lastname");
		Mockito.when(form3.getEmail()).thenReturn("beispiel3@example.com");
		Mockito.when(form3.getPassword()).thenReturn("pw");
		Mockito.when(form3.getStreet()).thenReturn("Beispielstr. 17");
		Mockito.when(form3.getPostcode()).thenReturn("00000");
		Mockito.when(form3.getCity()).thenReturn("Dresden");
		Mockito.when(form3.getCountry()).thenReturn("germany");
		Mockito.when(form3.getCompany()).thenReturn("Eentim");
	}

}
