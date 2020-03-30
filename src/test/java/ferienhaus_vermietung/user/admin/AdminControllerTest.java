package ferienhaus_vermietung.user.admin;

import ferienhaus_vermietung.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import static org.assertj.core.api.Assertions.*;

class AdminControllerTest extends AbstractIntegrationTests {

	@Autowired
	AdminController adminController;

	@Test
	void rejectsUnauthenticatedAccessToAdminController() {

		assertThatExceptionOfType(AuthenticationException.class) //
				.isThrownBy(() -> adminController.user(new ExtendedModelMap()));
	}

	@Test
	@WithMockUser(authorities = "ROLE_ADMIN")
	void allowsAuthenticatedAccessToAdminController() {

		ExtendedModelMap model = new ExtendedModelMap();

		adminController.user(model);

		assertThat(model.get("usersList")).isNotNull();
	}

}