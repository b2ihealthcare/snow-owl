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
package com.b2international.snowowl.server.console;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ApplicationContext.ServiceRegistryEntry;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.server.ServerDbUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

/**
 * OSGI command contribution with Snow Owl maintenance type commands.
 *
 */
public class MaintenanceCommandProvider implements CommandProvider {

	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl commands---\n");
		buffer.append("\tsnowowl checkservices - Checks the core services presence\n");
		buffer.append("\tsnowowl dbcreateindex [nsUri] - creates the CDO_CREATED index on the proper DB tables for all classes contained by a package identified by its unique namespace URI.\n");
		buffer.append("\tsnowowl listrepositories - prints all the repositories in the system.\n");
		buffer.append("\tsnowowl listbranches [repository] - prints all the branches in the system for a repository.\n");
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with "_".
	 * 
	 * @param interpreter
	 */
	public void _snowowl(CommandInterpreter interpreter) {
			String cmd = interpreter.nextArgument();

			if ("checkservices".equals(cmd)) {
				checkServices(interpreter);
				return;
			}

			if ("dbcreateindex".equals(cmd)) {
				executeCreateDbIndex(interpreter);
				return;
			}

			if ("listrepositories".equals(cmd)) {
				listRepositories(interpreter);
				return;
			}

			if ("listbranches".equals(cmd)) {
				listBranches(interpreter);
				return;
			}

			interpreter.println(getHelp());
	}

	public synchronized void executeCreateDbIndex(CommandInterpreter interpreter) {

		String nsUri = interpreter.nextArgument();
		if (null != nsUri) {
			ServerDbUtils.createCdoCreatedIndexOnTables(nsUri);
		} else {
			interpreter.println("Namespace URI should be specified.");
		}
	}

	public synchronized void listRepositories(CommandInterpreter interpreter) {
		ICDORepositoryManager repositoryManager = ApplicationContext.getServiceForClass(ICDORepositoryManager.class);
		Set<String> uuidKeySet = repositoryManager.uuidKeySet();
		if (!uuidKeySet.isEmpty()) {
			interpreter.println("Repositories:");
			for (String repositoryName : uuidKeySet) {
				interpreter.println("  " + repositoryName);
			}
		}
	}

	public synchronized void listBranches(CommandInterpreter interpreter) {
		String repositoryName = interpreter.nextArgument();
		if (isValidRepositoryName(repositoryName, interpreter)) {
			interpreter.println(String.format("Branches for repository %s:", repositoryName));
			Branch mainBranch = RepositoryRequests.branching(repositoryName).prepareGet(IBranchPath.MAIN_BRANCH).executeSync(getEventBus(), 1000);
			
			ArrayList<Branch> allBranches = newArrayList(mainBranch.children());
			allBranches.add(mainBranch);
						
			printBranchHierarchy(allBranches, Sets.<Branch>newHashSet(), mainBranch, interpreter);
		}
	}
	
	private void printBranchHierarchy(List<Branch> branches, Set<Branch> visitedBranches, Branch currentBranch, CommandInterpreter interpreter) {
		interpreter.println(getDepthOfBranch(currentBranch) + "|---" +  currentBranch.name());
		visitedBranches.add(currentBranch);
		for (Branch branch : branches) {
			if (!visitedBranches.contains(branch)) {
				if (branch.parentPath().equals(currentBranch.path())) {
					printBranchHierarchy(branches, visitedBranches, branch, interpreter);
				}
			}
		}
	}

	private String getDepthOfBranch(Branch currentBranch) {
		int depth = Splitter.on(Branch.SEPARATOR).splitToList(currentBranch.path()).size();
		String indent = "";
		for (int i = 1; i < depth; i++) {
			indent = indent + "    ";
		}
		return indent;
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}
	
	public synchronized void checkServices(CommandInterpreter ci) {

		ci.println("Checking core services...");
		try {

			Collection<ServiceRegistryEntry<?>> services = ApplicationContext.getInstance().checkServices();
			for (ServiceRegistryEntry<?> entry : services) {
				ci.println("Interface: " + entry.getServiceInterface() + " : " + entry.getImplementation());
			}
			ci.println("Core services are registered properly and available for use.");

		} catch (final Throwable t) {
			ci.print("Error: " + t.getMessage());
		}
	}

	private boolean isValidRepositoryName(String repositoryName, CommandInterpreter interpreter) {
		if (repositoryName == null) {
			interpreter.println(
					"Repository name should be specified. Execute 'listrepositories' to see the available repositories.");
			return false;
		}

		ICDORepositoryManager repositoryManager = ApplicationContext.getServiceForClass(ICDORepositoryManager.class);
		Set<String> uuidKeySet = repositoryManager.uuidKeySet();
		if (!uuidKeySet.contains(repositoryName)) {
			interpreter.println("Could not find repository called: " + repositoryName);
			interpreter.println("Available repository names are: " + uuidKeySet);
			return false;
		}
		return true;
	}
	
}