/*
 * Setting.java
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

import java.util.*;

import play.db.ebean.*;
import play.db.ebean.Model.Finder;
import play.data.validation.Constraints.*;
import javax.persistence.*;

import play.data.format.*;

/**
 * Setting entity
 * Stores  applications settings in db
 */
@Entity
public class Setting extends Model  {
	
	/** id of setting */
	@Id
	public String id;
	
	/** name(key) of setting */
	@Required
	public String name;
	
	/** value of setting */
	public String value;
	
	/** description of setting */
	public String description;
	
	/**
	 * Generic query helper for entity Server with id Long
	 */
	public static Finder<Long, Setting> find() {
		return new Finder<Long, Setting>(Long.class, Setting.class);
	}
	
	/**
	 * Return setting's value of a given name
	 * @param name setting's name      
	 */
	public static String getValue(String name) {
		 Setting setting = find().where()
						.eq("name", name)
						.setMaxRows(1)
						.findUnique();
		 
		 return (setting == null) ? null : setting.value;
	}
	

}




