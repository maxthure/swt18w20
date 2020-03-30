package ferienhaus_vermietung.order;

import ferienhaus_vermietung.AbstractIntegrationTests;
import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.holidayHome.Features;
import ferienhaus_vermietung.holidayHome.House;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest extends AbstractIntegrationTests {

	@Autowired
	OrderController orderController;

	@Autowired
	UserAccountManager userAccountManager;

	@Autowired
	CatalogHouses houseCatalog;

	@Autowired
	EventCatalog eventCatalog;

	OrderForm orderForm = Mockito.mock(OrderForm.class);


	@Test
	@WithMockUser(authorities = "ROLE_TENANT")
	void ControllerIntegrationTest() {

		UserAccount testTenant = userAccountManager.create("TestTenant", "TestTenant", Role.of("ROLE_Tenant"));
		testTenant.setFirstname("Vor");
		testTenant.setLastname("Nach");
		testTenant.setEmail("vor@nach.com");
		userAccountManager.save(testTenant);

		Optional<UserAccount> tTenant = Optional.of(testTenant);

		Event eventDummyUnique = eventCatalog.save(new Event("Party", 20.50, "APB E006", "Hier geht's ab!", LocalDate.of(2019, 2, 15), LocalTime.of(22, 0), ""));

		House house = houseCatalog.save(new House("house1", "Carol", "Landlord", 2, new String[]{"123 Fake Street","01234","Springfield"}, "Lorem ipsum 1", "", 20.00, 3, 8, new Features()));

		Mockito.when(orderForm.getEvent()).thenReturn(eventDummyUnique.getId());
		Mockito.when(orderForm.getHouse()).thenReturn(house.getId());
		Mockito.when(orderForm.getDayStart()).thenReturn(2);
		Mockito.when(orderForm.getDayEnd()).thenReturn(10);
		Mockito.when(orderForm.getMonthStart()).thenReturn(2);
		Mockito.when(orderForm.getMonthEnd()).thenReturn(2);
		Mockito.when(orderForm.getYearStart()).thenReturn(2019);
		Mockito.when(orderForm.getYearEnd()).thenReturn(2019);
		Mockito.when(orderForm.getType()).thenReturn("reservation");

		Cart cart = new Cart();
		Model model = new ExtendedModelMap();

		assertEquals("extras", orderController.reqExtras(cart,orderForm,model));
		assertEquals("extras", orderController.addEvent(cart,5,orderForm,model));
		assertEquals("cart",orderController.getBasket(cart,model,orderForm));
		assertEquals("cart",orderController.reqBasket(cart,model,orderForm));
		String id = "";
		String id2 = "";
		for (CartItem i: cart) {
			if(i.getProductName().equals("house1")) id = i.getId();
			else id2 = i.getId();
		}
		assertEquals("cart", orderController.deleteItem(cart,id2,model,orderForm));
		assertEquals("redirect:/", orderController.deleteItem(cart,id,model,orderForm));
		

		assertEquals("myBookings",orderController.myBookings(tTenant,model));
	}

}