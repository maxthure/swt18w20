package ferienhaus_vermietung.user;

import javax.validation.constraints.NotEmpty;

public interface RegistrationForm {

	@NotEmpty(message = "{RegistrationForm.firstname.NotEmpty}") //
	String getFirstname();

	@NotEmpty(message = "{RegistrationForm.lastname.NotEmpty}") //
	String getLastname();

	@NotEmpty(message = "{RegistrationForm.email.NotEmpty}") //
	String getEmail();

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}") //
	String getPassword();

	@NotEmpty(message = "{RegistrationForm.street.NotEmpty}") //
	String getStreet();

	@NotEmpty(message = "{RegistrationForm.postcode.NotEmpty}") //
	String getPostcode();

	@NotEmpty(message = "{RegistrationForm.city.NotEmpty}") //
	String getCity();

	@NotEmpty(message = "{RegistrationForm.country.NotEmpty}") //
	String getCountry();

	String getCompany();

}