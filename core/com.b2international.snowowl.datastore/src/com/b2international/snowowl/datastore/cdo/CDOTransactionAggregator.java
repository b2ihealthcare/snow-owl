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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithElementProxiesImpl;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.net4j.util.io.DataInputExtender;
import org.eclipse.net4j.util.io.DataOutputExtender;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * {@link ICDOTransactionAggregator} implementation.
 * @see #dispose()
 */
@SuppressWarnings("restriction")
public class CDOTransactionAggregator implements ICDOTransactionAggregator {
	
	private Set<CDOTransaction> transactions;
	private String uuid;

	public static ICDOTransactionAggregator create(final CDOTransaction transaction, final CDOTransaction... transactions) {
		final Set<CDOTransaction> $ = Sets.newHashSet(transactions);
		$.add(transaction);
		return create($);
	}
	
	public static ICDOTransactionAggregator create(final Iterable<CDOTransaction> transactions) {
		return new CDOTransactionAggregator(transactions);
	}

	/**
	 * Required by JVM.
	 * @see Externalizable
	 * @deprecated
	 */
	public CDOTransactionAggregator() {

	}
	
	private CDOTransactionAggregator(final Iterable<CDOTransaction> transactions) {
		Preconditions.checkNotNull(transactions, "Transaction argument cannot be null.");
		this.transactions = Sets.newHashSet(transactions);
		uuid = UUID.randomUUID().toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDOTransactionAggregator#getUuid()
	 */
	@Override
	public String getUuid() {
		return Preconditions.checkNotNull(uuid);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<CDOTransaction> iterator() {
		return Iterators.unmodifiableIterator(transactions.iterator());
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDOTransactionAggregator#add(org.eclipse.emf.cdo.transaction.CDOTransaction)
	 */
	@Override
	public void add(final CDOTransaction transaction) {
		transactions.add(transaction);
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		
		Preconditions.checkNotNull(out);
		
		//aggregator UUID first
		out.writeUTF(uuid);
		
		//size
		out.writeInt(transactions.size());
		
		//serialize transaction
		for (final CDOTransaction transaction : transactions) { //instead of rewinding the cursor
			
			final CDOBranch branch = transaction.getBranch();
			final CDOSession session = transaction.getSession();
			final CDORepositoryInfo repositoryInfo = session.getRepositoryInfo();
			final String repositoryUuid = repositoryInfo.getUUID();
			
			//repository UUID for getting the proper session when reading change set
			out.writeUTF(repositoryUuid);

			//write branch path
			out.writeUTF(BranchPathUtils.createPath(transaction).getPath());
			
			//write change set
			final CDODataOutput dataOutput = new CDODataOutputImpl(new DataOutputExtender(out)) {
				@Override public CDOIDProvider getIDProvider() {
					return new CDOTransactionBasedIdProvider(branch);
				}
			};
			
			dataOutput.writeCDOChangeSetData(transaction.getChangeSetData());
			
		}
		
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		
		Preconditions.checkNotNull(in);
		
		uuid = in.readUTF();
		
		final int size = in.readInt();
		final Set<CDOTransaction> $ = Sets.newHashSet();
		
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		
		for (int i = 0; i < size; i++) {
			
			final String repositoryUuid = in.readUTF();
			final IBranchPath branchPath = BranchPathUtils.createPath(in.readUTF());
			
			final ICDOConnection connection = connectionManager.getByUuid(repositoryUuid);
			final CDONet4jSession session = connection.getSession();
			final CDOTransaction transaction = connection.createTransaction(branchPath);
			
			final CDODataInput dataInput = new CDODataInputImpl(new DataInputExtender(in)) {
				
				@Override 
				protected CDORevisionFactory getRevisionFactory() {
					return CDORevisionFactory.DEFAULT;
				}
				
				@Override
				protected CDOPackageRegistry getPackageRegistry() {
					return session.getPackageRegistry();
				}
				
				@Override
				protected CDOLobStore getLobStore() {
					return ((CDONet4jSessionImpl) session).getLobStore(); 
				}
				
				@Override
				protected CDOListFactory getListFactory() {
					return CDOListWithElementProxiesImpl.FACTORY;
				}
				
				@Override
				protected CDOCommitInfoManager getCommitInfoManager() {
					return session.getCommitInfoManager();
				}
				
				@Override
				protected CDOBranchManager getBranchManager() {
					return session.getBranchManager();
				}
			};
			
			final CDOChangeSetData changeSetData = dataInput.readCDOChangeSetData();
			
			((InternalCDOTransaction) transaction).applyChangeSet(
					changeSetData, 
					transaction, 
					transaction, 
					transaction, 
					true);
			
			$.add(transaction);
			
		}
		
		transactions = Sets.newHashSet($);
		
	}

	/**
	 * Removes the specified transaction from this aggregator. Has no effect if the aggragtor does 
	 * not contain the given transaction.
	 * <br>Clients should be consider that the CDO transaction argument will not be deactivated. 
	 * @param transaction the transaction to remove.
	 * @nonapi
	 */
	public void remove(final CDOTransaction transaction) {
		transactions.remove(Preconditions.checkNotNull(transaction));
	}

	/**
	 * Releases the underlying transactions. Clears the underlying {@link CDOTransaction transaction} cache.
	 * @nonapi
	 */
	public void dispose() {
		LifecycleUtils.deactivate(transactions);
		if (null != transactions) {
			transactions.clear();
		}
	}
	
	@Override
	public void close() { 
		dispose();
	}
	

}