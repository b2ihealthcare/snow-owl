/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core;

import com.b2international.commons.StringUtils;

/**
 * Snow Owl's logical id for the FHIR API
 * <br>
 * The logical Id follows the scheme: <i>repository:{branchPath}:{code}|{member}</i>
 * <br>
 * for example, snomedStore:MAIN/201101031/DK/20140203:59524001 //blood bank procedure
 * for reference set members, snomedStore:MAIN/201101031/DK/20140203|98403008:84f56f72-9f8b-423d-98b8-25961811393c //reference set member
 *  
 * @since 6.7
 */
public class LogicalId {
	
	private final String repositoryId;
	
	private final String branchPath;
	
	private final String componentId; //optional
	
	private final String memberId; //optional
	
	/**
	 * Logical ID for reference set members
	 * @param repositoryId
	 * @param path
	 * @param componentId
	 * @param memberId
	 */
	public LogicalId(String repositoryId, String branchPath, String componentId, String memberId) {
		this.repositoryId = repositoryId;
		this.branchPath = branchPath;
		this.componentId = componentId;
		this.memberId = memberId;
	}
	
	public LogicalId(String repositoryId, String branchPath, String componentId) {
		this(repositoryId, branchPath, componentId, null);
	}
	
	public LogicalId(String repositoryId, String branchPath) {
		this(repositoryId, branchPath, null, null);
	}

	public static LogicalId fromIdString(String idString) {
		
		if (StringUtils.isEmpty(idString)) {
			throw new IllegalArgumentException("Logical ID input string is null or empty");
		}
		
		if (!idString.contains(":")) {
			throw new IllegalArgumentException(String.format("Invalid logical ID [%s], the format should be repoId:branchPath:{componentId}.", idString));
		}
		
		if (idString.endsWith(":")) {
			throw new IllegalArgumentException(String.format("Invalid logical ID [%s], it should not end with a ':'. The format should be repoId:branchPath:{componentId}.", idString));
		}
		
		String[] splitIdString = idString.split(":");

		if (splitIdString.length > 3) {
			throw new IllegalArgumentException(String.format("Invalid logical ID [%s], too many segments. The format should be repoId:branchPath:{componentId}.", idString));
		}
		
		String repositoryId = splitIdString[0];
		String branchPath = splitIdString[1];
		
		if (splitIdString.length == 3) {
			
			String componentId = splitIdString[2];
			String[] splitComponent = componentId.split("\\|");
			
			if (splitComponent.length > 2) {
				throw new IllegalArgumentException(String.format("Invalid component ID segment [%s], too many segments. The format should be componentId|{memberId}.", componentId));
			}
			
			if (splitComponent.length == 2) {
				return new LogicalId(repositoryId, branchPath, splitComponent[0], splitComponent[1]);
			}
			
			return new LogicalId(repositoryId, branchPath, componentId);
		} else {
			return new LogicalId(repositoryId, branchPath);
		}
		
	}
	
	public boolean isMemberId() {
		return memberId !=null;
	}
	
	public String getRepositoryId() {
		return repositoryId;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public String getComponentId() {
		return componentId;
	}
	
	public String getMemberId() {
		 return memberId;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(repositoryId);
		sb.append(":");
		sb.append(branchPath);
		
		if (componentId !=null) {
			sb.append(":");
			sb.append(componentId);
		}
		
		if (memberId !=null) {
			sb.append("|");
			sb.append(memberId);
		}
		
		return sb.toString();
		
	}

}
