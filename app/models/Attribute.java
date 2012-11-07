/*
 * Attribute.java
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
import play.data.validation.Constraints.*;

import javax.persistence.*;
import play.data.format.*;

/**
 * Entity model of Attribute
 */
@Entity
public class Attribute extends Model  {
	
	/** id of attribute */
	@Id
	public Long id;

	/** name of attribute */
	public String name;

	/** label of attribute */
	public String label;
	
	
	/**
	 * Generic query helper for entity Attribute with id Long
	 */
	public static Finder<Long, Attribute> find() {
		return new Finder<Long, Attribute>(Long.class, Attribute.class);
	}
	
	/**
	 * Method find and return attribute by it's name. 
	 * If an attribute doesn't exist, creates it. 
	 * 
	 * @param name - Name value of attribute to find
	 * @return found or created attribute
	 */
	public static Attribute getByName( String name ){		
		Attribute attribute = Attribute.find()
									 .where()
									 .eq("name", name )
									 .findUnique();
		if( attribute == null ){
			attribute = new Attribute();
			attribute.name =  name;
			attribute.save();
		}
		return attribute;
	}
}






