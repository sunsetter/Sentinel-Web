/*
 * LogAttribute.java
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
 * Entity model of LogAttribute
 */
@Entity
public class LogAttribute extends Model  {
	
	/** id of attribute */
	@Id
	public Long id;
	
	/** Log's id */
	@ManyToOne
	public Log log;
	
	/** Attribute's id */
	@ManyToOne
	public Attribute attribute;

	/** Attribute value */
	public Double value;

	/**
	 * Generic query helper for entity LogAttribute with id Long
	 */
	public static Finder<Long, LogAttribute> find() {
		return new Finder<Long, LogAttribute>(Long.class, LogAttribute.class);
	}
}
