package ferienhaus_vermietung.finances.statistics;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.Status;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import javax.money.MonetaryAmount;
import java.util.*;


@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "SameReturnValue"})
@Controller
public class StatisticsController {

	private final StatisticsManagment statisticsManagment;

	StatisticsController(StatisticsManagment statisticsManagment){

		Assert.notNull(statisticsManagment, "StatisticsManagment must not be null");

		this.statisticsManagment = statisticsManagment;

	}

	@GetMapping("/statisticsLandlord")
	@PreAuthorize("hasRole('ROLE_LANDLORD')")
	String statisticsLandlord(Model model, @LoggedIn Optional< UserAccount > userAccount) {

		ArrayList<Booking> bookingsBooking = statisticsManagment.getBookings(userAccount, Status.BOOKING);
		ArrayList<Booking> bookingsPaid = statisticsManagment.getBookings(userAccount, Status.PAID);
		ArrayList<Booking> bookingsCompleted = statisticsManagment.getBookings(userAccount, Status.COMPLETED);;
		ArrayList<Booking> bookingsCancelledLate = statisticsManagment.getBookings(userAccount, Status.CANCELLEDLATE);

		MonetaryAmount bookingTotal = statisticsManagment.getTotal(bookingsBooking);
		MonetaryAmount paidTotal = statisticsManagment.getTotal(bookingsPaid);
		MonetaryAmount completedTotal = statisticsManagment.getTotal(bookingsCompleted);
		MonetaryAmount cancelledLateTotal = statisticsManagment.getTotal(bookingsCancelledLate);
		MonetaryAmount totalTotal = bookingTotal.add(paidTotal).add(completedTotal).add(cancelledLateTotal);

		model.addAttribute("bookingsBooking", bookingsBooking);
		model.addAttribute("bookingsPaid", bookingsPaid);
		model.addAttribute("bookingsCompleted", bookingsCompleted);
		model.addAttribute("bookingsCancelledLate", bookingsCancelledLate);
		model.addAttribute("bookingTotal", bookingTotal);
		model.addAttribute("paidTotal", paidTotal);
		model.addAttribute("completedTotal", completedTotal);
		model.addAttribute("cancelledLateTotal", cancelledLateTotal);
		model.addAttribute("totalTotal", totalTotal);
		return "statisticsLandlord";

	}

	@GetMapping("/statisticsEventmanager")
	@PreAuthorize("hasRole('ROLE_EVENTMANAGER')")
	String statisticsEventmanager(Model model, @LoggedIn Optional< UserAccount > userAccount){

		Map<Event, Pair<Quantity,MonetaryAmount>> uniqueMap= statisticsManagment.getUniqueMap(userAccount.orElseThrow(()
				-> new NullPointerException("UserAccount not found")));
		MonetaryAmount uniqueTotal = statisticsManagment.getUniqueTotal(uniqueMap);

		model.addAttribute("uniqueEvents", uniqueMap);
		model.addAttribute("uniqueTotal",uniqueTotal);

		return "statisticsEventmanager";
	}
}
