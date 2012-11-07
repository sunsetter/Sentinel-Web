/*
 * AttributeBinder.java
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
import play.mvc.Http.Context;

/**
 * Class binds attributes from request to Attribute and <T> objects
 */
public class AttributeBinder<T>  {
	
	/** Class of the log <T>  */
	private Class<T> logClass;
	
	/** log prefix in query */
	private String paramPrefix = "";	

	/** list of errors */
	private List<String> errors = new ArrayList<String>(); 
		
	/** list of binded <T> attributes */
	private List <T> logAttributes  = new ArrayList<T>(); 
	
	
	/**
	 * @param logClass class of the <T>
	 * @param paramPrefix log prefix in query
	 */
	public AttributeBinder(Class<T> logClass, String paramPrefix){
		this.logClass = logClass;
		this.setParamPrefix(paramPrefix);
	}
	
	/** 
	 * Gets list of binded attributes
	 * @return list of binded  attributes
	 */
	public List <T> get(){		
		return this.logAttributes;		
	}
	
	/** 
	 * Binds attributes from request query 
	 * @return LogAttributeBinder
	 */
	public AttributeBinder<T> bindFromRequest(){
			
		Context ctx = Context.current();
		T attr;
		for(Map.Entry<String, String[]> param : ctx.request().queryString().entrySet()  ) {			
			attr = this.parse(param);
			if( attr != null ){
				logAttributes.add(attr);
			}
		}
		return this;		
	}	
	
	/** return true if there were errors during binding */
	public boolean hasErrors(){
		return ( this.errors.size() > 0 );
	}
	
	/** 
	 * Gets last error 
	 * @return last error 
	 */
	public String getLastError(){
		return this.errors.get( this.errors.size()-1 );
	}
	
	/** 
	 * Gets last error 
	 * @return last error 
	 */
	public String getParamPrefix() {
		return paramPrefix;
	}

	/**
	 * Sets query log prefix
	 * @param paramPrefix the log prefix
	 * @return LogAttributeBinder
	 */
	public AttributeBinder<T> setParamPrefix(String paramPrefix) {
		this.paramPrefix = paramPrefix;
		return this;
	}
	
	
	/**
	 * Method parse query entry and bind it to logAttribute and Attribute
	 * @param param Query's key and value
	 * @return
	 */
	private T parse( Map.Entry<String, String[]> param ){
		
		T logAttribute = null;			
		String queryKey = param.getKey();
	    if ( queryKey.startsWith( this.paramPrefix )){
	    	
	    	String attributeName = queryKey.substring( this.paramPrefix.length() );
			if (attributeName.length() > 0){					
				try {
					Attribute attribute = Attribute.getByName( attributeName );
					
					logAttribute = logClass.newInstance();
					java.lang.reflect.Field m = logClass.getField("value"); 
					m.set(logAttribute, Double.parseDouble( param.getValue()[0] ));
					
					m = logClass.getField("attribute"); 
					m.set(logAttribute, attribute);	
					
				} catch (NumberFormatException ex) {
					this.errors.add("Wrong input format for: " + attributeName);
					return null;
				} catch (Exception e) {
				}					
			} 		    	
	    }	    
	    return logAttribute;
	}
	
	
}
