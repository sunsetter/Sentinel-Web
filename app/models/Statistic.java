/*
 * Statistic.java
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
import java.util.List;

import play.db.ebean.*;
import javax.persistence.*;



/**
 * Entity model of Statistic
 */
@Entity
public class Statistic extends Model  {
	
	/** id of log */
	@Id
	public Long id;

	/** related server */
	@ManyToOne
	public Server server;
	
	/** Attribute's id */
	@ManyToOne
	public Attribute attribute;
	
	/** period id */
	@ManyToOne
	public Period period;
	
	/** average time in seconds for one period */
	public Long avgTime;
	
	/** average value for one period */
	public Double avgValue;
	
	/** count of values that were aggregated  */
	public Long  valuesCount = 1L;
	
	/**
	 * Generic query helper for entity Period with id Long
	 */
	public static Finder<Long, Statistic> find() {
		return new Finder<Long, Statistic>(Long.class, Statistic.class);
	}
	
	/** Make new statistic from given log */
	public static void saveFromLog(Long logId){
		Log log = Log.find().byId(logId);
		List<Period> periods = Period.find()
				.where()
				.eq("statisticRequired", true)
				.findList();
		
		for( Period period: periods){
			long step = Math.round(period.length / 30);
			for(LogAttribute logAttribute :  log.logAttributes){
				Statistic statistic = new Statistic();
				statistic.server    = new Server();
				statistic.attribute = new Attribute();
				statistic.period    = new Period();
				
				statistic.server.id    = log.server.id;
				statistic.attribute.id = logAttribute.attribute.id;
				statistic.period.id    = period.id;
				statistic.avgValue     = logAttribute.value;
				
				statistic.avgTime      = Math.round(Math.floor( log.dateCreate.getTime() / (1000*step) )) * step  ;
				statistic.addToStatistic();
			}
		}
		
	}
	
	/**
	 * Checks if Statistic for given server, attribute and period is already in db and updates it
	 * If Statistic is not present in db, insert it 
	 */
	public void addToStatistic(){
		if( (   this.server.id    == null) 
			|| (this.attribute.id == null) 
			|| (this.period.id    == null) 
			|| (this.avgValue == null)  
		){
			return;
		}
		if( this.id == null ){
			Statistic statistic = Statistic.find()
				 .where()
				 .eq("attribute.id", this.attribute.id )
				 .eq("server.id", this.server.id )
				 .eq("period.id", this.period.id )
				 .eq("avgTime", this.avgTime )
				 .findUnique();
			if ( statistic != null){
				this.id = statistic.id;
				
				// calculate arithmetic mean
				this.avgValue = statistic.avgValue + (this.avgValue - statistic.avgValue)/( statistic.valuesCount +1 );
				this.valuesCount = statistic.valuesCount + 1;
				this.update();
			}else{
				super.save();
			}			
		}else{
			super.save();
		}
		
	}
	
	
}













