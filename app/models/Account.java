/*
 * Account.java
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

import play.data.format.*;

/**
 * Entity model of Account
 */

@Entity
public class Account  extends Model {

	/** id of account*/
	@Id
	public Long id;

	/**  unique hash of account */
	public String hash;	

	/** account's name */
	@Required	
	public String name;	

	/** list of account's servers */
	@OneToMany(mappedBy="account")
	public List<Server> serverItems;
	
	/** date when account was created in DB */
	@Column(insertable = false, updatable = false, columnDefinition="TIMESTAMP DEFAULT NOW()")
	@CreatedTimestamp
	public Timestamp  dateCreate;


	
	/**
	 * Generic query helper for entity Account with id Long
	 */
	public static Finder<Long, Account> find() {
		return new Finder<Long, Account>(Long.class, Account.class);
	}


	/** Method saves account to db */
	public void save(){
		if( this.hash == null ){
			this.hash = Account.getRandomHash();
		}
		super.save();
	}


	/**
	 * Create and return random hash value 
	 * @return String generated hash
	 */
	public static String getRandomHash( ){
		Integer length = 10;
		String symbols="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		StringBuilder sb=new StringBuilder(length);
		Random r = new Random();
		
		for(int j=0;j<length;j++)
		{
			sb.append(symbols.charAt(r.nextInt( symbols.length() )));
		}

		return sb.toString();
	}


}












