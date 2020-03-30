package ferienhaus_vermietung.event;

import ferienhaus_vermietung.holidayHome.HouseManagement;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class EventController {

	private final EventCatalog eventCatalog;
	private final EventManagement eventManagement;
	private final HouseManagement houseManagement;
	private final EventComponent eventComponent;

	/**
	 * @param eventCatalog must not be {@literal null}.
	 * @param eventManagement must not be {@literal null}.
	 * @param houseManagement must not be {@literal null}.
	 * @param eventComponent must not be {@literal null}.
	 */
	EventController(EventCatalog eventCatalog, EventManagement eventManagement, HouseManagement houseManagement,
					EventComponent eventComponent) {

		Assert.notNull(eventCatalog, "EventCatalog must not be null");
		Assert.notNull(houseManagement, "HouseManagement must not be null");
		Assert.notNull(eventManagement, "EventManagement must not be null");

		this.eventCatalog = eventCatalog;
		this.eventManagement = eventManagement;
		this.houseManagement = houseManagement;
		this.eventComponent = eventComponent;
	}

	/**
	 * Displays every {@link Event}.
	 *
	 * @param model must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/events")
	String events(Model model) {

		model.addAttribute("uniqueEventList", eventCatalog.findByType(Event.EventType.UNIQUE));
		model.addAttribute("weeklyEventList", eventCatalog.findByType(Event.EventType.WEEKLY));
		model.addAttribute("monday", eventComponent.weekDayEvents("monday"));
		model.addAttribute("tuesday", eventComponent.weekDayEvents("tuesday"));
		model.addAttribute("wednesday", eventComponent.weekDayEvents("wednesday"));
		model.addAttribute("thursday", eventComponent.weekDayEvents("thursday"));
		model.addAttribute("friday", eventComponent.weekDayEvents("friday"));
		model.addAttribute("saturday", eventComponent.weekDayEvents("saturday"));
		model.addAttribute("sunday", eventComponent.weekDayEvents("sunday"));
		model.addAttribute("numbers",eventComponent.findGreatestList());

		return "events";

	}

	/**
	 * @param event must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping("/detailEvent/{event}")
	String detailEventPost(@PathVariable Event event) {
		return "detailEvent";
	}

	/**
	 * Displays details of an {@link Event}.
	 *
	 * @param event must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/detailEvent/{event}")
	String detailEventGet(@PathVariable Event event) {
		return "detailEvent";
	}

	/**
	 * Displays {@link Event} editing page.
	 *
	 * @param uniqueEventEditForm must not be {@literal null}.
	 * @param weeklyEventEditForm must not be {@literal null}.
	 * @param model must not be {@literal null}.
	 * @param event must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/event/{event}")
	@PreAuthorize("hasRole('ROLE_EVENTMANAGER')")
	String edits(UniqueEventEditForm uniqueEventEditForm, WeeklyEventEditForm weeklyEventEditForm, Model model,
				 @PathVariable() Event event) {

		if (event.getType() == Event.EventType.UNIQUE) {
			model.addAttribute("uniqueEventEditForm", uniqueEventEditForm);
			model.addAttribute("currentEvent", event);
			model.addAttribute("eid", event.getId());

			return "editUniqueEvent";
		} else {
			model.addAttribute("weeklyEventEditForm", weeklyEventEditForm);
			model.addAttribute("currentEvent", event);
			model.addAttribute("eid", event.getId());

			return "editWeeklyEvent";
		}

	}

	/**
	 * Initiates editing of unique {@link Event}.
	 *
	 * @param uniqueEventEditForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping(value = "/editUniqueEvent", params = "edit")
	String EditInCatalogUnique(@Valid UniqueEventEditForm uniqueEventEditForm, Errors result,
							   @LoggedIn Optional<UserAccount> userAccount) {

		if (result.hasErrors()) {
			return "editUniqueEvent";
		}

		eventManagement.editUniqueEvent(uniqueEventEditForm, uniqueEventEditForm.getEventID());


		return "redirect:/myEvents";
	}

	/**
	 * Initiates deletion of unique {@link Event}.
	 *
 	 * @param uniqueEventEditForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping(value = "/editUniqueEvent", params = "delete")
	String DeleteEventUnique(@Valid UniqueEventEditForm uniqueEventEditForm, Errors result,
							 @LoggedIn Optional<UserAccount> userAccount) {
		eventManagement.deleteEvent(eventCatalog.findById(uniqueEventEditForm.getEventID())
				.orElseThrow(() -> new NullPointerException("Event not present!")));

		return "redirect:/myEvents";
	}

	/**
	 * @return html template for url
	 */
	@GetMapping("/editUniqueEvent")
	String editsDoneUnique(){
		return "editUniqueEvent";
	}

	/**
	 * Initiates editing of weekly {@link Event}.
	 *
	 * @param weeklyEventEditForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return template for url
	 */
	@PostMapping(value = "/editWeeklyEvent", params = "edit")
	String EditInCatalogWeekly(@Valid WeeklyEventEditForm weeklyEventEditForm, Errors result,
							   @LoggedIn Optional<UserAccount> userAccount) {

		if (result.hasErrors()) {
			return "editWeeklyEvent";
		}

		eventManagement.editWeeklyEvent(weeklyEventEditForm, weeklyEventEditForm.getEventID());


		return "redirect:/myEvents";
	}

	/**
	 * Initiates deletion of weekly {@link Event}.
	 *
	 * @param weeklyEventEditForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping(value = "/editWeeklyEvent", params = "delete")
	String DeleteEventWeekly(@Valid WeeklyEventEditForm weeklyEventEditForm, Errors result,
							 @LoggedIn Optional<UserAccount> userAccount) {
		eventManagement.deleteEvent(eventCatalog.findById(weeklyEventEditForm.getEventID())
				.orElseThrow(() -> new NullPointerException("Event not present!")));
		return "redirect:/myEvents";
	}

	/**
	 * @return html template for url
	 */
	@GetMapping("/editWeeklyEvent")
	String editsDoneWeekly(){
		return "editWeeklyEvent";
	}

	/**
	 * Displays unique {@link Event} creation page.
	 *
	 * @param model must not be {@literal null}.
	 * @param uEventForm must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/uniqueEventCreation")
	@PreAuthorize("hasRole('ROLE_EVENTMANAGER')")
	String createEvent(Model model, UniqueEventCreationForm uEventForm) {
		model.addAttribute("uEventForm", uEventForm);
		model.addAttribute("houses", houseManagement.findAll().filter(house -> house.getBeds() > 0));
		return "uniqueEventCreation";
	}

	/**
	 * Initiates creation of unique {@link Event}.
	 *
	 * @param uEventForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return html template for url.
	 */
	@PostMapping("/uniqueEventCreation")
	String createNewEvent(@Valid UniqueEventCreationForm uEventForm, Errors result,
						  @LoggedIn Optional<UserAccount> userAccount) {

		if (result.hasErrors()) {
			return "uniqueEventCreation";
		}

		eventManagement.createUniqueEvent(uEventForm, userAccount
				.orElseThrow(() -> new NullPointerException("UserAccount not present!")).getUsername());

		return "redirect:/myEvents";
	}

	/**
	 * Displays weekly {@link Event} creation page.
	 *
	 * @param model must not be {@literal null}.
	 * @param wEventForm must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/weeklyEventCreation")
	@PreAuthorize("hasRole('ROLE_EVENTMANAGER')")
	String createEvent(Model model, WeeklyEventCreationForm wEventForm) {
		model.addAttribute("wEventForm", wEventForm);
		model.addAttribute("houses", houseManagement.findAll().filter(house -> house.getBeds() > 0));
		return "weeklyEventCreation";
	}

	/**
	 * Initiates creation of weekly {@link Event}.
	 *
	 * @param wEventForm must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return html template for url
	 */
	@PostMapping("/weeklyEventCreation")
	String createNewEvent(@Valid WeeklyEventCreationForm wEventForm, Errors result,
						  @LoggedIn Optional<UserAccount> userAccount) {

		if (result.hasErrors()) {
			return "weeklyEventCreation";
		}

		eventManagement.createWeeklyEvent(wEventForm, userAccount
				.orElseThrow(() -> new NullPointerException("UserAccount not present!")).getUsername());

		return "redirect:/myEvents";
	}

	/**
	 * Displays all {@link Event}s of {@link ferienhaus_vermietung.user.eventmanager.Eventmanager}.
	 *
	 * @param model must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/myEvents")
	@PreAuthorize("hasRole('ROLE_EVENTMANAGER')")
	String myEvents(Model model, @LoggedIn Optional<UserAccount> userAccount) {

		model.addAttribute("eventsByUser", eventCatalog.findByCreatorName(userAccount
				.orElseThrow(() -> new NullPointerException("UserAccount not present!")).getUsername()));

		return "myEvents";
	}

}
