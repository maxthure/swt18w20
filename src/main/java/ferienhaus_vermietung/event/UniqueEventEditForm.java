package ferienhaus_vermietung.event;

import org.salespointframework.catalog.ProductIdentifier;

interface UniqueEventEditForm extends UniqueEventForm{

	ProductIdentifier getEventID();

}

