package ferienhaus_vermietung.order;

import org.salespointframework.catalog.ProductIdentifier;

interface OrderForm {

	ProductIdentifier getHouse();

	String getType();

	ProductIdentifier getEvent();

	Integer getYearStart();

	Integer getMonthStart();

	Integer getDayStart();

	Integer getYearEnd();

	Integer getMonthEnd();

	Integer getDayEnd();

}
