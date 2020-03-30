package ferienhaus_vermietung.event;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;

@Component
public class EventComponent {

	private final EventCatalog eventCatalog;

	/**
	 * @param eventCatalog must not be {@literal null}.
	 */
	public EventComponent(EventCatalog eventCatalog){

		Assert.notNull(eventCatalog, "EventCatalog must not be null!");

		this.eventCatalog = eventCatalog;
	}

	/**
	 * @param weekday must not be {@literal null}.
	 * @return {@link ArrayList<Event>} containig all Events of a specific Weekday.
	 */
	public ArrayList<Event> weekDayEvents(String weekday){
		ArrayList<Event> temp = new ArrayList<>();
		for(Event e : eventCatalog.findByType(Event.EventType.WEEKLY)){
			if(e.getDay().equals(weekday)){
				temp.add(e);
			}
		}
		return temp;
	}

	/**
	 * @return {@literal int[]} containing a number for each Event of the Weekday with most Events
	 */
	public int[] findGreatestList(){
		ArrayList<ArrayList<Event>> lists = new ArrayList<>();
		lists.add(weekDayEvents("monday"));
		lists.add(weekDayEvents("tuesday"));
		lists.add(weekDayEvents("wednesday"));
		lists.add(weekDayEvents("thursday"));
		lists.add(weekDayEvents("firday"));
		lists.add(weekDayEvents("saturday"));
		lists.add(weekDayEvents("sunday"));

		int size = 0;
		for (ArrayList<Event> l : lists){
			if(l.size() >= size){
				size = l.size();
			}
		}
		int[] numbers = new int[size];
		for (int i = 0; i < size; i++){
			numbers[i] = i;
		}
		return numbers;
	}

}
