/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.scripting.services.api;

/**
 * Snow Owl administrator services
 * 
 * This service is responsible for administrative functions such as 
 * <ul><li>User manangement
 * 	<ul>
 * 		<li>List users
 * 		<li>Add users
 * 		<li>Notify users
 * 		<li>Disconnect users
 * 		<li>Change user information, reset pwd, etc.
 * 	</ul>
 *  <li>Aditing
 *  <ul>
 *  	<li>View log
 *  	<li>Filter log
 *  </ul>
 *  <li>Import/Export type of operations
 *  <li>DB backup and restore
 *  	 
 * </ul>
 * 
 *
 */
public interface IAdminService {

	//operations to find, add, list users
	//import export operations?
	//retrieve, find, analyze log
	
}