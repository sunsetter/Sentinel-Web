/*
 * ServerAttribute.java
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
 * Entity model of ServerAttribute
 */
@Entity
public class ServerAttribute extends Model  {
	
	/** id of attribute */
	@Id
	public Long id;
	
	/** Attribute value */
	public Double value;
	
	/** server's id */
	@ManyToOne
	public Server server;
	
	/** Attribute's id */
	@ManyToOne
	public Attribute attribute;
	
	/**
	 * Generic query helper for entity ServerAttribute with id Long
	 */
	public static Finder<Long, ServerAttribute> find() {
		return new Finder<Long, ServerAttribute>(Long.class, ServerAttribute.class);
	}
	
	
	/**
	 * Checks if server attribute is already in db and updates it
	 * If server attribute is not present in db, insert it 
	 */
	public void save(){
		if( (this.attribute == null) || (this.server == null) ){
			return;
		}
		if( this.id == null ){
			ServerAttribute serverAttribute = ServerAttribute.find()
				 .where()
				 .eq("attribute.name", this.attribute.name )
				 .eq("server.id", this.server.id )
				 .orderBy("id")
				 .fetch("attribute")
				 .findUnique();
			if ( serverAttribute != null){
				this.id = serverAttribute.id;
				this.update();
			}else{
				super.save();;
			}			
		}else{
			super.save();
		}
		
	}
	
}







