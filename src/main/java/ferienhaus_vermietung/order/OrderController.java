package ferienhaus_vermietung.order;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.CatalogHouses;

import ferienhaus_vermietung.holidayHome.House;

import ferienhaus_vermietung.holidayHome.HouseManagement;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;

import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;


@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "SameReturnValue"})
@Controller
@SessionAttributes("cart")
class OrderController {

	private final BookingManager orderManager;
	private final CatalogHouses houses;
	private final EventCatalog events;
	private final OrderComponent orderComponent;
	private final BusinessTime businessTime;
	private final CalenderComponent calenderComponent;
	private final int week = 7;
	private final HouseManagement houseManagement;

	/**
	 * @param orderManager must not be {@literal null}.
	 * @param houses must not be {@literal null}.
	 * @param events must not be {@literal null}.
	 * @param orderComponent must not be {@literal null}.
	 * @param houseManagement must not be {@literal null}.
	 */

	OrderController(BookingManager orderManager, CatalogHouses houses, EventCatalog events,
					OrderComponent orderComponent, BusinessTime businessTime, CalenderComponent calenderComponent,
					HouseManagement houseManagement){
		Assert.notNull(orderManager, "OrderManager must not be null!");
		Assert.notNull(houses, "Houses must not be null!");
		Assert.notNull(events, "Events must not be null!");
		Assert.notNull(orderComponent, "OrderComponent must not be null!");
		Assert.notNull(houseManagement, "HouseManagement must not be null!");

		this.orderManager = orderManager;
		this.houses = houses;
		this.events = events;
		this.orderComponent = orderComponent;
		this.businessTime = businessTime;
		this.calenderComponent = calenderComponent;
		this.houseManagement = houseManagement;
	}

	/**
	 * Initializes a new {@link Cart} used for the booking process.
	 * @return
	 */
	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	/**
	 * Displays every event linked the chosen house.
	 * @param cart
	 * @param form
	 * @param model
	 * @return html template for url
	 */
	@PostMapping("/extras")
	String reqExtras(@ModelAttribute Cart cart,OrderForm form, Model model) {
		cart.clear();

		LocalDate fD = LocalDate.of(form.getYearStart(),form.getMonthStart(),form.getDayStart());
		LocalDate lD = LocalDate.of(form.getYearEnd(), form.getMonthEnd(), form.getDayEnd());
		List<Event> eventsList = new ArrayList<>();


		for (Event  event: houses.findById(form.getHouse())
				.orElseThrow(() -> new NullPointerException("House not present!")).getConfirmedEvents()) {
			if((event.getType().equals(Event.EventType.UNIQUE)) && (event.overlaps(fD,lD))) {
				eventsList.add(event);
			}
		}

		model.addAttribute("form",form);
		model.addAttribute("events", eventsList);

		cart.addOrUpdateItem(houses.findById(form.getHouse())
				.orElseThrow(() -> new NullPointerException("House not present!")), fD.until(lD,DAYS));

		return "extras";
	}

	/**
	 * Adds the chosen event to the booking. {@Link number} represents the amount of tickets.
	 * @param cart
	 * @param number
	 * @param form
	 * @param model
	 * @return html template for url
	 */
	@PostMapping("/add")
	String addEvent(@ModelAttribute Cart cart,@RequestParam("number") int number, OrderForm form, Model model) {
		List<Event> eventsList = new ArrayList<>();

		LocalDate fD = LocalDate.of(form.getYearStart(),form.getMonthStart(),form.getDayStart());
		LocalDate lD = LocalDate.of(form.getYearEnd(), form.getMonthEnd(), form.getDayEnd());

		for (Event  event: houses.findById(form.getHouse())
				.orElseThrow(() -> new NullPointerException("House not present!")).getConfirmedEvents()) {
			if((event.getType().equals(Event.EventType.UNIQUE)) && (event.overlaps(fD,lD))) {
				eventsList.add(event);
			}
		}

		model.addAttribute("form",form);
		model.addAttribute("events", eventsList);

		cart.addOrUpdateItem(events.findById(form.getEvent())
				.orElseThrow(() -> new NullPointerException("Event not present!")), number);

		return "extras";
	}

	/**
	 * Displays every product chosen by the user.
	 * @param cart
	 * @param model
	 * @param form
	 * @return html template for url
	 */
	@GetMapping("/cart")
	@PreAuthorize("hasRole('ROLE_TENANT')")
	String getBasket(@ModelAttribute Cart cart, Model model, OrderForm form){
		model.addAttribute("form", form);
		return "cart";
	}

	@PostMapping("/cart")
	@PreAuthorize("hasRole('ROLE_TENANT')")
	String reqBasket(@ModelAttribute Cart cart, Model model, OrderForm form) {
		model.addAttribute("form", form);
		return "cart";
	}

	/**
	 * Removes the certain item with the {@Link id} and determines then if there is a house left.
	 * Without houses left the booking process gets cancelled and the cart cleared.
	 * @param cart
	 * @param id
	 * @param model
	 * @param form
	 * @return html template for url
	 */

	@PostMapping("/delete")
	String deleteItem(@ModelAttribute Cart cart,@RequestParam("itemId") String id, Model model, OrderForm form) {
		Assert.notNull(id, "Id must not be null!");
		cart.removeItem(id);
		model.addAttribute("form", form);
		for (CartItem ci: cart) {
			if(ci.getProduct().getClass().equals(House.class)){
				return "cart";
			}
		}
		cart.clear();
		return "redirect:/";
	}

	/**
	 * Last step of the Booking process where every Item in the {@Link cart} gets added into a booking.
	 * @param cart
	 * @param form
	 * @param userAccount
	 * @return html template for url
	 */

	@PostMapping("/checkout")
	String buy(@ModelAttribute Cart cart, OrderForm form, @LoggedIn Optional<UserAccount> userAccount) {
		Status status;
		if (form.getType().equals("booking")){
			status = Status.OPEN;
		} else {
			status = Status.RESERVATION;
		}

		LocalDate fD = LocalDate.of(form.getYearStart(),form.getMonthStart(),form.getDayStart());
		LocalDate lD = LocalDate.of(form.getYearEnd(), form.getMonthEnd(), form.getDayEnd());

		return userAccount.map(account -> {
			Booking booking = null;
			for (CartItem ci: cart) {
				if (houses.findById(ci.getProduct().getId()).isPresent()){
					House house =  houses.findById(ci.getProduct().getId()).get();
					booking = new Booking(status,account, Cash.CASH, fD,lD, house);
					cart.addItemsTo(booking);
					if(status.equals(Status.RESERVATION)) {
						orderManager.setReserved(booking);
					} else if(status.equals(Status.OPEN))	{
						orderManager.setOpen(booking);
					}
					orderComponent.addBookingToHouse(booking);
					break;
				}
			}
			cart.clear();
			return "redirect:/detailBooking/"+booking.getId();
		}).orElse("redirect:/login");
	}

	/**
	 * This represents the paying process.
	 * @param booking
	 * @return html template for url
	 */

	@PostMapping("/booking/{booking}")
	String booking(@PathVariable Booking booking){
		orderManager.setOpen(booking);
		return "redirect:/detailBooking/"+booking.getId();
	}
	/**
	 *  This represents the paying process.
	 * @param booking
	 * @return html template for url
	 */
	@PostMapping("/deposit/{booking}")
	String deposit(@PathVariable Booking booking) {
		orderManager.payDeposit(booking);
		return "redirect:/detailBooking/"+booking.getId();
	}
	/**
	 *  This represents the paying process.
	 * @param booking
	 * @return html template for url
	 */
	@PostMapping("/pay/{booking}")
	String pay(@PathVariable Booking booking) {
		orderManager.payBooking(booking);
		return "redirect:/detailBooking/"+booking.getId();
	}
	/**
	 * Booking can be get canceled depending on user choice or time.
	 * @param booking
	 * @param userAccount
	 * @return html template for url
	 */
	@PostMapping("/cancel/{booking}")
	String cancel(@PathVariable Booking booking, @LoggedIn Optional<UserAccount> userAccount){
		if((userAccount.isPresent() && userAccount.get().hasRole(Role.of("ROLE_TENANT"))) &&
				businessTime.getTime().toLocalDate().until(booking.getFirstDate(), ChronoUnit.DAYS) < week){
			orderManager.cancelLateBooking(booking);
		} else {
			orderManager.cancelBooking(booking);
		}
		return "redirect:/detailBooking/"+booking.getId();
	}

	/**
	 * Representation of all bookings for the admin.
	 * @param model
	 * @return html template for url
	 */
	@GetMapping("/orders")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	String orders(Model model) {
		model.addAttribute("orders",orderManager.findBy(Status.BOOKING).and(orderManager.findBy(Status.OPEN))
				.and(orderManager.findBy(Status.PAID)).and(orderManager.findBy(Status.RESERVATION))
				.and(orderManager.findBy(Status.CANCELLEDLATE)).and(orderManager.findBy(Status.CANCELLED))
				.and(orderManager.findBy(Status.COMPLETED)));
		return("orders");
	}

	/**
	 * Display of every Booking of the User and also representation of the paying process.
	 * @param userAccount
	 * @param model
	 * @return html template for url
	 */
	@GetMapping("/myBookings")
	String myBookings(@LoggedIn Optional<UserAccount> userAccount, Model model) {
		model.addAttribute("user",userAccount
				.orElseThrow(() -> new NullPointerException("UserAccount not present!")));
		Streamable<Booking> bookings = orderManager.findBy(userAccount.get());
		ArrayList<Booking> orders = new ArrayList<>();

		for (Booking b : bookings) {
			if (b.getStatus() != Status.WISHLIST && b.getStatus() != Status.WISHLISTPOSSIBLE) {
				orders.add(b);
			}
		}

		model.addAttribute("orders", orders);

		return("myBookings");
	}

	/**
	 * For further information of the {@link Booking}.
	 * @param booking
	 * @param model
	 * @param userAccount
	 * @return html template for url
	 */

	@GetMapping("/detailBooking/{booking}")
	String detailBooking(@PathVariable Booking booking, Model model, @LoggedIn Optional<UserAccount> userAccount){

		if(userAccount.isPresent() && userAccount.get().hasRole(Role.of("ROLE_TENANT"))){
			model.addAttribute("instance", "tenant");
		} else {
			model.addAttribute("instance", "landlord");
		}
		model.addAttribute("uniqueEvents", orderComponent.getUniqueEvents(booking));
		model.addAttribute("weeklyEvents",orderComponent.getWeeklyEvents(booking));
		model.addAttribute("booking", booking);

		return "detailBooking";
	}

	/**
	 *
	 * @param booking
	 * @return
	 */

	@PostMapping("/createCalendar/{booking}")
	String calendar(@PathVariable Booking booking){
		calenderComponent.write(booking);
		return "redirect:/detailBooking/"+booking.getId();
	}

	/**
	 *
	 * @param model
	 * @return
	 */

	@GetMapping("/bookingsLandlord")
	@PreAuthorize("hasRole('ROLE_LANDLORD')")
	String cancellationInit(Model model) {

		model.addAttribute("orders", orderManager.findBy(Status.OPEN).and(orderManager.findBy(Status.RESERVATION))
				.and(orderManager.findBy(Status.BOOKING)).and(orderManager.findBy(Status.PAID)));
		return "bookingsLandlord";
	}

	/**
	 *
	 * @param model
	 * @return
	 */

	@GetMapping("/cancellations")
	@PreAuthorize("hasRole('ROLE_LANDLORD')")
	String cancellations(Model model) {

		model.addAttribute("orders", orderManager.findBy(Status.CANCELLED)
				.and(orderManager.findBy(Status.CANCELLEDLATE)));

		return "cancellations";
	}

	/**
	 *
	 * @param house
	 * @param startDay
	 * @param startMonth
	 * @param startYear
	 * @param endDay
	 * @param endMonth
	 * @param endYear
	 * @return
	 */
	@PostMapping("/houses/wishlist/{house}/{startDay}_{startMonth}_{startYear}-{endDay}_{endMonth}_{endYear}")
	String mapHousePage(@PathVariable House house,@PathVariable String startDay, @PathVariable String startMonth,
						@PathVariable String startYear, @PathVariable String endDay, @PathVariable String endMonth,
						@PathVariable String endYear){
		return "redirect:/detail/wishlist/"+house.getId()+"/"+startDay+"_"+startMonth+"_"+startYear
				+"-"+endDay+"_"+endMonth+"_"+endYear;
	}

	/**
	 *
	 * @param house
	 * @param startDay
	 * @param startMonth
	 * @param startYear
	 * @param endDay
	 * @param endMonth
	 * @param endYear
	 * @param model
	 * @return
	 */

	@GetMapping("/detail/wishlist/{house}/{startDay}_{startMonth}_{startYear}-{endDay}_{endMonth}_{endYear}")
	String wishlistCard(@PathVariable House house,@PathVariable String startDay, @PathVariable String startMonth,
						@PathVariable String startYear, @PathVariable String endDay, @PathVariable String endMonth,
						@PathVariable String endYear, Model model) {

		model.addAttribute("features",house.getFeatures());
		model.addAttribute("fill", house.getBookingPlan());
		model.addAttribute("yearStart", Integer.parseInt(startYear));
		model.addAttribute("monthStart", Integer.parseInt(startMonth));
		model.addAttribute("dayStart", Integer.parseInt(startDay));
		model.addAttribute("yearEnd", Integer.parseInt(endYear));
		model.addAttribute("monthEnd", Integer.parseInt(endMonth));
		model.addAttribute("dayEnd", Integer.parseInt(endDay));
		model.addAttribute("instance", "wishlist");

		return "detail";
	}

	/**
	 *
	 * @param house
	 * @param startDay
	 * @param startMonth
	 * @param startYear
	 * @param endDay
	 * @param endMonth
	 * @param endYear
	 * @param userAccount
	 * @return
	 */

	@PostMapping(value = "/houses/{house}/{startDay}_{startMonth}_{startYear}-{endDay}_{endMonth}_{endYear}",
			params = "addToWishlist")
	@PreAuthorize("hasRole('ROLE_TENANT')")
	String wishlistInit(@PathVariable House house,@PathVariable String startDay, @PathVariable String startMonth,
						@PathVariable String startYear, @PathVariable String endDay, @PathVariable String endMonth,
						@PathVariable String endYear, @LoggedIn Optional<UserAccount> userAccount) {
		LocalDate dateStart = LocalDate.of(Integer.parseInt(startYear),Integer.parseInt(startMonth),
				Integer.parseInt(startDay));
		LocalDate dateEnd = LocalDate.of(Integer.parseInt(endYear),Integer.parseInt(endMonth),
				Integer.parseInt(endDay));

		Booking booking = new Booking(Status.WISHLIST, userAccount.orElseThrow(()
				-> new NullPointerException("UserAccount not found")), Cash.CASH, dateStart, dateEnd, house);
		orderManager.save(booking);
		orderComponent.addBookingToHouse(booking);

		return "redirect:/wishlist";
	}

	/**
	 *
	 * @param model
	 * @param userAccount
	 * @return
	 */

	@GetMapping("wishlist")
	@PreAuthorize("hasRole('ROLE_TENANT')")
	String wishlist(Model model, @LoggedIn Optional<UserAccount> userAccount){
		Streamable<Booking> bookings = orderManager.findBy(userAccount.orElseThrow(()
				-> new NullPointerException("UserAccount not found")));

		ArrayList<Booking> wishlistBookings = new ArrayList<>();
		ArrayList<Booking> possible = new ArrayList<>();
		ArrayList<Booking> notPossible = new ArrayList<>();

		for (Booking b : bookings){
			if (b.getStatus() == Status.WISHLIST) {
				wishlistBookings.add(b);
			}
		}

		for (Booking b : wishlistBookings){
			ArrayList<House> house = new ArrayList<>();
			house.add(b.getHouse());
			HashSet<House> availableHouses = houseManagement.testIfAvailable(house, b.getFirstDate(),
					b.getLastDate());
			if(availableHouses.contains(b.getHouse())){
				possible.add(b);
			} else {
				notPossible.add(b);
			}
		}

		ArrayList<Booking> toRemove = new ArrayList<>();
		for (Booking b : bookings) {
			for (Booking wb : wishlistBookings) {
				if (wb.getFirstDate().equals(b.getFirstDate()) && wb.getLastDate().equals(b.getLastDate()) &&
						((b.getStatus() != Status.WISHLISTPOSSIBLE) && (b.getStatus() != Status.WISHLIST))) {
					toRemove.add(wb);
				}
			}
		}

		possible.removeAll(toRemove);
		notPossible.removeAll(toRemove);

		model.addAttribute("notPossible", notPossible);
		model.addAttribute("possible", possible);
		return "wishlist";
	}

}
