/*
 * Server.java
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

package models;

import java.sql.Timestamp;
import java.util.*;

import play.db.ebean.*;
import play.data.validation.Constraints.*;

import javax.persistence.*;

import com.avaje.ebean.annotation.CreatedTimestamp;

//import controllers.ServerAttribute;

import play.data.format.*;

/**
 * Entity model of Server
 */
@Entity
public class Server extends Model  {

	/** id of server */
	@Id
	public Long id;

	/** name of server */
	@Required
	public String name;
	
	/** Server's OS description */
	public String osDescription="";

	/** date when server was created in DB */
	@Column(columnDefinition="TIMESTAMP DEFAULT NOW()")
	@CreatedTimestamp
	public Timestamp   dateCreate;
	
	/** server's account */
	@ManyToOne
	public Account account; 	

	/** list of server's log */
	@OneToMany(mappedBy="server")
	public List<Log> logItems = new ArrayList<Log>(); 
	
	/** list of server's attributes */
	@OneToMany(mappedBy="server")
	public List<ServerAttribute> serverAttributes = new ArrayList<ServerAttribute>(); 
	
	/**
	 * Generic query helper for entity Server with id Long
	 */
	public static Finder<Long, Server> find() {
		return new Finder<Long, Server>(Long.class, Server.class);
	}
	
	/**
	 * Method find and return server by it's name and account hash. 
	 * If an server doesn't exist, creates it. 
	 * 
	 * @param name - Name of a server to find
	 * @param account - account of a server to find
	 * @return found or created attribute
	 */
	public static Server getByName( String serverName, Account account){
		Server server = Server.find()
				.where()
				.eq("name", serverName)
				.eq("account.hash", account.hash)
				.findUnique();
		
		if(server == null){
			if( serverName.length()>0 ){
				server = new Server();
				server.name = serverName;
				server.account = new Account();
				server.account.id = account.id;
				server.save();
			}else{
				return null;
			}
		}
		return server;
	}
	
	
	/**
	 * Get server attribute by it's name
	 * @param attrName name of the attribute
	 * @return server Attribute
	 */
	public ServerAttribute getAttributeByName( String attrName ){		
		ServerAttribute serverAttribute = ServerAttribute.find()
									 .where()
									 .eq("attribute.name", attrName )
									 .eq("server.id", this.id )
									 .orderBy("id")
									 .fetch("attribute")
									 .findUnique();
		return serverAttribute;
	}
	
	/** 
	 *  Checks server's last log and return true if some
	 *  of the log's attributes are greater then 75% 
	 *  @return true if server has problems
	 */
	public boolean hasProblems(){
		List<LogStat> statList = LogStat.getFromLog( Log.getLast(this.id));
		for( LogStat logStat: statList){
			if (logStat.value > 75){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Checks for log push frequency
	 * @return true if log push frequency exceeded settings value
	 */
	public boolean isLogFrequencyExceeded(){
		// Checks for log push frequency				
		Log lastLog;
		//If server's logs exists and minLogInterval is set in settings
		if( (( lastLog = Log.getLast(this.id) ) !=null)
				&& (Setting.getValue("minLogInterval") != null) )
		{			
			long now = new Date().getTime();
			long difference =  now - lastLog.dateCreate.getTime();			
			long minLogInterval = Long.parseLong( Setting.getValue("minLogInterval") );
			if ( (difference/1000) < minLogInterval ){
				//return true;
			}
		}
		return false;
	}
	
	/** Method saves server to db */
	public void save(){		
		super.save();
		for( ServerAttribute sa :  this.serverAttributes){
			sa.server = this;
			sa.save();
		}		
	}
	
	
	
}


