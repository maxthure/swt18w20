package ferienhaus_vermietung.event;

import ferienhaus_vermietung.AbstractIntegrationTests;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EventControllerTest extends AbstractIntegrationTests {

	@Autowired
	EventController eventController;

	@Autowired
	CatalogHouses houseCatalog;

	@Autowired
	EventCatalog eventCatalog;

	@Autowired
	UserAccountManager userAccountManager;

	private UniqueEventCreationForm uniqueEventCreationForm = Mockito.mock(UniqueEventCreationForm.class);
	private UniqueEventEditForm uniqueEventEditForm = Mockito.mock(UniqueEventEditForm.class);

	private WeeklyEventCreationForm weeklyEventCreationForm = Mockito.mock(WeeklyEventCreationForm.class);
	private WeeklyEventEditForm weeklyEventEditForm = Mockito.mock(WeeklyEventEditForm.class);


	@Test
	@WithMockUser(authorities = "ROLE_EVENTMANAGER")
	void ControllerIntegrationTest() {
		UserAccount eventManagerAccount = userAccountManager.create("TestEventManager", "TestEventManager", Role.of("ROLE_EVENTMANAGER"));
		eventManagerAccount.setFirstname("Max");
		eventManagerAccount.setLastname("Mustermann");
		eventManagerAccount.setEmail("max@mustermann.com");
		userAccountManager.save(eventManagerAccount);

		Optional<UserAccount> eventManager = Optional.of(eventManagerAccount);

		Event eventDummyUnique = eventCatalog.save(new Event("Party", 20.50, "APB E006", "Hier geht's ab!", LocalDate.of(2019, 1, 31), LocalTime.of(22, 0), ""));
		Event eventDummyWeekly = eventCatalog.save(new Event("Bingo", "monday", "Domizil am Zoo", "Hütet euch!", LocalTime.of(22, 0), ""));

		House house1 = houseCatalog.save(new House("house1", "Carol", "Landlord", 2, new String[]{"123 Fake Street","01234","Springfield"}, "Lorem ipsum 1", "", 20.00, 3, 8, new Features()));
		House house2 = houseCatalog.save(new House("house2", "Darrel", "Landlord", 2, new String[]{"54 Evergreen Terrace","01234","Springfield"}, "Lorem ipsum 1", "", 35.00, 1, 12, new Features()));

		Mockito.when(uniqueEventCreationForm.getName()).thenReturn("UniqueEventName");
		Mockito.when(uniqueEventCreationForm.getPrice()).thenReturn(5.99);
		Mockito.when(uniqueEventCreationForm.getPlace()).thenReturn("UniquePlace");
		Mockito.when(uniqueEventCreationForm.getDescription()).thenReturn("Unique Lorem ipsum dolor sit amet.");
		Mockito.when(uniqueEventCreationForm.getYear()).thenReturn(2019);
		Mockito.when(uniqueEventCreationForm.getMonth()).thenReturn(1);
		Mockito.when(uniqueEventCreationForm.getDay()).thenReturn(25);
		Mockito.when(uniqueEventCreationForm.getHours()).thenReturn(15);
		Mockito.when(uniqueEventCreationForm.getMinutes()).thenReturn(30);
		Mockito.when(uniqueEventCreationForm.getHouses()).thenReturn(new HashSet<House>(Arrays.asList(house1, house2)));

		Mockito.when(weeklyEventCreationForm.getName()).thenReturn("EventName");
		Mockito.when(weeklyEventCreationForm.getPlace()).thenReturn("WeeklyPlace");
		Mockito.when(weeklyEventCreationForm.getDescription()).thenReturn("Weekly Lorem Ipsum dolor sit amet.");
		Mockito.when(weeklyEventCreationForm.getDay()).thenReturn("Monday");
		Mockito.when(weeklyEventCreationForm.getHours()).thenReturn(15);
		Mockito.when(weeklyEventCreationForm.getMinutes()).thenReturn(30);
		Mockito.when(weeklyEventCreationForm.getHouses()).thenReturn(new HashSet<House>(Arrays.asList(house1, house2)));

		Mockito.when(uniqueEventEditForm.getName()).thenReturn("New unique name");
		Mockito.when(uniqueEventEditForm.getPrice()).thenReturn(399.99);
		Mockito.when(uniqueEventEditForm.getPlace()).thenReturn("New unique place");
		Mockito.when(uniqueEventEditForm.getDescription()).thenReturn("New Unique description");
		Mockito.when(uniqueEventEditForm.getYear()).thenReturn(2020);
		Mockito.when(uniqueEventEditForm.getMonth()).thenReturn(8);
		Mockito.when(uniqueEventEditForm.getDay()).thenReturn(15);
		Mockito.when(uniqueEventEditForm.getHours()).thenReturn(23);
		Mockito.when(uniqueEventEditForm.getMinutes()).thenReturn(45);

		Mockito.when(weeklyEventEditForm.getName()).thenReturn("New weekly name");
		Mockito.when(weeklyEventEditForm.getPlace()).thenReturn("New weekly place");
		Mockito.when(weeklyEventEditForm.getDescription()).thenReturn("New weekly description");
		Mockito.when(weeklyEventEditForm.getDay()).thenReturn("tuesday");
		Mockito.when(weeklyEventEditForm.getHours()).thenReturn(12);
		Mockito.when(weeklyEventEditForm.getMinutes()).thenReturn(15);

		Model model = new ExtendedModelMap();

		assertThat(eventController.events(model)).isEqualTo("events");

		assertThat(eventController.detailEventPost(eventDummyUnique)).isEqualTo("detailEvent");
		assertThat(eventController.detailEventPost(eventDummyWeekly)).isEqualTo("detailEvent");

		assertThat(eventController.detailEventGet(eventDummyUnique)).isEqualTo("detailEvent");
		assertThat(eventController.detailEventGet(eventDummyWeekly)).isEqualTo("detailEvent");

		String view1u = eventController.edits(uniqueEventEditForm, weeklyEventEditForm, model, eventDummyUnique);
		assertThat(view1u).isEqualTo("editUniqueEvent");

		String view1w = eventController.edits(uniqueEventEditForm, weeklyEventEditForm, model, eventDummyWeekly);
		assertThat(view1w).isEqualTo("editWeeklyEvent");

		assertThat(eventController.editsDoneUnique()).isEqualTo("editUniqueEvent");
		assertThat(eventController.editsDoneWeekly()).isEqualTo("editWeeklyEvent");

		assertThat(eventController.createEvent(model, uniqueEventCreationForm)).isEqualTo("uniqueEventCreation");
		assertThat(eventController.createEvent(model, weeklyEventCreationForm)).isEqualTo("weeklyEventCreation");

		assertThat(eventController.myEvents(model, eventManager)).isEqualTo("myEvents");
	}
}
