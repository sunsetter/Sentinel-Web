/*
 * LogStat.java
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
//import play.data.validation.Constraints.*;
import javax.persistence.*;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.Sql; 

import play.data.format.*;


/**
 * Entity sql model of Log statistic. 
 * Provides method for retrieving statistics about server's attributes.
 */
@Entity  
@Sql 
public class LogStat {
	
	/** name of an attribute */	
	public String attribute;

	/** attribute's value */
	public Double value;
	
	/** timestamp */
	public Date dateCreate;
	
	/** log's server */
	@ManyToOne
	public Server server;
	
	/**
	 * Generic query helper for entity LogStat with id Long
	 */
	public static Finder<Long, LogStat> find() {
		return new Finder<Long, LogStat>(Long.class, LogStat.class);
	}
	
	
	/**
	 * Generate LogStat List of the given log
	 * @param log
	 * @return List of attributes of last log
	 */
	public static List<LogStat> getFromLog( Log log ){
		List<LogStat> statList =  new ArrayList<LogStat>(); 
		for( LogAttribute logAttribute :  log.logAttributes){
			LogStat l = new LogStat();
			l.attribute  = logAttribute.attribute.name;
			l.value      = logAttribute.value;
			l.dateCreate = log.dateCreate;
			statList.add(l);
		}
		return statList;
	}
	
	
	
	/**
	 * Subtracts the specified amount of time given as a String parameter 
	 * to a current Date
	 * 
	 * @param String offset as a Sting parameter, like "8h", "7d"
	 * @return Date offset as Date type
	 */
	public static Date getTimeOffset(String offset){		
		
		Calendar timeOffset = Calendar.getInstance();
		
		if (offset.matches("^[\\d]+h$") ){
			int diff = Integer.parseInt( offset.substring(0, offset.length() - 1) );
			timeOffset.add(Calendar.HOUR, -diff);
			return timeOffset.getTime();
		}else if (offset.matches("^[\\d]+d$") ){
			int diff = Integer.parseInt( offset.substring(0, offset.length() - 1) );
			timeOffset.add(Calendar.DAY_OF_MONTH, -diff);
			return timeOffset.getTime();
		}else{
			// Default offset is 1 hour
			timeOffset.add(Calendar.HOUR, -1);
			return timeOffset.getTime();
		}		
	
	}
	

	/**
	 * Method gets average values of last serer's log for time period
	 * 
	 * @param server_id Id of the server
	 * @param offset String that represents time offset from current time.
	 * @return
	 */
	public static List<LogStat>  getLastStat(Long server_id, String offset){
		
		Log lastLog = Log.getLast(server_id);
		List<LogStat> statList =  new ArrayList<LogStat>(); 
		if( offset.equals("Now") ){			
			return  LogStat.getFromLog( lastLog );
		}
		
		Date period = LogStat.getTimeOffset(offset);
		for( LogAttribute logAttribute :  lastLog.logAttributes){
			String sql
		    	= " SELECT "
		    	+ "    server.id, "
				+ "    attribute.name, "
				+ "    avg(log_attribute.value) "
				+ " FROM "
				+ "    log_attribute "
				+ "	INNER JOIN attribute "
				+ "    ON (attribute.id = log_attribute.attribute_id) "
				+ "	INNER JOIN log "
				+ "    ON (log.id = log_attribute.log_id) "
				+ "	INNER JOIN server "
				+ "    ON (log.server_id = server.id) "
				+ " WHERE "
				+ "    log_attribute.attribute_id = '" + logAttribute.attribute.id + "' "
				+ " GROUP BY server.id, attribute.name ";   
			
			RawSql rawSql =   
			    RawSqlBuilder  
			        // let ebean parse the SQL so that it can  
			        // add expressions to the WHERE and HAVING   
			        // clauses  
			        .parse(sql)  
			        // map resultSet columns to bean properties  
			        .columnMapping("avg(log_attribute.value)",  "value")
			        .columnMapping("attribute.name",  "attribute")	
			        .columnMapping("server.id",  "server.id")  
			        .create();  
			  
			LogStat logStat = LogStat.find()
						 .setRawSql(rawSql)
						 .where()
						 .eq("server.id", server_id)
						 .ge("log.date_create", period)
						 .setMaxRows(1)
						 .findUnique();
			if(logStat != null){
				statList.add(logStat);
			}
		}
		return statList;
	}
	
	
	/**
	 * Method returns sql part which is calendar table 
	 * for timeinterval from current time to time in past, split into 30 time steps 
	 * 
	 * @param step Step between two rows in calendar table, given in ms
	 * @return String Sql part
	 */	
	public static String getCalendarTableSql(long step){
		long timeOffset = step;
		String calendarTable = "\n SELECT  floor( ( UNIX_TIMESTAMP(NOW() ) )  /"+ step +")*" + step + " t ";		
		for( long i = 0; i < 30; i++){			
			calendarTable
				+= " UNION ALL\n "
				+  " SELECT  floor( ( UNIX_TIMESTAMP(NOW()) - "+timeOffset+" ) /"+ step +")*" + step + " t ";
			timeOffset += step;
		}				
		return calendarTable;
	}
	
	
	/**
	 * Method Gets list of average values of serer's attribute 
	 * for timeinterval from period to current time, split into 30 timeintervals
	 * 
	 * @param server_id Id of the server
	 * @param offset String that represents time offset from current time.
	 * @param attribute_name name of the attribute to find stat
	 * @return List of log statistics
	 */
	public static List<LogStat> getDiagramPointsFromLog(Long server_id, Period period, String attribute_name){
		
		// find attribute by its name
		Attribute attribute = Attribute.find()
				 .where()
				 .eq("name", attribute_name )
				 .findUnique();
		//if attribute not found return empty list 
		if( attribute == null ){
			return new ArrayList<LogStat>(); 
		}
		
		// splits time interval into 30 pieces
		long step = Math.round(period.length / 30);
		
		String calendarSql = LogStat.getCalendarTableSql(step);
		
		String sql
		    = " SELECT" 
			+ "    avg(log_attribute.value), "
		    + "    FROM_UNIXTIME(calendar.t), "  
		    + "    log.server_id, " 
		    + "    attribute.name "
		    + " FROM " 
		    + "    ( " + calendarSql + " ) calendar"
		    + "  LEFT JOIN log"
		    + "    ON " 
		    + "    ( "
		    + "      calendar.t = (floor(unix_timestamp(log.date_create)/"+ step +")*" + step + ")"
		    + "      AND " 
			+ "      log.server_id = '" + server_id + "' "
		    + "    )"
		    + "  LEFT JOIN log_attribute"
		    + "    ON ("
		    + "         log.id                     = log_attribute.log_id "
		    + "     AND log_attribute.attribute_id = '" + attribute.id + "'  "		    
			+ "     ) "
		    + "	 LEFT JOIN attribute "
			+ "    ON ( attribute.id = log_attribute.attribute_id )"	    
		    + " GROUP BY log.server_id,attribute.name, FROM_UNIXTIME(calendar.t)"
		    + " ORDER BY FROM_UNIXTIME(calendar.t)" ; 
		  
		RawSql rawSql =   
		    RawSqlBuilder  
		        // let ebean parse the SQL so that it can  
		        // add expressions to the WHERE and HAVING clauses  
		        .parse(sql)  
		        // map resultSet columns to bean properties  
		        .columnMapping("avg(log_attribute.value)",  "value")
		        .columnMapping("attribute.name",  "attribute")	
		        .columnMapping("FROM_UNIXTIME(calendar.t)",  "dateCreate") 
		        .columnMapping("log.server_id",  "server.id")
		        .create();  
		
		return LogStat.find()
					 .setRawSql(rawSql)
					 .findList();  
		
	}
	
	
	

	
	
	public static List<LogStat> getDiagramPointsFromStat(Long server_id, Period period, String attributeName){
		
		// find attribute by its name
		Attribute attribute = Attribute.find()
				 .where()
				 .eq("name", attributeName )
				 .findUnique();
		
		//if attribute not found return empty list 
		if( attribute == null ){
			return new ArrayList<LogStat>(); 
		}
		
		// splits time interval into 30 pieces
		long step = Math.round(period.length / 30);
		
		String calendarSql = LogStat.getCalendarTableSql(step);
		
		String sql
		    = " SELECT" 
			+ "    statistic.avg_value, "
		    + "    FROM_UNIXTIME(calendar.t), "  
		    + "    statistic.server_id, " 
		    + "    attribute.name "
		    + " FROM " 
		    + "    ( " + calendarSql + " ) calendar"
		    + "  LEFT JOIN statistic"
		    + "    ON " 
		    + "    ( "
		    + "      calendar.t = statistic.avg_time"
		    + "      AND " 
			+ "      statistic.server_id    = '" + server_id + "' "
			+ "      AND " 
			+ "      statistic.attribute_id = '" + attribute.id + "' "
			+ "      AND " 
			+ "      statistic.period_id    = '" + period.id + "' "
		    + "    )"
		    + "	 LEFT JOIN attribute "
			+ "    ON ( attribute.id = statistic.attribute_id )"
		    + " ORDER BY FROM_UNIXTIME(calendar.t)" ; 
		  
				System.out.println("\n\n sql: " + sql + " \n\n");
		RawSql rawSql =   
		    RawSqlBuilder  
		        // let ebean parse the SQL so that it can  
		        // add expressions to the WHERE and HAVING clauses  
		        .parse(sql)  
		        // map resultSet columns to bean properties  
		        .columnMapping("statistic.avg_value",  "value")
		        .columnMapping("attribute.name",  "attribute")	
		        .columnMapping("FROM_UNIXTIME(calendar.t)",  "dateCreate") 
		        .columnMapping("statistic.server_id",  "server.id")
		        .create();  
		  
		
		return LogStat.find()
					 .setRawSql(rawSql)
					 .findList();  
		 
	}
	
	public static List<LogStat> getDiagramPoints(Long server_id, String periodName, String attributeName){		
		Period period = Period.find()
				 .where()
				 .eq("name", periodName )
				 .findUnique();
		//if attribute not found return empty list 
		if( period == null ){
			return new ArrayList<LogStat>(); 
		}
		if( period.statisticRequired ){
			return LogStat.getDiagramPointsFromStat(server_id, period, attributeName);
		}else{
			return LogStat.getDiagramPointsFromLog(server_id, period, attributeName);
		}
	}
	
}
