package ferienhaus_vermietung.finances.statistics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class StatisticsControllerTest {

	@Autowired
	StatisticsController statisticsController;
	@Autowired
	UserAccountManager userAccountManager;

	@Test
	@WithMockUser(authorities = "ROLE_LANDLORD")
	void statisticsLandlordTest() {

		UserAccount landlordAccount = userAccountManager.create("TestLandlord", "TestLandlord", Role.of("ROLE_LANDLORD"));
		Optional<UserAccount> landlord = Optional.of(landlordAccount);

		Model model = new ExtendedModelMap();

		assertThat(statisticsController.statisticsLandlord(model, landlord)).isEqualTo("statisticsLandlord");


	}

	@Test
	@WithMockUser(authorities = "ROLE_EVENTMANAGER")
	void statisticsEventmanagerTest() {

		UserAccount eventmanagerAccount = userAccountManager.create("TestEventmanager", "TestEventmanager", Role.of("ROLE_EVENTMANAGER"));
		Optional<UserAccount> eventmanager = Optional.of(eventmanagerAccount);

		Model model = new ExtendedModelMap();

		assertThat(statisticsController.statisticsEventmanager(model, eventmanager)).isEqualTo("statisticsEventmanager");
	}
}