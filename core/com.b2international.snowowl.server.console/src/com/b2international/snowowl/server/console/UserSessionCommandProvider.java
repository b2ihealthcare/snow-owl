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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager.ISessionOperationCallback;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.OperationLockInfo;
import com.b2international.snowowl.datastore.oplock.impl.AllRepositoriesLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryLockTarget;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.identity.domain.User;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

/**
 * OSGI command contribution with Snow Owl commands to manage user sessions.
 * 
 */
public class UserSessionCommandProvider implements CommandProvider {

	private static Logger USER_ACTIVITY_LOGGER = LoggerFactory.getLogger(UserSessionCommandProvider.class);
	
	@Override
	public String getHelp() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl user session commands---\n");
		buffer.append("\tsession users - List the users that are currently logged in\n");
		buffer.append("\tsession message [user1Name,userName2,userNameN|ALL] [message] - Send message to active user(s) or all users. Do not use space between the users.\n");
		buffer.append("\tsession disconnect [userName1,userName2,userNameN|ALL] - Disconnect active user(s) or all users.  Do not use space between the users.\n");
		buffer.append("\tsession login [enabled|disabled|status] - Enables/disables login for new, non-administrator sessions.\n");
		buffer.append("\tsession showlocks - Displays all currently acquired locks on the server.\n");
		buffer.append("\tsession lock [allRepositories|repositoryUuid|repositoryUuid branchPath] - Acquires a lock in repository on the specified branch.\n");
		buffer.append("\tsession unlock [allRepositories|repositoryUuid|repositoryUuid branchPath] - Releases a lock in the repository on the specified branch.\n");
		buffer.append("\tsession forceunlock [all|lock identifier] - Forcefully releases the specified locks.\n");
		buffer.append("\tsession repositories - Lists all available repositories.\n");
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with
	 * "_".
	 * 
	 * @param interpreter
	 */
	public void _session(final CommandInterpreter interpreter) {

		try {
			
			final String cmd = Strings.nullToEmpty(interpreter.nextArgument());
			
			switch (cmd) {
				case "users":
					users(interpreter);
					break;
				case "disconnect":
					disconnect(interpreter);
					break;
				case "message":
					message(interpreter);
					break;
				case "login":
					login(interpreter);
					break;
				case "lock":
					lock(interpreter);
					break;
				case "unlock":
					unlock(interpreter);
					break;
				case "showlocks":
					showLocks(interpreter);
					break;
				case "forceunlock":
					forceUnlock(interpreter);
					break;
				default:
					interpreter.println(getHelp());
					break;
			}

		} catch (final Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}

	/**
	 * List the active users of the session.
	 * 
	 * @param interpreter
	 */
	public synchronized void users(final CommandInterpreter interpreter) {
		
		final List<Pair<String, String>> info = newArrayList(ApplicationContext.getInstance().getService(IApplicationSessionManager.class).getConnectedSessionInfo());
		
		if (CompareUtils.isEmpty(info)) {
			
			interpreter.println("No users are connected to the server.");
			
		} else {
		
			Collections.sort(info, new Comparator<Pair<String, String>>() {
				@Override public int compare(final Pair<String, String> o1, final Pair<String, String> o2) {
					return Strings.nullToEmpty(o1.getA()).compareTo(Strings.nullToEmpty(o2.getA()));
				}
			});
			
			for (final Pair<String, String> pair : info) {
					interpreter.println("User: " + pair.getA() + " | session ID: " + pair.getB());
			}
			
		}

	}

	/**
	 * Sends a message to all the users.
	 * 
	 * @param interpreter
	 */
	public synchronized void message(final CommandInterpreter interpreter) {

		final String usage = "Command usage: session message [user1Name,userName2,userNameN|ALL] [message]";
		final String userListParameter = interpreter.nextArgument();
		if (StringUtils.isEmpty(userListParameter)) {
			interpreter.println("User is null.");
			interpreter.print(usage);
			return;
		}
		
		final String messageBody = interpreter.nextArgument();
		if (StringUtils.isEmpty(messageBody)) {
			interpreter.println("Message is null.");
			interpreter.print(usage);
			return;
		}
		
		final StringBuilder sb = new StringBuilder(messageBody);
		sb.append(' ');
		while (true) {
			
			final String messageFragment = interpreter.nextArgument();
			if (StringUtils.isEmpty(messageFragment)) { 
				break;
			} else {
				sb.append(messageFragment);
				sb.append(' ');
			}
		}
		
		final AtomicBoolean success = new AtomicBoolean(false);
		final ICDORepositoryManager repositoryManager = ApplicationContext.getInstance().getService(ICDORepositoryManager.class);

		final String message = sb.toString();
		
		final ISessionOperationCallback callback = new ISessionOperationCallback() {
			
			@Override public void done(final CDORemoteSession session) {
				interpreter.println("Message sent to " + session.getUserID());
				success.compareAndSet(false, true);
			}
		};

		if ("ALL".equals(userListParameter)) {
			
			repositoryManager.sendMessageToAll(message, callback);
			
		} else {
			
			repositoryManager.sendMessageTo(message, tokenizeParameter(userListParameter), callback);
			
		}

		if (!success.get()) {
			interpreter.println("Failed to message user(s): " + userListParameter
					+ ". Are these active users? Currently active users are:\n");
			users(interpreter);
		}
	}
	
	/**
	 * Disconnect the users.
	 * 
	 * @param interpreter
	 */
	public synchronized void disconnect(final CommandInterpreter interpreter) {
		final String userNamesParameter = interpreter.nextArgument();

		if (StringUtils.isEmpty(userNamesParameter)) {
			interpreter.print("Command usage: session disconnect [userName1,userName2,userNameN|ALL]");
			return;
		}

		final AtomicBoolean success = new AtomicBoolean(false);
		final ICDORepositoryManager repositoryManager = ApplicationContext.getInstance().getService(ICDORepositoryManager.class);

		final ISessionOperationCallback callback = new ISessionOperationCallback() {
			
			@Override public void done(final CDORemoteSession session) {
				interpreter.println("User: " + session.getUserID() + " | session ID: " + session.getSessionID() + " was disconnected.");
				LogUtils.logUserEvent(USER_ACTIVITY_LOGGER, "admin", "Disconnected user: " + session.getUserID() + " from session: " + session.getSessionID() + ".");
				success.compareAndSet(false, true);
			}
		};

		if ("ALL".equals(userNamesParameter)) {
			
			repositoryManager.disconnectAll(callback);
			
		} else {
			
			repositoryManager.disconnect(tokenizeParameter(userNamesParameter), callback);
			
		}

		if (!success.get()) {
			interpreter.println("Failed to disconnect user(s): " + userNamesParameter + ". Are these users active? Currently active users are:\n");
			users(interpreter);
		}
	}

	private static final List<String> ALLOWED_SUBCOMMANDS = Arrays.asList("enabled", "disabled", "status"); 
	
	public synchronized void login(final CommandInterpreter interpreter) {

		final String subCommand = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(subCommand) || !ALLOWED_SUBCOMMANDS.contains(subCommand.toLowerCase())) {
			interpreter.print("Command usage: session login [enabled|disabled|status]");
			return;
		}
		
		IApplicationSessionManager applicationSessionManager = ApplicationContext.getInstance().getService(IApplicationSessionManager.class);
		if (subCommand.equalsIgnoreCase("status")) {
			interpreter.println(MessageFormat.format("Non-administrative logins are currently {0}.", (applicationSessionManager.isLoginEnabled() ? "enabled" : "disabled")));
			return;
		}

		final boolean loginEnabled = subCommand.equalsIgnoreCase("enabled");
		applicationSessionManager.enableLogins(loginEnabled);
		interpreter.println(MessageFormat.format("{0} non-administrative logins.", (loginEnabled ? "Enabled" : "Disabled")));
	}

	/*
	 * There should be no spaces present in the input string.
	 */
	private List<String> tokenizeParameter(final String userName) {
		final List<String> userList = new ArrayList<String>();
		
		final StringTokenizer st = new StringTokenizer(userName, ",");
		while (st.hasMoreElements()) {
			userList.add(st.nextToken());
		}
		return userList;
	}

	private static final String COLUMN_FORMAT = "%4s | %3s | %-16s | %-50s | %-50s";

	private synchronized void showLocks(final CommandInterpreter interpreter) {
	
		final IDatastoreOperationLockManager lockManager = ApplicationContext.getInstance().getService(IDatastoreOperationLockManager.class);
		final List<OperationLockInfo<DatastoreLockContext>> locks = ((DatastoreOperationLockManager) lockManager).getLocks();
		
		if (locks.isEmpty()) {
			interpreter.println("No locks are currently granted on this server.");
			return;
		}
		
		interpreter.println();
		interpreter.println(String.format(COLUMN_FORMAT, "Id", "Lvl", "Created on", "Locked area", "Owner context"));
		interpreter.println(Strings.repeat("-", 135));
		
		for (final OperationLockInfo<DatastoreLockContext> lockEntry : locks) {
			interpreter.println(String.format(COLUMN_FORMAT, 
					lockEntry.getId(),
					lockEntry.getLevel(),
					Dates.formatByHostTimeZone(lockEntry.getCreationDate(), DateFormats.MEDIUM),
					StringUtils.truncate(StringUtils.capitalizeFirstLetter(lockEntry.getTarget().toString()), 50),
					StringUtils.truncate("Lock owner: " +lockEntry.getContext().getUserId(), 50)));
			
			interpreter.println(String.format(COLUMN_FORMAT, "", "", "", "",
					StringUtils.truncate(StringUtils.capitalizeFirstLetter(lockEntry.getContext().getDescription()), 50)));
			
			interpreter.println(Strings.repeat("-", 135));
		}
		
	}
	
	private synchronized void forceUnlock(final CommandInterpreter interpreter) {
		
		final DatastoreOperationLockManager lockManager = (DatastoreOperationLockManager) ApplicationContext.getInstance().getService(IDatastoreOperationLockManager.class);
		final String argument = interpreter.nextArgument();
		
		if (null == argument) {
			interpreter.println("Missing parameter. Usage: forceunlock [all|lock identifier]");
			return;
		}
		
		if ("all".equalsIgnoreCase(argument)) {
			lockManager.unlockAll();
			interpreter.println("Forcefully released all acquired locks.");
			return;
		}
		
		final Integer parsedLockId = Ints.tryParse(argument);
		
		if (null != parsedLockId) {
			
			if (lockManager.unlockById(parsedLockId)) {
				interpreter.println("Forcefully released lock with identifier " + parsedLockId + ".");
			} else {
				interpreter.println("Lock with identifier " + parsedLockId + " could not be found.");
			}
			
		} else {
			interpreter.println("Lock identifier is not an interger. Usage: forceunlock [all|lock identifier]");
		}
	}
	
	private synchronized void lock(final CommandInterpreter interpreter) {
		
		final IOperationLockTarget target = parseLockTarget(interpreter); 
		
		if (null == target) {
			return;
		}
		
		final IDatastoreOperationLockManager lockManager = ApplicationContext.getInstance().getService(IDatastoreOperationLockManager.class);
		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), DatastoreLockContextDescriptions.MAINTENANCE);
		
		try {
			lockManager.lock(context, 3000L, target);
		} catch (final OperationLockException | InterruptedException e) {
			interpreter.println(e);
			return;
		}
		
		interpreter.println("Acquired lock for " + target + ".");
	}
	
	private synchronized void unlock(final CommandInterpreter interpreter) {
		
		final IOperationLockTarget target = parseLockTarget(interpreter);
		
		if (null == target) {
			return;
		}

		final IDatastoreOperationLockManager lockManager = ApplicationContext.getInstance().getService(IDatastoreOperationLockManager.class);
		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), DatastoreLockContextDescriptions.MAINTENANCE);

		try {
			lockManager.unlock(context, target);
		} catch (final OperationLockException e) {
			interpreter.print(e);
			return;
		}
		
		interpreter.println("Released lock for " + target + ".");
	}
	
	private IOperationLockTarget parseLockTarget(final CommandInterpreter interpreter) {
		
		final String uuidOrAll = interpreter.nextArgument();

		if (StringUtils.isEmpty(uuidOrAll)) {
			interpreter.println("Repository UUID or \"allRepositories\" should be specified.");
			interpreter.println(getHelp());
			return null;
		}
		
		if ("allRepositories".equalsIgnoreCase(uuidOrAll)) {
			return AllRepositoriesLockTarget.INSTANCE;
		}
		
		final RepositoryManager repositoryManager = ApplicationContext.getInstance().getService(RepositoryManager.class);
		final Repository repository = repositoryManager.get(uuidOrAll);
		if (null == repository) {
			
			interpreter.println("Repository does not exist with UUID: '" + uuidOrAll + "'.");
			interpreter.println("Available stores are the followings:");
			interpreter.println("------------------------------------");
			for (final Repository r : repositoryManager.repositories()) {
				interpreter.println("\t" + r.id());
			}
			interpreter.println("------------------------------------");
			
			interpreter.println(getHelp());
			return null;
		}
		
		final String path = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(path)) {
			return new SingleRepositoryLockTarget(uuidOrAll);
		}
		
		IBranchPath branchPath = null;
		
		try {

			branchPath = BranchPathUtils.createPath(path);
			//assuming active connection manager service here
			
			RevisionBranch branch = repository.service(BaseRevisionBranching.class).getBranch(path);
			if (null == branch) {
				interpreter.println("Branch does not exist. Branch path: '" + branchPath + "'. Repository UUID: '" + uuidOrAll + "'.");
				interpreter.println(getHelp());
				return null;
			}
			
		} catch (final Throwable t) {
			interpreter.println("Branch does not exist. Branch path: '" + path + "'. Repository UUID: '" + uuidOrAll + "'.");
			interpreter.println(getHelp());
			return null;
		}
		
		if (null == branchPath) {
			return null;
		}
		
		return new SingleRepositoryAndBranchLockTarget(uuidOrAll, branchPath);
	}
}