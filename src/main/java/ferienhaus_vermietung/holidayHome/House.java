
package ferienhaus_vermietung.holidayHome;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.order.Booking;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.springframework.util.Assert;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

import java.util.ArrayList;
import java.util.Collection;

import static org.salespointframework.core.Currencies.*;


@Entity
public class House extends Product {

	private String nameHouse;
	private String nameLandlord;
	private String userNameLandlord;
	private String description;
	private String image;
	private String[] address;
    private double rent;
    private int beds;
    private int minStay;
    private int maxStay;

    @Lob
    private Features features;

	@ElementCollection
	private Collection<Booking> houseBookingPlan;

	@ManyToMany
	private Set<Event> requestedEvents;

	@ManyToMany
	private Set<Event> confirmedEvents;

	@SuppressWarnings("unused")
	private House() {}

	/**
	 *
	 * @param nameHouse must not be {@literal null}.
	 * @param nameLandlord must not be {@literal null}.
	 * @param userNameLandlord must not be {@literal null}.
	 * @param address must not be {@literal null}.
	 * @param description must not be {@literal null}.
	 * @param image must not be {@literal null}.
	 * @param rent must not be {@literal null}.
	 * @param minStay must not be {@literal null}.
	 * @param maxStay must not be {@literal null}.
	 * @param features must not be {@literal null}.
	 */
	public House(String nameHouse, String nameLandlord, String userNameLandlord, int beds, String[] address,
				 String description, String image, double rent, int minStay, int maxStay, Features features){

		super(nameHouse, Money.of(rent, EURO));

		Assert.notNull(nameLandlord, "NameLandlord must not be null!");
		Assert.notNull(userNameLandlord, "UsernameLandlord must not be null!");
		Assert.notNull(address, "Adress must not be null!");
		Assert.notNull(description, "Description must not be null!");
		Assert.notNull(image, "Image must not be null!");

		this.nameHouse = nameHouse;
		this.nameLandlord = nameLandlord;
		this.userNameLandlord = userNameLandlord;
		this.beds = beds;
		this.address = address;
		this.description = description;
		this.image = image;
		this.rent = rent;
		this.minStay = minStay;
		this.maxStay = maxStay;
		this.features = features;
		houseBookingPlan = new ArrayList<>();

		this.requestedEvents = new HashSet<>();
		this.confirmedEvents = new HashSet<>();
	}

	//setter

	/**
	 * Set {@param name} of {@Link House}.
	 *
	 * @param nameHouse must not be {@literal null}.
	 */
	public void setNameHouse(String nameHouse) {
		this.nameHouse = nameHouse;
	}

	/**
	 * Set {@param nameLandlord} of {@Link House}.
	 *
	 * @param nameLandlord must not be {@literal null}.
	 */
	public void setNameLandlord(String nameLandlord) {
		this.nameLandlord = nameLandlord;
	}

	/**
	 * Set {@param number} of beds of {@Link House}.
	 *
	 * @param beds must not be {@literal null}.
	 */
	public void setBeds(int beds) {
		this.beds = beds;
	}

	/**
	 * Set {@param address} of {@Link House}.
	 *
	 * @param address must not be {@literal null}.
	 */
	public void setAddress(String[] address) {
			this.address = address;
	}

	/**
	 * Set {@param description} of {@Link House}.
	 *
	 * @param description must not be {@literal null}.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set {@param image} of {@Link House}.
	 *
	 * @param image
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * Set {@param rent} of {@Link House}.
	 *
	 * @param rent must not be {@literal null}.
	 */
	public void setRent(double rent) {
		super.setPrice(Money.of(rent, EURO));
		this.rent = rent;
	}

	/**
	 * Set {@param minStay} of {@Link House}.
	 *
	 * @param minStay must not be {@literal null}.
	 */
	public void setMinStay(int minStay) { this.minStay = minStay; }

	/**
	 * Set {@param maxStay} of {@Link House}.
	 *
	 * @param maxStay must not be {@literal null}.
	 */
	public void setMaxStay(int maxStay) { this.maxStay = maxStay; }

	/**
	 * Set {@param features} of {@Link House}.
	 *
	 * @param features must not be {@literal null}.
	 */
	public void setFeatures(Features features) {
		this.features = features;
	}

	/**
	 * Add {@Link Event} to list of {@param confirmedEvents}.
	 *
	 * @param event must not be {@literal null}.
	 */
	public void addConfirmedEvent(Event event) {
		confirmedEvents.add(event);
	}

	/**
	 * Add {@Link Event} to list of {@param requestedEvents}.
	 *
	 * @param event must not be {@literal null}.
	 */
	public void addRequestedEvent(Event event) {
		requestedEvents.add(event);
	}

	/**
	 * Remove {@Link Event} from list of {@param confirmedEvents}.
	 *
	 * @param event must not be {@literal null}.
	 */
	public void removeConfirmedEvent(Event event) {
		confirmedEvents.remove(event);
	}

	/**
	 * Remove {@Link Event} from list of {@param requestedEvents}.
	 * @param event
	 */
	public void removeRequestedEvent(Event event) {
		requestedEvents.remove(event);
	}

	//getter

	/**
	 * Get {@param name} of {@Link House}.
	 *
	 * @return {@param name} of {@Link House}.
	 */
	public String getNameHouse() {
		return nameHouse;
	}

	/**
	 * Get {@param nameLandlord} of {@Link House}.
	 *
	 * @return {@param nameLandlord} of {@Link House}.
	 */
	public String getNameLandlord() {
		return nameLandlord;
	}

	/**
	 * Get {@param userNameLandlord} of {@Link House}.
	 *
	 * @return {@param userNameLandlord} of {@Link House}.
	 */
	public String getUserNameLandlord() {
		return userNameLandlord;
	}

	/**
	 * Get {@param beds} of {@Link House}.
	 *
	 * @return {@param beds} of {@Link House}.
	 */
	public int getBeds() {
		return beds;
	}

	/**
	 * Get {@param address} of {@Link House}.
	 *
	 * @return {@param address} of {@Link House}.
	 */
	public String[] getAddress() {
		return address;
	}

	/**
	 * Get {@param description} of {@Link House}.
	 *
	 * @return {@param description} of {@Link House}.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get {@param image} of {@Link House}.
	 *
	 * @return {@param image} of {@Link House}.
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Get {@param rent} of {@Link House}.
	 *
	 * @return {@param rent} of {@Link House}.
	 */
	public double getRent() {
		return rent;
	}

	/**
	 * Get {@param minStay} of {@Link House}.
	 *
	 * @return {@param minStay} of {@Link House}.
	 */
	public int getMinStay() { return minStay; }

	/**
	 * Get {@param maxStay} of {@Link House}.
	 *
	 * @return {@param maxStay} of {@Link House}.
	 */
	public int getMaxStay() { return maxStay; }

	/**
	 * Get {@param features} of {@Link House}.
	 *
	 * @return {@param features} of {@Link House}.
	 */
	public Features getFeatures() {
		return features;
	}

	/**
	 * Get {@param houseBookingPlan} of {@Link House}.
	 *
	 * @return {@param houseBookingPlan} of {@Link House}.
	 */
	public Collection<Booking> getBookingPlan() {
		return houseBookingPlan;
	}

	/**
	 * Get {@param confirmedEvents} of {@Link House}.
	 *
	 * @return {@param confirmedEvents} of {@Link House}.
	 */
	public Set<Event> getConfirmedEvents() {
		return confirmedEvents;
	}

	/**
	 * Get {@param requestedEvents} of {@Link House}.
	 *
	 * @return {@param requestedEvents} of {@Link House}.
	 */
	public Set<Event> getRequestedEvents() {
		return requestedEvents;
	}

}