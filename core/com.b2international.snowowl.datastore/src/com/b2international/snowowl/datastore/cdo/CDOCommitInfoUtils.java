/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.commons.arrays.Arrays2;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Utility class for reading and handling {@link CDOCommitInfo commit info}.
 *
 */
public abstract class CDOCommitInfoUtils {

	/**
	 * Executes the given commit info query and handles each of them on the specified {@link CDOCommitInfoHandler commit handler}. 
	 * @param query the query to execute.
	 * @param handler the handler.
	 */
	public static void getCommitInfos(final CDOCommitInfoQuery query, final CDOCommitInfoHandler handler) {
		Collections3.forEach(query.execute(), new Procedure<CDOCommitInfo>() {
			@Override protected void doApply(final CDOCommitInfo commitInfo) {
				handler.handleCommitInfo(commitInfo);
			}
		});
	}
	
	/**
	 * Sugar for getting the UUID from the commit info.
	 * <br>Same as {@code #getUuid(CDOCommitInfo#getComment())}.
	 * @see #getUuid(String)
	 */
	public static String getUuid(final CDOCommitInfo commitInfo) {
		Preconditions.checkNotNull(commitInfo, "Commit info argument cannot be null.");
		if (commitInfo instanceof ICDOCommitInfoWithUuid) {
			return ((ICDOCommitInfoWithUuid) commitInfo).getUuid();
		}
		return getUuid(Strings.nullToEmpty(commitInfo.getComment()));
	}
	
	/**
	 * Returns with the leading UUID from the commit comment if any.
	 * If no leading UUID can be extracted from the comment, then this method generates one
	 * and returns with it.
	 * @param comment the commit comment.
	 * @return a UUID identifying a commit comment.
	 */
	public static String getUuid(@Nullable final String comment) {
		
		final String _comment = Strings.nullToEmpty(comment);
		final Matcher matcher = CDOCommitInfoConstants.UUID_PATETRN.matcher(_comment);
		
		String commitId = null;
		
		while (matcher.find()) {
			
			commitId = matcher.group(0);
			break;
			
		}
		
		if (StringUtils.isEmpty(commitId)) {
			commitId = UUID.randomUUID().toString();
		}
		return commitId;
	}
	
	/**
	 * Returns with {@code true} if the argument can referenced and 
	 * the commit info does NOT represents a failure commit into, hence
	 * the {@link CDOCommitInfo#getBranch()} can be referenced as well.
	 * Otherwise returns with {@code false}.
	 * @param commitInfo the commit info to check.
	 * @return {@code true} if the argument can be referenced, otherwise {@code false}.
	 */
	public static boolean check(final CDOCommitInfo commitInfo) {
		return null != commitInfo && null != commitInfo.getBranch();
	} 
	
	/**
	 * Returns with a new CDO commit info instance after removing the leading UUID from the commit comment, if any.
	 * <br>This method will return with {@code null} if the commit info argument is {@code null}. 
	 * @param commitInfo the commit info to modify. Can be {@code null}.
	 * @return a new commit info instance with a modified {@link CDOCommitInfo#getComment() comment}.
	 */
	@SuppressWarnings("restriction")
	@Nullable public static CDOCommitInfo removeUuidFromComment(@Nullable final CDOCommitInfo commitInfo) {
		
		if (null == commitInfo) {
			return null;
		}
		
		if (commitInfo instanceof FailureCommitInfo) {
			return commitInfo;
		}
		
		return new org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo() {
			@Override protected CDOCommitInfo getDelegate() {
				return commitInfo;
			}
			@Override public String getComment() {
				return removeUuidPrefix(Strings.nullToEmpty(commitInfo.getComment()));
			}
		};
		
	}
	
	/**
	 * Returns with the repository UUID where the {@link CDOCommitInfo commit info} belongs to.
	 * @param info the commit info.
	 * @return the repository UUID or {@code null} if the commit info represents a failure commit info.
	 */
	@Nullable public static String getRepositoryUuid(final CDOCommitInfo info) {
		final CDOBranch branch = checkNotNull(info, "info").getBranch();
		if (null == branch) {
			return null;
		}
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		return connectionManager.get(branch).getUuid();
	}
	
	/**
	 * Same behavior as {@link #extractUuidPrefix(String, false)}.
	 */
	public static String removeUuidPrefix(final String prefixedMessage) {
		return removeUuidPrefix(prefixedMessage, false);
	}
	
	/**Extracts and removes the leading version 4 UUID if any from the prefixed string argument.
	 *Returns with the modified argument. This method might ignore extracting the leading UUID
	 *on the specified prefixed message argument if {@code failFast} is {@code false}. If {@code failFast} 
	 *is {@code true} and the message argument does not contain a leading UUID, then this method throws a runtime
	 *exception.*/
	@Nullable public static String removeUuidPrefix(final String prefixedMessage, final boolean failFast) {
		Preconditions.checkNotNull(prefixedMessage, "Prefixed message argument cannot be null.");
		final String[] split = CDOCommitInfoConstants.UUID_PATETRN.split(prefixedMessage);
		
		//these are the use cases
		//1) UUID + "some text"
		//2) UUID + ""
		//3) UUID + null
		//4) UUID
		//5) "not a UUID"
		
		//split was successful, hence first part is a valid UUID
		if (!CompareUtils.isEmpty(split)) {
			
			if (1 == split.length) { //stinky use case, assuming a non UUID, consider use case nr. 5
				if (failFast) {
					throw new IllegalArgumentException("Failed to extract initial UUID from the argument: '" + prefixedMessage + "'.");
				} else {
					return Strings.nullToEmpty(split[0]);
				}
			}
			
			return Strings.nullToEmpty(split[1]); //null safe, consider 3. use case
		}
		
		final boolean matches = CDOCommitInfoConstants.UUID_PATETRN.matcher(prefixedMessage).matches();
		
		if (matches) { //use case for empty string and nothing, which is basically same as empty string
			return Strings.nullToEmpty(null); //guava Strings already internalized the string. String#intern()
		}
	
		if (failFast) {
			throw new IllegalArgumentException("Failed to extract initial UUID from the argument: '" + prefixedMessage + "'.");
		} else {
			return Strings.nullToEmpty(prefixedMessage);
		}
		
	}

	/**
	 * Creates an {@link EmptyCDOCommitInfo empty commit info}.
	 * @param repositoryUuid the repository UUID.
	 * @param branchPath the branch path.
	 * @param userId the unique user ID.
	 * @param comment the commit comment.
	 * @param timestamp the commit timestamp.
	 * @param previousTimestamp the commit previous timestamp.
	 * @return with an empty commit info.
	 */
	public static CDOCommitInfo createEmptyCommitInfo(final String repositoryUuid, final IBranchPath branchPath, final String userId, 
			final String comment, final long timestamp, final long previousTimestamp) {
		
		Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(userId, "User ID argument cannot be null.");
		Preconditions.checkNotNull(comment, "Commit comment argument cannot be null.");
		
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = connectionManager.getByUuid(repositoryUuid);
		final CDOBranch branch = connection.getBranch(branchPath);
		
		Preconditions.checkNotNull(branch, "Branch [" + branchPath + "] does not exist in " + connection.getRepositoryName() + " repository.");
		
		return new EmptyCDOCommitInfo(branch, userId, comment, timestamp, previousTimestamp);
	}
	
	/**
	 * Query for getting and processing {@link CDOCommitInfo commit info}.
	 */
	public static final class CDOCommitInfoQuery {
		
		public static final Collection<String> EXCLUDED_USERS = 
				ImmutableSet.of(CDOCommitInfoConstants.SYSTEM_USER_ID);
		
		private static final Predicate<String> EXCLUDED_USERS_PREDICATE = Predicates.not(Predicates.in(EXCLUDED_USERS));
	
		private final ConsumeAllCommitInfoHandler delegateHandler;
		private final Map<String, IBranchPath> branchPathMap;

		private long startTime = CDOBranchPoint.UNSPECIFIED_DATE;
		private long endTime = CDOBranchPoint.UNSPECIFIED_DATE;
		private boolean enableGrouping;
		private Predicate<ICDOConnection> connectionPredicate = Predicates.alwaysTrue();
		private Predicate<String> excludedUsersPredicate = EXCLUDED_USERS_PREDICATE;
		private boolean traverseAncestorBranches;
		
		/**
		 * Creates a CDO commit info query working on the given branch.
		 * @param branchPathMap the branch paths (keyed by repository UUID) to work with
		 */
		public CDOCommitInfoQuery(final Map<String, IBranchPath> branchPathMap) {
			this.branchPathMap = branchPathMap;
			delegateHandler = new ConsumeAllCommitInfoHandler();
		}
		
		/**Set the start time to the specified value.*/
		public CDOCommitInfoQuery setStartTime(final long startTime) {
			this.startTime = startTime;
			return this;
		}
		
		/**Sets the end time of the query to the specified value.*/
		public CDOCommitInfoQuery setEndTime(final long endTime) {
			this.endTime = endTime;
			return this;
		}
		
		/**Sets the predicate on the {@link ICDOConnection connection}s.*/
		public CDOCommitInfoQuery setConnectionPredicate(final Predicate<ICDOConnection> connectionPredicate) {
			this.connectionPredicate = Preconditions.checkNotNull(connectionPredicate);
			return this;
		}
		
		/**Enables or disables commit info grouping.*/
		public CDOCommitInfoQuery setEnableGrouping(final boolean enableGrouping) {
			this.enableGrouping = enableGrouping;
			return this;
		}
		
		/**Specifies the excluded user ID predicate.*/
		public CDOCommitInfoQuery setExcludedUsersPredicate(final Predicate<String> excludedUsersPredicate) {
			this.excludedUsersPredicate = Preconditions.checkNotNull(excludedUsersPredicate);
			return this;
		}
		
		/**{@code true} if commit info has to be retrieved from the ancestor branches as well. Otherwise {@code false}.*/
		public CDOCommitInfoQuery setTraverseAncestorBranches(final boolean traverseAncestorBranches) {
			this.traverseAncestorBranches = traverseAncestorBranches;
			return this;
		}
		
		/*performs the query. returns with an iterator for the results.*/
		@SuppressWarnings("restriction")
		private synchronized Iterator<CDOCommitInfo> execute() {
			
			for (final ICDOConnection connection : ApplicationContext.getInstance().getService(ICDOConnectionManager.class)) {
				
				if (connectionPredicate.apply(connection)) {
					
					final IBranchPath branchPath = branchPathMap.get(connection.getUuid());
					
					long _endTime = endTime;
					
					if (null != branchPath) { //if commit info query does not work on all available repositories but a subset of them
					
						CDOBranch branch = connection.getBranch(branchPath);
						
						if (null != branch) { //omit non existing branches
							
							do  {
								
								final CDONet4jSession session = connection.getSession();
								final CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
								commitInfoManager.getCommitInfos(branch, startTime, _endTime, delegateHandler);

								_endTime = branch.getBase().getTimeStamp();
								branch = branch.getBase().getBranch(); //null if reached repository creation time
								
							} while (traverseAncestorBranches && null != branch);
							
						}
						
					}
					
				}
				
			}
			
			final int expectedSize = delegateHandler.getInfos().size();
			if (!enableGrouping) {
				
				final CDOCommitInfo[] $ = new CDOCommitInfo[expectedSize];
				
				final AtomicInteger i = new AtomicInteger();
				Collections3.forEach(delegateHandler.getInfos(), new Procedure<CDOCommitInfo>() {
					@Override protected void doApply(final CDOCommitInfo info) {
						
						final String userId = info.getUserID();
						if (excludedUsersPredicate.apply(userId)) {
							$[i.getAndIncrement()] = info;
						}
						
					}
				});
				
				return Arrays2.iterator(Arrays.copyOf($, i.get()));
				
			} else {
				
				//we need to store the commit associated to a logical group with the least commit timestamp  
				final Map<String, Pair<CDOCommitInfo, Long>> unsortedInfos = Maps.newHashMapWithExpectedSize(expectedSize);
				for (final CDOCommitInfo info : delegateHandler.getInfos()) {

					final String userId = info.getUserID();
					//this should filter out 'logically grouped ones'
					final CDOCommitInfoWithUuid wrappedInfo = new CDOCommitInfoWithUuid(info);
					final String uuid = wrappedInfo.getUuid();
					
					if (excludedUsersPredicate.apply(userId)) {
						if (unsortedInfos.containsKey(uuid)) {
							
							//replace the visited commit info with the current one if the timestamp is smaller.
							if (unsortedInfos.get(uuid).getB().longValue() > wrappedInfo.getTimeStamp()) {
								unsortedInfos.put(uuid, Pair.<CDOCommitInfo, Long>of(wrappedInfo, wrappedInfo.getTimeStamp()));
							}
							
						} else {
							
							unsortedInfos.put(uuid, Pair.<CDOCommitInfo, Long>of(wrappedInfo, wrappedInfo.getTimeStamp()));
							
						}
					}
					
				}
				
				//filter out all commit info instances which are associated with the terminology metadata store after grouping
				final Iterable<Pair<CDOCommitInfo, Long>> filteredCommitInfo = Iterables.filter(unsortedInfos.values(), new Predicate<Pair<CDOCommitInfo, Long>>() {
					@Override public boolean apply(final Pair<CDOCommitInfo, Long> pair) {
						final SerializableCDOCommitInfo $ = (SerializableCDOCommitInfo) CDOCommitInfoConstants.TO_SERIALIZABLE_FUNCTION.apply(pair.getA());
						return !StringUtils.isEmpty($.getToolingId());
					}
				});
				
				//ensure ordered result
				final Set<CDOCommitInfo> $ = new TreeSet<CDOCommitInfo>(CDOCommitInfoConstants.CDO_COMMIT_INFO_COMPARATOR);
				$.addAll(Sets.newHashSet(Iterables.transform(filteredCommitInfo, new Function<Pair<CDOCommitInfo, Long>, CDOCommitInfo>() {
					@Override public CDOCommitInfo apply(final Pair<CDOCommitInfo, Long> pair) {
						return pair.getA();
					}
				})));
				
				return $.iterator();
				
			}
			
		}
		
	}
	
	/**
	 * Commit info handler for consuming each {@link CDOCommitInfo} read from the repository and storing
	 * them in a backing array list. This handler consumes each commit info but the {@code null} ones.
	 */
	public static final class ConsumeAllCommitInfoHandler implements CDOCommitInfoHandler {

		private final Collection<CDOCommitInfo> infos = Lists.newArrayList();
		
		/* (non-Javadoc)
		 * @see org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler#handleCommitInfo(org.eclipse.emf.cdo.common.commit.CDOCommitInfo)
		 */
		@Override
		public void handleCommitInfo(final CDOCommitInfo commitInfo) {
			if (null != commitInfo) {
				infos.add(commitInfo);
			}
		}
		
		/**
		 * Returns with a view of the commit info instances. 
		 */
		public List<CDOCommitInfo> getInfos() {
			return Collections.unmodifiableList((List<? extends CDOCommitInfo>) infos);
		}
		
	}
	
	public static final class ConsumeAllCDOBranchesHandler implements CDOBranchHandler {

		private final Collection<CDOBranch> infos = Lists.newArrayList();
		
		@Override
		public void handleBranch(CDOBranch branch) {
			if (branch != null) {
				infos.add(branch);
			}
		}

		public List<CDOBranch> getBranches() {
			return Collections.unmodifiableList((List<? extends CDOBranch>) infos);
		}
		
	}
	
}