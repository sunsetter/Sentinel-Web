/*
 * Servers.java
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
 * Class provides actions for managing servers 
 */
public class Servers extends Controller  {

	/** Server form */
	public static Form<Server> serverForm = form(Server.class);

	/**
	 * Action display average info about selected server
	 * 
	 * @param account_hash unique account hash
	 * @return Result
	 */
	public static Result show(String account_hash, String serverName, String period) {

		// Check if given account exists
		Account account = Account.find()
				.where()
				.eq("hash", account_hash)
				.findUnique();

		if(account == null){
			return notFound("Account does not exists");
		}
		
		// Find selected server
		Server selectedServer = Server.find()
				.where()
				.eq("name", serverName)
				.eq("account.id", account.id)
				.findUnique();
		
		// default server is first
		if( (selectedServer == null) && (account.serverItems.size()>0 ) ){
			selectedServer = account.serverItems.get(0);
		}
		if( selectedServer == null){
			return notFound("Server with given name not found");
		}
		
		return  
			ok(views.html.servers.show.render(
					account, 
					selectedServer, 
					period, 
					LogStat.getLastStat(selectedServer.id, period)
					));
	}
	

	/** 
	 * Action display diagram of selected server's attribute for selected period
	 * @return Result
	 */
	public static Result showDiagram(String account_hash, String serverName, String attributeName, String period ) {

		// Check if given account exists
		Account account = Account.find()
				.where()
				.eq("hash", account_hash)
				.findUnique();

		if(account == null){
			return notFound("Account does not exists");
		}

		// Find selected server
		Server selectedServer = Server.find()
				.where()
				.eq("name", serverName)
				.eq("account.id", account.id)
				.findUnique();

		
		if( selectedServer == null ){
			return notFound("Server does not exists");
		}
		return ok(						
				views.html.servers.showDiagram.render(account, selectedServer, period, attributeName) 
				);
	}



	/** 
	 * Method Display information about adding a new server
	 * @return Result
	 */
	public static Result add (String account_hash ) {
		// Check if given account exists
		Account account = Account.find()
				.where()
				.eq("hash", account_hash)
				.findUnique();
		if(account == null){
			return notFound("Account does not exists");
		}
		return ok(						
				views.html.servers.add.render( account ) 
				);
	}
	
	
	
	
	/** 
	 * Action shows confirmation page of deleting selected server
	 * @return Result
	 */
	public static Result confirmDelete(String account_hash, String serverName ) {

		// Check if given account exists
		Account account = Account.find()
				.where()
				.eq("hash", account_hash)
				.findUnique();

		if(account == null){
			return notFound("Account does not exists");
		}

		// Find selected server
		Server selectedServer = Server.find()
				.where()
				.eq("name", serverName)
				.eq("account.id", account.id)
				.findUnique();
		
		if( selectedServer == null ){
			return notFound("Server does not exists");
		}		
		
		return ok(views.html.servers.delete.render(account, selectedServer));
	}
	
	
	/** 
	 * Action removes selected server
	 * @return Result
	 */
	public static Result delete(String account_hash, String serverName ) {

		// Check if given account exists
		Account account = Account.find()
				.where()
				.eq("hash", account_hash)
				.findUnique();

		if(account == null){
			return notFound("Account does not exists");
		}

		// Find selected server
		Server selectedServer = Server.find()
				.where()
				.eq("name", serverName)
				.eq("account.id", account.id)
				.findUnique();
		
		if( selectedServer == null ){
			return notFound("Server does not exists");
		}
		
		selectedServer.delete();
		flash("success", "Server \"" + selectedServer.name + "\" has been removed from your account");
		return redirect(controllers.routes.Accounts.show(account.hash));
	}


}
