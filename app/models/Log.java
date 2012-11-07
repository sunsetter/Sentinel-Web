/*
 * Log.java
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
import javax.persistence.*;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.CreatedTimestamp;


/**
 * Entity model of Log
 */
@Entity
public class Log extends Model  {
	
	/** id of log */
	@Id
	public Long id;

	/** log's server */
	@ManyToOne
	public Server server;

	/** date when log was created in DB */
	@Column(insertable = false, updatable = false, columnDefinition="TIMESTAMP DEFAULT NOW()")
	@CreatedTimestamp
	public Timestamp  dateCreate;

	
	/** list of log's attributes */
	@OneToMany(mappedBy="log")
	public List<LogAttribute> logAttributes = new ArrayList<LogAttribute>(); 
	
	/**
	 * Generic query helper for entity Log with id Long
	 */
	public static Finder<Long, Log> find() {
		return new Finder<Long, Log>(Long.class, Log.class);
	}
	
	/**
	 * Return a page of logs
	 *
	 * @param page Page to display
	 * @param pageSize Number of computers per page
	 * @param sortBy Log property used for sorting
	 * @param order Sort order (either or asc or desc)
	 * @param filter Filter applied on the name column
	 */
	public static Page<Log> getPage(int page, int pageSize, String sortBy, String order, String filter, Long account_id) {
		return 
				find().where()
				.ilike("server.name", "%" + filter + "%")
				.eq("server.account.id", account_id)
				.orderBy(sortBy + " " + order)
				.fetch("server")
				.findPagingList(pageSize)
				.getPage(page);
	}

	/**
	 * Return last serer's log
	 * @param server_id  Server's id      
	 */
	public static Log getLast(Long server_id) {
		return find().where()
						.eq("server.id", server_id)
						.orderBy("dateCreate desc")
						.setMaxRows(1)
						.findUnique();
		
	}
	
	/** Method saves Log to db */
	public void save(){
		super.save();
		for(LogAttribute l :  this.logAttributes){
			l.log = this;
			l.save();
		}
		Statistic.saveFromLog(this.id);
	}
	

	
}
