package ferienhaus_vermietung.event;

import org.salespointframework.catalog.Catalog;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.util.Streamable;

public interface EventCatalog extends Catalog<Event> {

	Sort DEFAULT_SORT = new Sort (Direction.ASC, "date");
	Sort TYPE_SORT = new Sort (Direction.ASC, "type");

	// Find event by creator
	Streamable<Event> findByCreatorName(String creatorName, Sort sort);
	default Streamable<Event> findByCreatorName(String creatorName) {
		return findByCreatorName(creatorName, TYPE_SORT);
	}

	// Find event by place
	Iterable<Event> findByPlace(String place, Sort sort);
	default Iterable<Event> findByPlace(String place) {
		return findByPlace(place, TYPE_SORT);
	}

	// Find event by event type
	Iterable<Event> findByType(Event.EventType type, Sort sort);
	default Iterable<Event> findByType(Event.EventType type) {
		return findByType(type, DEFAULT_SORT);
	}

	Iterable<Event> findByCreatorNameAndType(String creatorName ,Event.EventType type, Sort sort);
	default Iterable<Event> findByCreatorNameAndType(String creatorName, Event.EventType type) {
		return findByCreatorNameAndType(creatorName, type, DEFAULT_SORT);
	}


}

