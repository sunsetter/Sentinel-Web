/*
 * Global.java
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

import play.*;
import play.mvc.*;
import play.mvc.Http.RequestHeader;
import play.libs.*;
import static play.mvc.Results.*;
import play.api.templates.*;

import java.util.*;

import models.*;
import views.html.*;

public class Global extends GlobalSettings {
	
	@Override
	public void beforeStart(Application app) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+2"));
	}
	
	@Override
	public Result onError(RequestHeader request, Throwable t) {
		return badRequest("Appliaction error");
	}  

	@Override
	public Result onHandlerNotFound(RequestHeader request) {
		return notFound(
			      views.html.error404.render()
			    );
	} 

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
		return badRequest("Don't try to hack the URI!");
	}  
    
}