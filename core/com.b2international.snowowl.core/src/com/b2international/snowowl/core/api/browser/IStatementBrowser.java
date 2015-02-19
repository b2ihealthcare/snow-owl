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
package com.b2international.snowowl.core.api.browser;

import java.util.List;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Interface for providing statements related to concepts on the server side.
 * 
 * @param <C> the concept type
 * @param <S> the statement type
 * @param <K> the statement identifier type
 */
public interface IStatementBrowser<C, S, K> {
	
	/**
	 * Returns the list of inbound statements for the specified concept.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param concept
	 * @return the list of inbound statements for the specified concept
	 */
	List<S> getInboundStatements(final IBranchPath branchPath, final C concept);
	
	/**
	 * Returns the list of outbound statements for the specified concept.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param concept
	 * @return the list of outbound statements for the specified concept
	 */
	List<S> getOutboundStatements(final IBranchPath branchPath, final C concept);
	
	/**
	 * Returns the list of all statements for a specified concept identified by its ID.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param conceptId the unique ID of the concept.
	 * @return the list of all statements for the specified concept
	 */
	List<S> getStatementsById(final IBranchPath branchPath, final K conceptId);
	
	/**
	 * Returns the list of inbound statements for a specified concept identified by its unique ID.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param conceptId the unique ID of a concept.
	 * @return the list of inbound statements for the specified concept
	 */
	List<S> getInboundStatementsById(final IBranchPath branchPath, final K conceptId);
	
	/**
	 * Returns the list of outbound statements for a specified concept identified by its unique ID.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param conceptId the unique ID of a concept.
	 * @return the list of outbound statements for the specified concept
	 */
	List<S> getOutboundStatementsById(final IBranchPath branchPath, final K conceptId);
	
	/**
	 * Returns the list of all statements for the specified concept.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param concept
	 * @return the list of all statements for the specified concept
	 */
	List<S> getStatements(final IBranchPath branchPath, final C concept);
	
	/**
	 * Returns all known statements. 
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @return all known statements
	 */
	List<S> getAllStatements(final IBranchPath branchPath);

	/**
	 * Returns the statement with the specified unique identifier.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param id
	 * @return the statement with the specified unique identifier
	 */
	S getStatement(final IBranchPath branchPath, final K id);
}