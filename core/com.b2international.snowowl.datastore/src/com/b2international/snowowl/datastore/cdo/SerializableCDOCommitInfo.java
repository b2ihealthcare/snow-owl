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
import java.io.Serializable;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
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
import org.eclipse.net4j.util.io.DataInputExtender;
import org.eclipse.net4j.util.io.DataOutputExtender;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.google.common.base.Preconditions;

/**
 * Class for wrapping a {@link CDOCommitInfo commit info} and adding {@link Serializable} nature via {@link Externalizable}.
 *
 */
@SuppressWarnings("restriction")
public class SerializableCDOCommitInfo extends org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo implements Externalizable {

	private CDOCommitInfo delegate;

	/**
	 * Called by JVM.
	 * @see Externalizable
	 * @deprecated
	 */
	public SerializableCDOCommitInfo() {
		
	}
	
	public SerializableCDOCommitInfo(final CDOCommitInfo delegate) {
		this.delegate = Preconditions.checkNotNull(delegate);
		Preconditions.checkArgument(null != this.delegate.getBranch(), "CDO failure commit info is not supported.");
	}
	
	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {

		Preconditions.checkNotNull(out);
		
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = connectionManager.get(getBranch());
		final String repositoryUuid = connection.getUuid();

		//repository UUID
		out.writeUTF(repositoryUuid);
		
		final CDODataOutput dataOutput = new CDODataOutputImpl(new DataOutputExtender(out)) {
			
			@Override public CDOIDProvider getIDProvider() {
				return new CDOTransactionBasedIdProvider(getBranch());
			}
			
		};
		
		dataOutput.writeCDOCommitInfo(getDelegate());
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {

		Preconditions.checkNotNull(in);
		
		final String repositoryUuid = in.readUTF();
		final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final ICDOConnection connection = connectionManager.getByUuid(repositoryUuid);
		final CDONet4jSession session = connection.getSession();
		
		final CDODataInput dataInput = new CDODataInputImpl(new DataInputExtender(in)) {
			
			@Override 
			protected CDORevisionFactory getRevisionFactory() {
				return CDORevisionFactory.DEFAULT;
			}
			
			@Override
			public CDOPackageRegistry getPackageRegistry() {
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
		
		delegate = dataInput.readCDOCommitInfo();
		
	}

	/**
	 * Sugar for {@link #getBranchPath()}.
	 */
	public String getBranchPathName() {
		return getBranchPath().getPath();
	}
	
	/**
	 * Returns with the {@link IBranchPath branch path} of current commit info.
	 * @return the branch path.
	 */
	public IBranchPath getBranchPath() {
		return BranchPathUtils.createPath(delegate.getBranch());
	}
	
	/**
	 * Returns with the unique identifier of the repository where the change indicating by the current commit
	 * info happened.
	 * @return the repository UUID.
	 */
	public String getRepositoryUuid() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(getBranch()).getUuid();
	}
	
	/**
	 * Returns with the human readable name of the repository where the change indicating by the current commit
	 * info happened.
	 * @return the human readable name of the repository.
	 */
	public String getRepositoryName() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(getBranch()).getRepositoryName();
	}
	
	/**
	 * Returns with the application specific tooling ID (<i>terminology ID</i>) associated with the repository 
	 * where the modification represented by the current commit info were made. 
	 * @return the application specific tooling ID.
	 */
	public String getToolingId() {
		return CodeSystemUtils.getSnowOwlToolingId(getRepositoryUuid());
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo#getDelegate()
	 */
	@Override
	protected CDOCommitInfo getDelegate() {
		return delegate;
	}

}