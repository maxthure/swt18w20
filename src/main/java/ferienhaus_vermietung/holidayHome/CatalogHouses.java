package ferienhaus_vermietung.holidayHome;

import ferienhaus_vermietung.order.Booking;
import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;


public interface CatalogHouses extends  Catalog<House>{

	Sort TYPE_SORT = new Sort (Sort.Direction.ASC, "nameHouse");

	Iterable<House> findByName(String name, Sort sort);

	// Find house by creator
	Iterable<House> findByUserNameLandlord(String userNameLandlord, Sort sort);
	default Iterable<House> findByUserNameLandlord(String userNameLandlord) {
		return findByUserNameLandlord(userNameLandlord, TYPE_SORT);
	}

	Iterable<House> findByHouseBookingPlan(Booking booking);

}
