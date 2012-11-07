/*
 * Logs.java
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
import play.libs.*;
import play.mvc.BodyParser.Json;

import views.html.*;


import play.data.format.*;
import play.data.format.Formatters.*;
import java.text.ParseException;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;


/**
 * Class provides actions for managing Logs
 */
public class Logs extends Controller  {

	public static Form<Log> logForm = form(Log.class);

	/**
	 * Create and save log item to db
	 * 
	 * @param account_hash unique account hash
	 * @param serverName       server name
	 * @return Result
	 */
	public static Result add(String account_hash, String serverName, String osDescription) {
		// Check if given account exists		
		Account account = Account.find()
				.where()
				.eq("hash", account_hash)
				.findUnique();
		if(account == null){
			return ok("Account does not exists");
		}
		
		// Find (or create) server with given name		
		Server server = Server.getByName(serverName, account);
		if (server == null){
			return ok("Can't create server with given name");
		}		
		

		// Checks for log push frequency				
		if( server.isLogFrequencyExceeded()){
			return ok("Minimal log push frequency interval is not reached");			
		}
		
		Log log = new Log();		
		
		AttributeBinder<LogAttribute> logBinder = new AttributeBinder<LogAttribute>( LogAttribute.class, "log." );
		AttributeBinder<ServerAttribute> serverAttrBinder = new AttributeBinder<ServerAttribute>( ServerAttribute.class, "server." );
		
		
		// binds log attributes from request
		if( logBinder.bindFromRequest().hasErrors() ){
			return ok( logBinder.getLastError() );
		}
		log.logAttributes = logBinder.get();
		
		// binds server attributes from request	
		if( serverAttrBinder.bindFromRequest().hasErrors() ){
			return ok( serverAttrBinder.getLastError() );
		}
		server.serverAttributes = serverAttrBinder.bindFromRequest().get();
		
		
		List<String> errors = getLogValidationErrors(log);
		if( errors.size() > 0 ){
			String listString = "";
			for (String s : errors){
			    listString += s + "\n";
			}
			return ok(listString);
		}
		
		// Saving Server's OS Description and server's atributes
		server.osDescription = osDescription;
		server.save();
		

		log.server = server; 
		log.save();	
		return ok("ok");
	}

	
	
	
	/**
	 * Checks if log contains attributes with errors
	 * @param log 
	 * @return List of errors
	 */
	private static List<String> getLogValidationErrors( Log log ){
		List<String> errors = new ArrayList<String>();
		for( LogAttribute logAttribute :  log.logAttributes){
			if( (logAttribute.value < 0) || (logAttribute.value > 100) ){				
				errors.add("Argument \"" + logAttribute.attribute.name + "\" must be in percents");
			}
		}	
		return errors;
	}
	

	/**
	 * Action sends json data needed to draw diagram of selected server's attribute for selected period
	 * @return List of errors
	 */
	@BodyParser.Of(Json.class)
	public static Result getDiagramStat(String account_hash, String serverName, String attributeName, String period) {

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
		
		JsonNode json = request().body().asJson();
		ObjectNode result = play.libs.Json.newObject();
		String name = "Ann";//json.asText();//findPath("name").getTextValue();
		JsonNodeFactory factory = JsonNodeFactory.instance;

		ArrayNode diagramPoints = new ArrayNode(factory);
		for(LogStat log: LogStat.getDiagramPoints(selectedServer.id, period, attributeName)) {
			ArrayNode point = new ArrayNode(factory);
			point.add( log.dateCreate.getTime() );
			point.add( log.value );
			diagramPoints.add(point);
  		} 
		if(name == null) {			
			return ok(result);
		} else {
			result.put("points", diagramPoints);
			result.put("minTick", LogStat.getTimeOffset(period).getTime() );
			result.put("maxTick", Calendar.getInstance().getTime().getTime() );
			result.put("lastUpdate", Log.getLast(selectedServer.id).dateCreate.getTime() );
			return ok(result);
		}

	}

}




