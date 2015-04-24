/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.iot.services.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.enroll.UserManagement;
import org.wso2.iot.user.User;
import org.wso2.iot.utils.IOTConfiguration;

@Path("/UserManager")
public class UserManager {
	private static Log log = LogFactory.getLog(UserManager.class);

	
	@Path("/UserRegister")
	@POST
	public void userRegister(@QueryParam("username") String username,
	                         @QueryParam("password") String password,
	                         @QueryParam("firstName") String firstName,
	                         @QueryParam("lastName") String lastName,
	                         @QueryParam("email") String email, @QueryParam("roles") String[] roles)
	                                                                                                throws ConfigurationException,
	                                                                                                InstantiationException,
	                                                                                                IllegalAccessException {

		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setFirstname(firstName);
		user.setLastname(lastName);
		for (String role : roles) {
			user.addRole(role);
			log.info(role);
		}

		UserManagement userManagement = IOTConfiguration.getInstance().getUserManagementImpl();

		userManagement.addNewUser(user);
	}

	@Path("RemoveUser")
	@POST
	public void removeUser(@QueryParam("username") String username) throws InstantiationException,
	                                                               IllegalAccessException,
	                                                               ConfigurationException {

		UserManagement userManagement = IOTConfiguration.getInstance().getUserManagementImpl();
		userManagement.removeUser(username);

	}

	@Path("UpdateUser")
	@POST
	public void updateUser(@QueryParam("username") String username,
	                       @QueryParam("password") String password,
	                       @QueryParam("firstName") String firstName,
	                       @QueryParam("lastName") String lastName,
	                       @QueryParam("email") String email, @QueryParam("roles") String[] roles)
	                                                                                              throws InstantiationException,
	                                                                                              IllegalAccessException,
	                                                                                              ConfigurationException {

		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setFirstname(firstName);
		user.setLastname(lastName);
		for (String role : roles) {
			user.addRole(role);
			log.info(role);
		}

		UserManagement userManagement = IOTConfiguration.getInstance().getUserManagementImpl();

		userManagement.updateUser(user);
	}
	
	@Path("/login")
	@POST
	public String login(@QueryParam("username") String username,
	                  @QueryParam("password") String password,@Context HttpServletRequest request,@Context HttpServletResponse response) throws ConfigurationException,
	                                                          InstantiationException,
	                                                          IllegalAccessException {

		

		UserManagement userManagement = IOTConfiguration.getInstance().getUserManagementImpl();

		boolean authenticated = userManagement.isAuthenticated(username, password);
		
		if(authenticated){
			request.getSession(true);
			response.setStatus(200);
			
		}else{
			
			response.setStatus(401);
		}
		
		return username;
		
	}

}
