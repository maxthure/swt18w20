/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ferienhaus_vermietung.controller;

import ferienhaus_vermietung.holidayHome.House;
import ferienhaus_vermietung.holidayHome.HouseManagement;
import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.BookingManager;
import ferienhaus_vermietung.order.Status;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@SuppressWarnings("SameReturnValue")
@Controller
public class WelcomeController {

	private BookingManager orderManager;
	private HouseManagement houseManagement;

	/**
	 * @param orderManager must not be {@literal null}.
	 * @param houseManagement must not be {@literal null}.
	 */
	WelcomeController(BookingManager orderManager, HouseManagement houseManagement) {
		Assert.notNull(orderManager, "OrderManager must not be null!");
		Assert.notNull(houseManagement, "HouseManagement must not be null!");
		this.orderManager = orderManager;
		this.houseManagement = houseManagement;
	}

	/**
	 * @return html template for url
	 */
	@RequestMapping("/")
	public String index() {
		return "welcome";
	}

	/**
	 * Displays a message if booking of a {@Link House} on wishlist is now possible.
	 *
	 * @param model must not be {@literal null}.
	 * @param userAccount may be {@literal null}.
	 * @return html template for url
	 */
	@GetMapping("/")
	public String possibleWishlist(Model model, @LoggedIn Optional<UserAccount> userAccount) {
		boolean flag = false;

		if(userAccount.isPresent() && userAccount.get().hasRole(Role.of("ROLE_TENANT"))){
			Streamable<Booking> bookings = orderManager.findBy(userAccount.orElseThrow(()
					-> new NullPointerException("UserAccount not found")));

			for (Booking b : bookings){
				ArrayList<House> house = new ArrayList<>();
				house.add(b.getHouse());
				HashSet<House> availableHouses = houseManagement.testIfAvailable(house, b.getFirstDate(),
						b.getLastDate());
				if(availableHouses.contains(b.getHouse()) && b.getUserAccount().equals(userAccount.orElseThrow(()
						-> new NullPointerException("UserAccount not found"))) &&
						(b.getStatus().equals(Status.WISHLISTPOSSIBLE) || (b.getStatus().equals(Status.WISHLIST)))) {
					flag = true;
				}
			}
		}

		model.addAttribute("wishlistPossible", flag);
		return "welcome";
	}

	/**
	 * @return html template for url
	 */
	@RequestMapping("/faq")
	public String faq() {
		return "FAQ";
	}

}
