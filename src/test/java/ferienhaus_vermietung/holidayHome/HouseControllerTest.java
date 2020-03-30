package ferienhaus_vermietung.holidayHome;

import ferienhaus_vermietung.AbstractIntegrationTests;
import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.Model;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class HouseControllerTest extends AbstractIntegrationTests {

	@Autowired
	HouseController houseController;

	@Autowired
	CatalogHouses catalogHouses;

	@Autowired
	EventCatalog eventCatalog;

	@Autowired
	UserAccountManager userAccountManager;

	protected HouseEditForm houseEditForm = Mockito.mock(HouseEditForm.class);
	protected EventConfirmationForm eventConfirmationForm = Mockito.mock(EventConfirmationForm.class);
	protected HouseForm houseForm = Mockito.mock(HouseForm.class);
	protected AvailabilityForm availabilityForm = Mockito.mock(AvailabilityForm.class);


	@Test
	@WithMockUser(authorities = "ROLE_LANDLORD")
	void ControllerIntegrationTest() {

		UserAccount landlordAccount = userAccountManager.create("TestLandlord", "TestLandlord", Role.of("ROLE_LANDLORD"));
		landlordAccount.setFirstname("Sasha");
		landlordAccount.setLastname("Schmidt");
		landlordAccount.setEmail("sasha.schmidt@example.com");
		userAccountManager.save(landlordAccount);

		Optional<UserAccount> landlord = Optional.of(landlordAccount);

		Features features = new Features();
		features.put(Features.Feature.BABY_CRIB,true);
		features.put(Features.Feature.TELEVISION,true);
		House house = catalogHouses.save(new House("TestHaus", "TestLandlord", "TestLandlord", 3, new String[]{"Am Königsgarten 5", "01193", "Dresden"}, "Schön", "", 10, 1, 15, features));
		String startDay = "13";
		String startMonth = "5";
		String startYear = "2019";
		String endDay = "20";
		String endMonth = "5";
		String endYear = "2019";

		UserAccount user = new UserAccount();
		Optional<UserAccount> userAccount = Optional.of(user);

		Event event = eventCatalog.save(new Event("New Years Eve", 10, "Elbe", "Happy New Year", LocalDate.of(2018, 12, 31), LocalTime.of(22, 0), ""));

		Mockito.when(houseEditForm.getHouseID()).thenReturn(house.getId());

		Mockito.when(houseForm.getNameHouse()).thenReturn("TestHaus");
		Mockito.when(houseForm.getBeds()).thenReturn(3);
		Mockito.when(houseForm.getDescription()).thenReturn("Schön");
		Mockito.when(houseForm.getStreet()).thenReturn("Am Königsgarten 5");
		Mockito.when(houseForm.getPostcode()).thenReturn("01193");
		Mockito.when(houseForm.getCity()).thenReturn("Dresden");
		Mockito.when(houseForm.getRent()).thenReturn(10.00);
		Mockito.when(houseForm.getMinStay()).thenReturn(1);
		Mockito.when(houseForm.getMaxStay()).thenReturn(15);
		Mockito.when(houseForm.getImage()).thenReturn("");
		Mockito.when(houseForm.getKitchen()).thenReturn(false);
		Mockito.when(houseForm.getAc()).thenReturn(false);
		Mockito.when(houseForm.getWashingMachine()).thenReturn(false);
		Mockito.when(houseForm.getTumbleDryer()).thenReturn(false);
		Mockito.when(houseForm.getWifi()).thenReturn(false);
		Mockito.when(houseForm.getBabyCrib()).thenReturn(true);
		Mockito.when(houseForm.getTelevision()).thenReturn(true);

		Mockito.when(eventConfirmationForm.getHouseID2()).thenReturn(house.getId());
		Mockito.when(eventConfirmationForm.getEventID()).thenReturn(event.getId());

		Mockito.when(availabilityForm.getDayStart()).thenReturn(13);
		Mockito.when(availabilityForm.getMonthStart()).thenReturn(5);
		Mockito.when(availabilityForm.getYearStart()).thenReturn(2019);
		Mockito.when(availabilityForm.getDayEnd()).thenReturn(20);
		Mockito.when(availabilityForm.getMonthEnd()).thenReturn(5);
		Mockito.when(availabilityForm.getYearEnd()).thenReturn(2019);
		Mockito.when(availabilityForm.getKitchenSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getKitchenSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getAcSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getWashingMachineSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getTumbleDryerSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getWifiSearch()).thenReturn(false);
		Mockito.when(availabilityForm.getBabyCribSearch()).thenReturn(true);
		Mockito.when(availabilityForm.getTelevisionSearch()).thenReturn(true);

		Model model = new ExtendedModelMap();

		assertThat(houseController.displayHouses(model)).isEqualTo("houses");

		String view = houseController.mapHousePage(house, startDay, startMonth, startYear, endDay, endMonth, endYear);
		assertThat(view).isEqualTo("redirect:/detail/"+house.getId()+"/"+startDay+"_"+startMonth+"_"+startYear
				+"-"+endDay+"_"+endMonth+"_"+endYear);

		assertThat(houseController.detail(house, model, userAccount)).isEqualTo("detail");

		String view2 = houseController.detail(house, startDay, startMonth, startYear, endDay, endMonth, endYear, model);
		assertThat(view2).isEqualTo("detail");

		String view3 = houseController.editsDone(houseEditForm, model, house);
		assertThat(view3).isEqualTo("editHouse");

		// Event confirmation
		String view4 = houseController.confirmEvent(eventConfirmationForm);
		assertThat(view4).isEqualTo("redirect:/detail/"+house.getId());
		assertThat(house.getConfirmedEvents()).hasSize(1);

		// Event rejection
		String view5 = houseController.rejectEvent(eventConfirmationForm);
		assertThat(view5).isEqualTo("redirect:/detail/"+house.getId());
		assertThat(house.getConfirmedEvents()).hasSize(1);

		// Event deletion
		String view6 = houseController.removeEvent(eventConfirmationForm);
		assertThat(view6).isEqualTo("redirect:/detail/"+house.getId());
		assertThat(house.getConfirmedEvents()).hasSize(0);

		assertThat(houseController.editsDone()).isEqualTo("editHouse");

		String view7 = houseController.myHouses(model, landlord);
		assertThat(view7).isEqualTo("myHouses");

		String view8 = houseController.createHouse(model, houseForm);
		assertThat(view8).isEqualTo("createHouse");

		String view9 = houseController.AvailableHousesInit(model, availabilityForm);
		assertThat(view9).isEqualTo("searchAvailability");

	}
}