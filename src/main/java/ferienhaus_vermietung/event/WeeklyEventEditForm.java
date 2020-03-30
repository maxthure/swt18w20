package ferienhaus_vermietung.event;

import org.salespointframework.catalog.ProductIdentifier;

interface WeeklyEventEditForm extends WeeklyEventForm{

	ProductIdentifier getEventID();
}

