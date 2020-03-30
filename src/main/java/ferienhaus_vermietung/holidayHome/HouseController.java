package ferienhaus_vermietung.holidayHome;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Controller
public class HouseController {

	private final CatalogHouses housesCatalog;
	private final HouseManagement houseManagement;
	private final EventCatalog eventCatalog;
	private final BusinessTime businessTime;

	@Autowired
	private HttpServletRequest request;

	/**
	 * @param catalogHouses must not be {@literal null}.
	 * @param houseManagement must not be {@literal null}.
	 * @param eventCatalog must not be {@literal null}.
	 * @param businessTime must not be {@literal null}.
	 */
	HouseController(CatalogHouses catalogHouses, HouseManagement houseManagement, EventCatalog eventCatalog,
					BusinessTime businessTime) {

		Assert.notNull(catalogHouses, "Catalog must not be null");
		Assert.notNull(houseManagement, "HouseManagement must not be null");
		Assert.notNull(eventCatalog, "EventManagement must not be null");

		this.housesCatalog = catalogHouses;
		this.houseManagement = houseManagement;
		this.eventCatalog = eventCatalog;
		this.businessTime = businessTime;
	}

	/**
	 * Displays every {@Link House}.
	 *
	 * @param model must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/houses")
	String displayHouses(Model model) {
		ArrayList<House> houses = new ArrayList<>();
		for (House h : housesCatalog.findAll()) {
			if(h.getBeds() > 0) {
				houses.add(h);
			}
		}
		model.addAttribute("Houses", houses);
		model.addAttribute("title", "Häuser");

		return "houses";

	}

	/**
	 * @param house must not be {@literal null}.
	 * @param startDay must not be {@literal null}.
	 * @param startMonth must not be {@literal null}.
	 * @param startYear must not be {@literal null}.
	 * @param endDay must not be {@literal null}.
	 * @param endMonth must not be {@literal null}.
	 * @param endYear must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping("/houses/{house}/{startDay}_{startMonth}_{startYear}-{endDay}_{endMonth}_{endYear}")
	String mapHousePage(@PathVariable House house,@PathVariable String startDay, @PathVariable String startMonth,
						@PathVariable String startYear, @PathVariable String endDay, @PathVariable String endMonth,
						@PathVariable String endYear){
		return "redirect:/detail/"+house.getId()+"/"+startDay+"_"+startMonth+"_"+startYear
				+"-"+endDay+"_"+endMonth+"_"+endYear;
	}

	/**
	 * Displays details of a {@Link House} depending on {@Link Useraccount}.
	 *
	 * @param house must not be {@literal null}.
	 * @param model must not be {@literal null}.
	 * @param userAccount may be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/detail/{house}")
	String detail(@PathVariable House house, Model model, @LoggedIn Optional<UserAccount> userAccount) {
		model.addAttribute("features",house.getFeatures());
		model.addAttribute("fill", house.getBookingPlan());
		if(userAccount.isPresent() && userAccount.get().hasRole(Role.of("ROLE_LANDLORD"))){
				for(House h : housesCatalog.findByUserNameLandlord(userAccount.get().getUsername())){
					if(house.equals(h)){
						model.addAttribute("instance", "landlord");
						break;
					}
				}
		} else {
			model.addAttribute("instance", "");
		}
		return "detail";
	}

	/**
	 * Displays details of {@Link House}.
	 *
	 * @param house must not be {@literal null}.
	 * @param startDay must not be {@literal null}.
	 * @param startMonth must not be {@literal null}.
	 * @param startYear must not be {@literal null}.
	 * @param endDay must not be {@literal null}.
	 * @param endMonth must not be {@literal null}.
	 * @param endYear must not be {@literal null}.
	 * @param model must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/detail/{house}/{startDay}_{startMonth}_{startYear}-{endDay}_{endMonth}_{endYear}")
	String detail(@PathVariable House house, @PathVariable String startDay, @PathVariable String startMonth,
				  @PathVariable String startYear, @PathVariable String endDay, @PathVariable String endMonth,
				  @PathVariable String endYear, Model model) {

		LocalDate dateStart = LocalDate.of(Integer.parseInt(startYear),Integer.parseInt(startMonth),
				Integer.parseInt(startDay));
		LocalDate dateEnd = LocalDate.of(Integer.parseInt(endYear),Integer.parseInt(endMonth),
				Integer.parseInt(endDay));

		if(businessTime.getTime().toLocalDate().plusDays(1).until(dateStart,ChronoUnit.MONTHS) < 1){
			model.addAttribute("reservation", false);
		} else{
			model.addAttribute("reservation",true);
		}
		model.addAttribute("features",house.getFeatures());
		model.addAttribute("fill", house.getBookingPlan());
		model.addAttribute("total",house.getPrice().multiply(dateStart.until(dateEnd,ChronoUnit.DAYS)));
		model.addAttribute("yearStart", Integer.parseInt(startYear));
		model.addAttribute("monthStart", Integer.parseInt(startMonth));
		model.addAttribute("dayStart", Integer.parseInt(startDay));
		model.addAttribute("yearEnd", Integer.parseInt(endYear));
		model.addAttribute("monthEnd", Integer.parseInt(endMonth));
		model.addAttribute("dayEnd", Integer.parseInt(endDay));
		model.addAttribute("instance", "tenant");

		return "detail";
	}

	/**
	 * Displays {@Link House} editing page.
	 *
	 * @param houseEditForm must not be {@literal null}.
	 * @param model must not be {@literal null}.
	 * @param house must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/house/{house}")
	@PreAuthorize("hasRole('ROLE_LANDLORD')")
	String editsDone(HouseEditForm houseEditForm, Model model, @PathVariable() House house) {

		model.addAttribute("houseEditForm", houseEditForm);
		model.addAttribute("currentHouse", house);
		model.addAttribute("hid", house.getId());

		return "editHouse";

	}

	/**
	 * Initiates confirmation of {@Link Event}.
	 *
	 * @param eventConfirmationForm must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping("/confirmEvent")
	String confirmEvent(EventConfirmationForm eventConfirmationForm) {

		Event event = eventCatalog.findById(eventConfirmationForm.getEventID())
				.orElseThrow(() -> new NullPointerException("Event not present!"));
		House house = housesCatalog.findById(eventConfirmationForm.getHouseID2())
				.orElseThrow(() -> new NullPointerException("House not present!"));

		houseManagement.confirmEvent(event, house);

		return "redirect:/detail/"+house.getId();
	}

	/**
	 * Initiates rejection of {@Link Event}.
	 *
	 * @param eventConfirmationForm must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping("/rejectEvent")
	String rejectEvent(EventConfirmationForm eventConfirmationForm) {

		Event event = eventCatalog.findById(eventConfirmationForm.getEventID())
				.orElseThrow(() -> new NullPointerException("Event not present!"));
		House house = housesCatalog.findById(eventConfirmationForm.getHouseID2())
				.orElseThrow(() -> new NullPointerException("House not present!"));

		houseManagement.rejectEvent(event, house);

		return "redirect:/detail/"+house.getId();
	}

	/**
	 * Initiates rejection of {@Link Event}.
	 *
	 * @param eventConfirmationForm must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping("/removeEvent")
	String removeEvent(EventConfirmationForm eventConfirmationForm) {

		Event event = eventCatalog.findById(eventConfirmationForm.getEventID())
				.orElseThrow(() -> new NullPointerException("Event not present!"));
		House house = housesCatalog.findById(eventConfirmationForm.getHouseID2())
				.orElseThrow(() -> new NullPointerException("House not present!"));

		houseManagement.removeEvent(event, house);

		return "redirect:/detail/"+house.getId();
	}

	/**
	 * Initiates editing of {@Link House}.
	 *
	 * @param houseEditForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount may be {@literal null}.
	 * @param image must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping(value = "/editHouse", params = "edit")
	String editInCatalog(@Valid HouseEditForm houseEditForm, Errors result, @LoggedIn Optional<UserAccount> userAccount,
						 @RequestParam("image") MultipartFile image){

		if (result.hasErrors()) {
			return "editHouse";
		}

		if (image.isEmpty()) {
			houseManagement.editHouse(houseEditForm, housesCatalog.findById(houseEditForm.getHouseID()).
			orElseThrow(() -> new NullPointerException("House not found")).getImage(), houseEditForm.getHouseID());
		} else {
			saveImage(image);
			houseManagement.editHouse(houseEditForm, image.getOriginalFilename(), houseEditForm.getHouseID());
		}

		return "redirect:/detail/"+houseEditForm.getHouseID();
	}

	/**
	 * Initiates deleting of {@Link House}.
	 *
	 * @param houseEditForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount may be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping(value = "/editHouse", params = "delete")
	String deleteHouse(@Valid HouseEditForm houseEditForm, Errors result, @LoggedIn Optional<UserAccount> userAccount) {
		House house = housesCatalog.findById(houseEditForm.getHouseID())
				.orElseThrow(() -> new NullPointerException("House not found"));
		houseManagement.deleteHouse(house);
		return "redirect:/myHouses";
	}

	/**
	 * @return html template for url
	 */
	@GetMapping("/editHouse")
	String editsDone(){
		return "editHouse";
	}

	/**
	 * Displays all {@Link House}s of {@Link Landlord}.
	 *
	 * @param model must not be {@literal null}.
	 * @param userAccount may be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/myHouses")
	@PreAuthorize("hasRole('ROLE_LANDLORD')")
	String myHouses(Model model, @LoggedIn Optional<UserAccount> userAccount) {

		model.addAttribute("housesByUser", housesCatalog.findByUserNameLandlord(userAccount.
				orElseThrow(() -> new NullPointerException("UserAccount not present!")).getUsername()));
		
		return "myHouses";

	}

	/**
	 * Displays {@Link House}creation page.
	 *
	 * @param model must not be {@literal null}.
	 * @param houseForm must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/createHouse")
	@PreAuthorize("hasRole('ROLE_LANDLORD')")
	String createHouse(Model model, HouseForm houseForm) {
		
		model.addAttribute("houseCreationForm", houseForm);
		
		return "createHouse";

	}

	/**
	 * Initiates creation of {@Link House}.
	 *
	 * @param houseForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount may be {@literal null}.
	 * @param image must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping("/createHouse")
	String CreateHouse(@Valid HouseForm houseForm, Errors result, @LoggedIn Optional<UserAccount> userAccount,
					   @RequestParam("image") MultipartFile image) {
		if (result.hasErrors() || (houseForm.getMinStay() > houseForm.getMaxStay())) {
			return "createHouse";
		}

		houseManagement.createHouse(houseForm, image.getOriginalFilename(), userAccount
				.orElseThrow(() -> new NullPointerException("UserAccount not present!")));

		saveImage(image);

		return "redirect:/myHouses";
	}

	/**
	 * Displays search for {@Link Houses} page.
	 *
	 * @param model must not be {@literal null}.
	 * @param availabilityForm must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/searchAvailability")
	String AvailableHousesInit(Model model, AvailabilityForm availabilityForm) {

		model.addAttribute("availabilityForm", availabilityForm);

		return "searchAvailability";

	}

	/**
	 * Initiates search for available {@Link House}s.
	 *
	 * @param availabilityForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param model must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping("/availableHouses")
	String mapPage(@Valid AvailabilityForm availabilityForm, Errors result, Model model) {

		if (result.hasErrors()) {
			return "redirect:/searchAvailability";
		}

		ArrayList<HashSet<House>> returnedList = houseManagement.availabilities(availabilityForm);

		model.addAttribute("form",availabilityForm);
		model.addAttribute("availableHouses", returnedList.get(0));
		model.addAttribute("wishlistHouses", returnedList.get(1));

		return "availableHouses";
	}

	private void saveImage(MultipartFile image) {
		String uploadsDir = "/uploads/";
		String realPathtoUploads =  request.getServletContext().getRealPath(uploadsDir);
		if(! new File(realPathtoUploads).exists()) {
			new File(realPathtoUploads).mkdir();
		}

		String orgName = image.getOriginalFilename();
		String filePath = realPathtoUploads + orgName;
		File dest = new File(filePath);
		try {
			image.transferTo(dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
