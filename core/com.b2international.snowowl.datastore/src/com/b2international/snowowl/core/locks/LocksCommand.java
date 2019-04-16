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
package com.b2international.snowowl.core.locks;

import java.util.List;

import com.b2international.commons.StringUtils;
import com.b2international.commons.extension.Component;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineStream;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
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
import com.b2international.snowowl.identity.domain.User;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import picocli.CommandLine;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @since 7.0
 */
@Component
@CommandLine.Command(
	name = "locks",
	header = "Display and manage locks",
	description = "Displays locks currently present in the system and offers subcommands to acquire/release them.",
	subcommands = {
		HelpCommand.class,
		LocksCommand.AddLockCommand.class,
		LocksCommand.RemoveLockCommand.class
	}
)
public final class LocksCommand extends Command {

	private static final String COLUMN_FORMAT = "%4s | %3s | %-16s | %-50s | %-50s";
	private static final String ALL = "ALL";
	private static final DatastoreLockContext CONSOLE_CONTEXT = new DatastoreLockContext(User.SYSTEM.getUsername(), DatastoreLockContextDescriptions.MAINTENANCE);
	
	@Override
	public void run(CommandLineStream out) {
		final IDatastoreOperationLockManager lockManager = ApplicationContext.getInstance().getService(IDatastoreOperationLockManager.class);
		final List<OperationLockInfo<DatastoreLockContext>> locks = ((DatastoreOperationLockManager) lockManager).getLocks();
		
		if (locks.isEmpty()) {
			out.println("No locks are currently granted on this server.");
			return;
		}
		
		out.println();
		out.println(COLUMN_FORMAT, "Id", "Lvl", "Created on", "Locked area", "Owner context");
		out.println(Strings.repeat("-", 135));
		
		for (final OperationLockInfo<DatastoreLockContext> lockEntry : locks) {
			out.println(String.format(COLUMN_FORMAT, 
					lockEntry.getId(),
					lockEntry.getLevel(),
					Dates.formatByHostTimeZone(lockEntry.getCreationDate(), DateFormats.MEDIUM),
					StringUtils.truncate(StringUtils.capitalizeFirstLetter(lockEntry.getTarget().toString()), 50),
					StringUtils.truncate("Lock owner: " +lockEntry.getContext().getUserId(), 50)));
			
			out.println(String.format(COLUMN_FORMAT, "", "", "", "",
					StringUtils.truncate(StringUtils.capitalizeFirstLetter(lockEntry.getContext().getDescription()), 50)));
			
			out.println(Strings.repeat("-", 135));
		}
	}
	
	@CommandLine.Command(
		name = "add",
		header = "Locks a resource",
		description = "Locks the specified resource in the system making it read-only for other users"
	)
	public static final class AddLockCommand extends Command {

		@Parameters(paramLabel = "REPOSITORY[:BRANCH]|ALL", description = "The repository and optionally a branch to acquire lock for. 'ALL' value can be used to acquire lock for all available repositories.")
		String repositoryOrAll;
		
		@Override
		public void run(CommandLineStream out) {
			final IOperationLockTarget target = parseLockTarget(repositoryOrAll); 
			
			if (null == target) {
				out.println("Couldn't find resource '%s' to acquire lock for.", repositoryOrAll);
				return;
			}
			
			final IDatastoreOperationLockManager lockManager = getLockManager();
			final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), DatastoreLockContextDescriptions.MAINTENANCE);
			
			try {
				lockManager.lock(context, 3000L, target);
				out.println("Acquired lock for %s.", target);
			} catch (final OperationLockException | InterruptedException e) {
				out.println(e);
			}
		}
		
	}
	
	@CommandLine.Command(
		name = "remove",
		header = "Unlocks a resource",
		description = "Unlocks the specified resource in the system making it both writeable and readable for other users"
	)
	public static final class RemoveLockCommand extends Command {

		@Parameters(paramLabel = "REPOSITORY[:BRANCH]|LOCK_ID|ALL", description = "The lock target (repository and optional branch) or ID release. 'ALL' value can be used to release all currently acquired locks.")
		String lockTargetOrLockIdOrAll;
		
		@Option(names = { "-f", "--force" }, required = false, description = "To forcefully release the lock when it cannot be released in the normal way.")
		boolean force;
		
		@Override
		public void run(CommandLineStream out) {
			// first try to parse it as LOCK ID and release if succeeded
			Integer lockId = Ints.tryParse(lockTargetOrLockIdOrAll);
			IOperationLockTarget target = null;
			if (lockId == null) {
				// then try to parse it as lock target
				target = parseLockTarget(lockTargetOrLockIdOrAll);
				if (null == target) {
					out.println("Couldn't find resource '%s' to release the lock from.", lockTargetOrLockIdOrAll);
					return;
				}
			}

			try {
				if (lockId != null) {
					getLockManager().unlockById(lockId);
					out.println("Released lock by ID '%s'.", lockId);
				} else if (target == AllRepositoriesLockTarget.INSTANCE) {
					getLockManager().unlockAll();
					out.println("Released ALL locks.");
				} else {
					getLockManager().unlock(CONSOLE_CONTEXT, target);
					out.println("Released lock previously acquired for resource '%s'.", target);
				}
			} catch (final OperationLockException e) {
				out.print(e);
			}
		}
		
	}

	private static DatastoreOperationLockManager getLockManager() {
		return (DatastoreOperationLockManager) ApplicationContext.getInstance().getService(IDatastoreOperationLockManager.class);
	}
	
	private static IOperationLockTarget parseLockTarget(final String lockTargetOrAll) {
		if (ALL.equalsIgnoreCase(lockTargetOrAll)) {
			return AllRepositoriesLockTarget.INSTANCE;
		}
		
		final String repositoryId;
		final String path;
		
		String[] parts = lockTargetOrAll.split(":");
		if (parts.length == 2) {
			repositoryId = parts[0];
			path = parts[1];
		} else if (parts.length == 1) {
			repositoryId = parts[0];
			path = null;
		} else {
			return null;
		}

		final RepositoryManager repositoryManager = ApplicationContext.getInstance().getService(RepositoryManager.class);
		final Repository repository = repositoryManager.get(repositoryId);
		
		if (null == repository) {
			return null;
		}
		
		if (Strings.isNullOrEmpty(path)) {
			return new SingleRepositoryLockTarget(repositoryId);
		}
		
		IBranchPath branchPath = BranchPathUtils.createPath(path);
		//assuming active connection manager service here
		
		RevisionBranch branch = repository.service(BaseRevisionBranching.class).getBranch(path);
		if (null == branch) {
			return null;
		}
		
		return new SingleRepositoryAndBranchLockTarget(repositoryId, branchPath);
	}
	
}
