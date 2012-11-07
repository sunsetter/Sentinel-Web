/*
 * Accounts.java
 * 
 * Copyright (c) 2012, insign gmbh.  
 * www.insign.ch 
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package controllers;

import java.util.*;


import models.*;
import play.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

/**
 * 
 * Class provides actions for managing accounts
 * 
 */
public class Accounts extends Controller  {	

	/** Account form */
	public static Form<Account> accountForm = form(Account.class);

	/**
	 * Method display account with given unique hash 
	 * and shows list of servers of this account
	 * 
	 * @param account_hash unique account hash
	 * @return Result
	 */
	public static Result show(String account_hash) {

		// Check if given account exists
		Account account = Account.find()
				.where()
				.eq("hash", account_hash)
				.findUnique();

		if(account == null){
			return notFound("Account does not exists");
		}
	
		return  ok(						
				views.html.accounts.show.render(account) 
				);
	}
	
	
	/** 
	 * Method  handles form submission and saves Account to DB 
	 * @return Result
	 */
	public static Result save( ) {
		accountForm = form(Account.class).bindFromRequest();

		// Check if form has errors
		if(accountForm.hasErrors()) {
			return badRequest(views.html.accounts.save.render(accountForm));
		}
		
		accountForm.get().save();		
		flash("success", "New account \"" + accountForm.get().name + "\" has been created");
		return redirect(
				controllers.routes.Accounts.showLoadingPage() + 
				"#" +
				controllers.routes.Accounts.show(accountForm.get().hash)
		);

	}
	
	


	/**  
	 * Method shows empty form for new account 
	 * @return Result
	 */
	public static Result showBlank( ) {
		Form<Account> accountForm = form(Account.class);
		return ok(
				views.html.accounts.save.render(accountForm)
				);
	}
	
	/**  
	 * Method shows empty form for new account 
	 * @return Result
	 */
	public static Result showLoadingPage( ) {
		return ok(
				views.html.accounts.showLoadingPage.render()
				);
	}
	
	/** 
	 * Method  redirects from old account page to new
	 * @return Result
	 */
	public static Result redirectToNewPage( String account_hash ) {
		return redirect(
				controllers.routes.Accounts.showLoadingPage() + 
				"#" +
				controllers.routes.Accounts.show(account_hash)
		);

	}
}











