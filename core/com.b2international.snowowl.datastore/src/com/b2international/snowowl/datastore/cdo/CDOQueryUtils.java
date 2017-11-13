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
package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Suppliers.memoize;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.internal.server.ServerCDOView;
import org.eclipse.emf.cdo.internal.server.ServerCDOView.ServerCDOSession;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.SQLQueryHandler;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.internal.cdo.query.CDOQueryCDOIDIteratorImpl;
import org.eclipse.emf.internal.cdo.query.CDOQueryResultIteratorImpl;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;

import com.b2international.commons.StringUtils;
import com.b2international.commons.TokenReplacer;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Contains utility methods for handling CDO queries. 
 */
@SuppressWarnings("restriction")
public abstract class CDOQueryUtils {

	/**
	 * CDO's SQL query handler parameter for returning non-CDO object results
	 * from a query. This constant is preferable to the original defined in
	 * {@link SQLQueryHandler}, as the other one is considered internal and
	 * produces compiler warnings.
	 */
	public static final String CDO_OBJECT_QUERY = org.eclipse.emf.cdo.server.internal.db.SQLQueryHandler.CDO_OBJECT_QUERY;

	private static final String BRANCH_AWARE_PREFIX = "{visibleFromBranch";
	
	private static boolean isBranchAware(final CDOQuery query) {
		return (query.getQueryLanguage().equals("sql")) && (query.getQueryString().contains(BRANCH_AWARE_PREFIX));
	}
	
	private static CDOQuery createDecoratedQuery(final CDOQuery query, final CDOBranchPoint branchPoint) {
		
		checkNotNull(query, "query");
		checkNotNull(branchPoint, "branchPoint");
		checkState("sql".equals(query.getQueryLanguage()), "query is not expressed in SQL.");
		checkState(!StringUtils.isEmpty(query.getQueryString()), "query string is empty or consists of whitespace characters only.");
			
		final CDOBranch branch = branchPoint.getBranch();
		final int branchId = branch.getID();
		final long timeStamp = branchPoint.getTimeStamp();
		final boolean isMainBranch = branch.isMainBranch();
		final Supplier<Long> baseTimeStampSupplier = memoize(new Supplier<Long>() {
			public Long get() {
				return branch.getBase().getTimeStamp();
			}
		});

		final Function<String[], String> generator = new Function<String[], String>() {
			
			@Override
			public String apply(final String[] input) {
				
				final String alias = (input.length > 0) ? (input[0] + ".") : "";
				final boolean ignoreRevised = (input.length > 1) && ("true".equalsIgnoreCase(input[1]));
				final StringBuilder queryBuilder = new StringBuilder();
				
				if (!ignoreRevised) {
					
					addVersionPart(alias, queryBuilder);
					queryBuilder.append(" AND ");
					
				}
				
				addBranchPrefilterPart(alias, queryBuilder);

				if (isMainBranch) {
					
					queryBuilder.append(" AND ");
					
					addBranchExactFilterSubPart(alias, 
							queryBuilder, 
							timeStamp, 
							ignoreRevised, 
							":timeStamp", 
							":branchId");
					
					// Nothing else to do on main branch
					return queryBuilder.toString();
				}
				
				queryBuilder.append(" AND ");
				addBranchExactFilterPart(alias, queryBuilder, ignoreRevised);
				
				return queryBuilder.toString();
			}

			private void addVersionPart(final String alias, final StringBuilder queryBuilder) {
				
				queryBuilder.append(alias);
				queryBuilder.append(CDODBSchema.ATTRIBUTES_VERSION);
				queryBuilder.append(" > 0");
			}

			/*
			 * Restricts the query to the task branch in question and/or the
			 * main branch in a simple AND clause, so optimizers can make use of
			 * indexes including CDO_BRANCH
			 */
			private void addBranchPrefilterPart(final String alias, final StringBuilder queryBuilder) {

				if (!isMainBranch) {
					queryBuilder.append("(");
				}
				
				queryBuilder.append(alias);
				queryBuilder.append(CDODBSchema.ATTRIBUTES_BRANCH);
				queryBuilder.append(" = :branchId");

				if (!isMainBranch) {
					
					queryBuilder.append(" OR ");
					queryBuilder.append(alias);
					queryBuilder.append(CDODBSchema.ATTRIBUTES_BRANCH);
					queryBuilder.append(" = 0)");
				}
			}

			private void addBranchExactFilterPart(final String alias, final StringBuilder queryBuilder, final boolean ignoreRevised) {
				
				queryBuilder.append("(");
				
				addBranchExactFilterSubPart(
						alias, 
						queryBuilder,
						timeStamp, 
						ignoreRevised,
						":timeStamp", 
						":branchId");
					
				queryBuilder.append(" OR ");
			
				addBranchExactFilterSubPart(
						alias, 
						queryBuilder,
						baseTimeStampSupplier.get().longValue(), 
						ignoreRevised, 
						":baseTimeStamp",
						String.valueOf(CDOBranch.MAIN_BRANCH_ID)); 
				
				queryBuilder.append(")");
			}

			private void addBranchExactFilterSubPart(
					final String alias,
					final StringBuilder queryBuilder,
					final long timeStampValue,
					final boolean ignoreRevised,
					final String timeStampParameter, 
					final String branchIdParameter) {

				if (timeStampValue == CDORevision.UNSPECIFIED_DATE) {

					if (!ignoreRevised) {
						queryBuilder.append("(");
					}

					queryBuilder.append(alias);
					queryBuilder.append(CDODBSchema.ATTRIBUTES_BRANCH);
					queryBuilder.append(" = ");
					queryBuilder.append(branchIdParameter);
					
					if (!ignoreRevised) {
						queryBuilder.append(" AND ");
						queryBuilder.append(alias);
						queryBuilder.append(CDODBSchema.ATTRIBUTES_REVISED);
						queryBuilder.append(" = 0)");
					}
					
				} else {

					queryBuilder.append("(");
					
					queryBuilder.append(alias);
					queryBuilder.append(CDODBSchema.ATTRIBUTES_BRANCH);
					queryBuilder.append(" = ");
					queryBuilder.append(branchIdParameter);

					queryBuilder.append(" AND ");
					queryBuilder.append(alias);
					queryBuilder.append(CDODBSchema.ATTRIBUTES_CREATED);
					queryBuilder.append(" <= ");
					queryBuilder.append(timeStampParameter);
					
					if (!ignoreRevised) {
						queryBuilder.append(" AND (");
						queryBuilder.append(alias);
						queryBuilder.append(CDODBSchema.ATTRIBUTES_REVISED);
						queryBuilder.append(" = 0 OR ");
						queryBuilder.append(alias);
						queryBuilder.append(CDODBSchema.ATTRIBUTES_REVISED);
						queryBuilder.append(" >= ");
						queryBuilder.append(timeStampParameter);
						queryBuilder.append(")");
					}
					
					queryBuilder.append(")");
				}
			}
		};

		final String decoratedQueryString = new TokenReplacer()
				.register("visibleFromBranch", generator)
				.substitute(query.getQueryString());

		final CDOQuery decoratedQuery = query.getView().createQuery("sql", decoratedQueryString);
		decoratedQuery.setContext(query.getContext());
		decoratedQuery.setMaxResults(query.getMaxResults());
		
		for (final Entry<String, Object> parameter : query.getParameters().entrySet()) {
			decoratedQuery.setParameter(parameter.getKey(), parameter.getValue());
		}
		
		// XXX: parameters with the same names will be overwritten here 
		decoratedQuery.setParameter("branchId", branchId);
		
		if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE) {
			decoratedQuery.setParameter("timeStamp", timeStamp);
		}
		
		if (!isMainBranch) {
			decoratedQuery.setParameter("baseTimeStamp", baseTimeStampSupplier.get().longValue());
		}

		return decoratedQuery;
	}
	
	/**
	 * Executes the query and returns with an optionally unique set of results
	 * presented as a list that exist in the view associated with the query.
	 * <p>
	 * <b>Note</b>: to make duplicate removal effective, the requested object
	 * class must have well-behaving {@link Object#equals(Object)} and
	 * {@link Object#hashCode()} implementations, or reference equality has to
	 * be ensured for objects of the same content. Object arrays don't have such
	 * guarantees, and duplicates should not be expected to be removed in this
	 * case.
	 * 
	 * @param query the query to execute (may not be <code>null</code>)
	 * @param objectClass the result list type (may not be <code>null</code>)
	 * @return the query result list
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getViewResult(final CDOQuery query, final Class<T> objectClass) {

		checkNotNull(query, "query");
		checkNotNull(objectClass, "objectClass");
		
		if (!isBranchAware(query)) {
			return query.getResult(objectClass);
		}
		
		final Collection<T> collectedResults = Sets.<T>newLinkedHashSet();
		final CDOBranchPoint branchPoint = query.getView();
		final CDOQuery branchQuery = createDecoratedQuery(query, branchPoint);

		final List<T> result;
		if (query.getView() instanceof org.eclipse.emf.cdo.internal.server.ServerCDOView) {
			
			final AbstractQueryIterator<?> queryItr = objectClass.isAssignableFrom(CDOID.class) 
					? new CDOQueryCDOIDIteratorImpl<CDOID>(query.getView(), branchQuery) 
					: new CDOQueryResultIteratorImpl<T>(query.getView(), branchQuery);
			
			getResults(branchQuery, queryItr);
			
			result = (List<T>) Lists.newArrayList(queryItr);
			
		} else {
			
			result = branchQuery.getResult(objectClass);
			
		}
		
		//ensure non-null elements.
		//it might happen that query returns with the proper CDO IDs, 
		//but when iterating on the CDOQRI, ID cannot be resolved via the given CDO view
		for (final Iterator<T> itr = result.iterator(); itr.hasNext(); /**/) {
			
			final T value = itr.next();

			if (null != value) {
				collectedResults.add(value);
			}
			
		}
		
		return ImmutableList.copyOf(collectedResults); 
	}
	
	@SuppressWarnings("restriction")
	public static void getResults(final CDOQuery query, final AbstractQueryIterator<?> queryIterator) {
		
		final ServerCDOView view = (ServerCDOView) query.getView();
		final ServerCDOSession session = (ServerCDOSession) view.getSession();
		final InternalRepository repository = session.getRepository();
		final InternalQueryManager queryManager = repository.getQueryManager();

		final InternalSession serverSession = session.getInternalSession();
		final InternalView serverView = serverSession.getView(view.getViewID());
		final InternalQueryResult result = queryManager.execute(serverView, queryIterator.getQueryInfo());
		final int queryId = result.getQueryID();
		
		queryIterator.setQueryID(queryId);
		
		final CDOQueryQueue<Object> resultQueue = queryIterator.getQueue();
		
		try {
			
			while (result.hasNext()) {
				final Object object = result.next();
				if (null != object) {
					resultQueue.add(object);
				}
			}
			
		} catch (final Throwable t) {
			resultQueue.setException(new RuntimeException(t.getMessage(), t));
		} finally {
			resultQueue.close();
		}
	}

	private CDOQueryUtils() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
