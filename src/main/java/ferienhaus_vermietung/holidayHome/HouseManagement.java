package ferienhaus_vermietung.holidayHome;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.order.OrderComponent;
import ferienhaus_vermietung.order.Status;
import ferienhaus_vermietung.user.landlord.Landlord;
import org.springframework.data.util.Streamable;
import ferienhaus_vermietung.order.Booking;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional
public class HouseManagement {

	private final CatalogHouses catalogHouses;
	private final OrderComponent orderComponent;
	private final HouseComponent houseComponent;

	/**
	 * @param catalogHouses must not be {@literal null}.
	 * @param orderComponent must not be {@literal null}.
	 * @param houseComponent must not be {@literal null}.
	 */

	public HouseManagement(CatalogHouses catalogHouses, OrderComponent orderComponent, HouseComponent houseComponent) {

		Assert.notNull(catalogHouses, "House Catalog must not be null!");
		Assert.notNull(orderComponent, "OrderComponent must not be null!");

		this.catalogHouses = catalogHouses;
		this.orderComponent = orderComponent;
		this.houseComponent = houseComponent;
	}

	/**
	 * Creation of {@Link House} as well as saving in {@param catalogHouses}.
	 *
	 * @param houseForm must not be {@literal null}.
	 * @param image must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return created {@Link House}.
	 */
	public House createHouse(HouseForm houseForm, String image, UserAccount userAccount) {
		String houseName = houseForm.getNameHouse();
		String landlordName = userAccount.getFirstname() + " " + userAccount.getLastname();
		String userNameLandlord = userAccount.getUsername();
		String description = houseForm.getDescription();
		String[] address = {houseForm.getStreet(), houseForm.getPostcode(), houseForm.getCity()};
		Double rent = houseForm.getRent();
		Integer beds = houseForm.getBeds();
		Integer minStay = houseForm.getMinStay();
		Integer maxStay = houseForm.getMaxStay();
		Features features = houseComponent.getAllFeatures(houseForm);

		House house = new House(houseName, landlordName, userNameLandlord, beds, address, description, image, rent,
				minStay, maxStay, features);

		return catalogHouses.save(house);

	}

	/**
	 * Editing of {@Link House} and saving changes in {@param catalogHouses}.
	 *
	 * @param houseEditForm must not be {@literal null}.
	 * @param image must not be {@literal null}.
	 * @param id must not be {@literal null}.
	 */
	public void editHouse(HouseEditForm houseEditForm, String image, ProductIdentifier id) {

		Optional<House> changedHouseOptional = catalogHouses.findById(id);
		House changedHouse = changedHouseOptional.orElseThrow(() -> new NullPointerException("House not present!"));

		String nameHouse = houseEditForm.getNameHouse();
		String description = houseEditForm.getDescription();
		String[] address = {houseEditForm.getStreet(), houseEditForm.getPostcode(), houseEditForm.getCity()};
		double rent = houseEditForm.getRent();
		int beds = houseEditForm.getBeds();
		int minStay = houseEditForm.getMinStay();
		int maxStay = houseEditForm.getMaxStay();
		Features features = houseComponent.getAllFeatures(houseEditForm);

		changedHouse.setNameHouse(nameHouse);
		changedHouse.setAddress(address);
		changedHouse.setDescription(description);
		changedHouse.setRent(rent);
		changedHouse.setBeds(beds);
		changedHouse.setMaxStay(maxStay);
		changedHouse.setMinStay(minStay);
		changedHouse.setFeatures(features);
		changedHouse.setImage(image);

		catalogHouses.save(changedHouse);

	}

	/**
	 * Confirmation of {@Link Event} for {@Link House}.
	 *
	 * @param event must not be {@literal null}.
	 * @param house must not be {@literal null}.
	 */
	public void confirmEvent(Event event, House house) {

		if (house.getRequestedEvents().contains(event)) {
			house.removeRequestedEvent(event);
		}
		house.addConfirmedEvent(event);
	}

	/**
	 * Rejection of {@Link Event} for {@Link House}.
	 *
	 * @param event must not be {@literal null}.
	 * @param house must not be {@literal null}.
	 */
	public void rejectEvent(Event event, House house) {

		if (house.getRequestedEvents().contains(event)) {
			house.removeRequestedEvent(event);
		}
	}

	/**
	 * Removing of {@Link Event} for {@Link House}.
	 *
	 * @param event must not be {@literal null}.
	 * @param house must not be {@literal null}.
	 */
	public void removeEvent(Event event, House house) {

		if (house.getConfirmedEvents().contains(event)) {
			house.removeConfirmedEvent(event);
		}
	}

	/**
	 * Finding all {@Link House}s.
	 *
	 * @return Streamable of {@Link House}s.
	 */
	public Streamable<House> findAll() {
		return Streamable.of(catalogHouses.findAll());
	}

	/**
	 * Finding of {@Link House} via {@param houseName}.
	 *
	 * @param houseName must not be {@literal null}.
	 * @return Streamable of {@Link House}s.
	 */
	public Streamable<House> findByHouseName(String houseName) {
		return catalogHouses.findByName(houseName);
	}

	/**
	 * Testing if {@Link House}s are available using {@param guests}, {@param features}, {@param minStay} and
	 * {@param maxStay}, entered by a {@Link User}.
	 * @param availabilityForm must not be {@literal null}.
	 * @return
	 */
	public ArrayList<HashSet<House>> availabilities(AvailabilityForm availabilityForm){

		ArrayList<HashSet<House>> returnList = new ArrayList<>();

		int yearStart = availabilityForm.getYearStart();
		int monthStart = availabilityForm.getMonthStart();
		int dayStart = availabilityForm.getDayStart();
		LocalDate dateStart = LocalDate.of(yearStart, monthStart, dayStart);

		int yearEnd = availabilityForm.getYearEnd();
		int monthEnd = availabilityForm.getMonthEnd();
		int dayEnd = availabilityForm.getDayEnd();
		LocalDate dateEnd = LocalDate.of(yearEnd, monthEnd, dayEnd);

		long days = dateStart.until(dateEnd,DAYS);

		HashSet<House> availableHouses = new HashSet<>();
		HashSet<House> wishlistHouses = new HashSet<>();

		Iterable<House> houses = catalogHouses.findAll();
		ArrayList<House> houseCollection = new ArrayList<>();
		for (House h : houses) {
			if (days >= h.getMinStay() && days <= h.getMaxStay()
					&& h.getFeatures().satisfies(houseComponent.getAllFeatures(availabilityForm))
					&& (availabilityForm.getGuests() <= h.getBeds())) {
				houseCollection.add(h);
			}
		}

		availableHouses = testIfAvailable(houseCollection, dateStart, dateEnd);

		for (House h : houses) {
			if (!availableHouses.contains(h) && houseCollection.contains(h)) {
				wishlistHouses.add(h);
			}
		}

		returnList.add(availableHouses);
		returnList.add(wishlistHouses);
		return returnList;
	}


	/**
	 * Testing if {@Link House} is available during a time period entered by a {@Link User}.
	 *
	 * @param houseCollection must not be {@literal null}.
	 * @param dateStart must not be {@literal null}.
	 * @param dateEnd must not be {@literal null}.
	 * @return List of available {@Link House}s as well as {@Link House}s that can be added to a wishlist.
	 */
	public HashSet<House> testIfAvailable(ArrayList<House> houseCollection, LocalDate dateStart, LocalDate dateEnd) {

		HashSet<House> availableHouses = new HashSet<>();

		for (House h : houseCollection) {
			Collection<Booking> bookingsHouse = h.getBookingPlan();
			for (Booking b : bookingsHouse) {
				if (dateEnd.isBefore(b.getFirstDate()) || dateStart.isAfter(b.getLastDate())){
					availableHouses.add(h);
				} else if (dateEnd.isEqual(b.getFirstDate()) || dateStart.isEqual(b.getLastDate())){
					availableHouses.add(h);
				} else if(b.getStatus().equals(Status.CANCELLED) || b.getStatus().equals(Status.CANCELLEDLATE)
						|| b.getStatus().equals(Status.WISHLIST) || b.getStatus().equals(Status.WISHLISTPOSSIBLE)){
					availableHouses.add(h);
				} else {
					availableHouses.remove(h);
					break;
				}
			}
			if (bookingsHouse.isEmpty()) {
				availableHouses.add(h);
			}
		}

		return availableHouses;
	}


	/**
	 * Deletion of all {@Link Houses} of {@Link Landlord}.
	 *
	 * @param landlord must not be {@literal null}.
	 */
	public void deleteAllHousesOfLandlord(Landlord landlord){
		for(House h : catalogHouses.findByUserNameLandlord(landlord.getUserAccount().getUsername())){
			deleteHouse(h);
		}
	}

	/**
	 * Deletion of a {@Link House}.
	 * @param house must not be {@literal null}.
	 */
	public void deleteHouse(House house){
		ArrayList<Booking> bookings = new ArrayList<>();
		for (Booking b : house.getBookingPlan()) {
			if(!(b.getStatus().equals(Status.COMPLETED) || b.getStatus().equals(Status.CANCELLED)
					|| b.getStatus().equals(Status.CANCELLEDLATE))){
				bookings.add(b);
			} else {
				b.setHouse(catalogHouses.findByName("DELETED ITEM").get().findFirst()
						.orElseThrow(() -> new NullPointerException("House not Found")));
			}
		}
		for (Booking b : bookings) {
			orderComponent.deleteBooking(b);
			b.setHouse(catalogHouses.findByName("DELETED ITEM").get().findFirst()
					.orElseThrow(() -> new NullPointerException("House not Found")));
		}
		catalogHouses.delete(house);
	}

}