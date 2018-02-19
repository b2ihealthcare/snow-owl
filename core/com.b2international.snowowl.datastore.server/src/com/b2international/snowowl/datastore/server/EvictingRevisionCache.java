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
package com.b2international.snowowl.datastore.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * @since 6.3
 */
public class EvictingRevisionCache extends Lifecycle implements InternalCDORevisionCache {

	private final Map<CDOID, TypeAndRefCounter> typeMap = new HashMap<CDOID, TypeAndRefCounter>();
	private final Cache<CDOIDAndBranch, RevisionList> revisionLists;

	public EvictingRevisionCache() {
		revisionLists = CacheBuilder.newBuilder()
				// TODO configure eviction policy via config file 
				.expireAfterAccess(20, TimeUnit.MINUTES)
				.removalListener(new RemovalListener<CDOIDAndBranch, RevisionList>() {
					@Override
					public void onRemoval(RemovalNotification<CDOIDAndBranch, RevisionList> entry) {
						if (entry.getCause() != RemovalCause.EXPLICIT) {
							typeRefDecrease(entry.getKey().getID());
						}
					}
				})
				.build();
	}

	public InternalCDORevisionCache instantiate(CDORevision revision) {
		return new EvictingRevisionCache();
	}

	@Override
	public EClass getObjectType(CDOID id) {
		synchronized (revisionLists) {
			TypeAndRefCounter typeCounter = typeMap.get(id);
			if (typeCounter != null) {
				return typeCounter.getType();
			}

			return null;
		}
	}

	private void typeRefIncrease(CDOID id, EClass type) {
		TypeAndRefCounter typeCounter = typeMap.get(id);
		if (typeCounter == null) {
			typeCounter = new TypeAndRefCounter(type);
			typeMap.put(id, typeCounter);
		}

		typeCounter.increase();
	}

	private void typeRefDecrease(CDOID id) {
		TypeAndRefCounter typeCounter = typeMap.get(id);
		if (typeCounter != null && typeCounter.decreaseAndGet() == 0) {
			typeMap.remove(id);
		}
	}

	private void typeRefDispose() {
		typeMap.clear();
	}

	private boolean isKeyInBranch(Object key, CDOBranch branch) {
		return ObjectUtil.equals(((CDOIDAndBranch) key).getBranch(), branch);
	}

	private CDOIDAndBranch createKey(CDOID id, CDOBranch branch) {
		return CDOIDUtil.createIDAndBranch(id, branch);
	}

	public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint) {
		RevisionList revisionList = getRevisionList(id, branchPoint.getBranch());
		if (revisionList != null) {
			return revisionList.getRevision(branchPoint.getTimeStamp());
		}

		return null;
	}

	public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion) {
		RevisionList revisionList = getRevisionList(id, branchVersion.getBranch());
		if (revisionList != null) {
			return revisionList.getRevisionByVersion(branchVersion.getVersion());
		}

		return null;
	}

	public List<CDORevision> getCurrentRevisions() {
		List<CDORevision> currentRevisions = new ArrayList<CDORevision>();
		synchronized (revisionLists) {
			for (RevisionList revisionList : revisionLists.asMap().values()) {
				InternalCDORevision revision = revisionList.getRevision(CDORevision.UNSPECIFIED_DATE);
				if (revision != null) {
					currentRevisions.add(revision);
				}
			}
		}

		return currentRevisions;
	}

	public Map<CDOBranch, List<CDORevision>> getAllRevisions() {
		Map<CDOBranch, List<CDORevision>> result = new HashMap<CDOBranch, List<CDORevision>>();
		synchronized (revisionLists) {
			for (RevisionList list : revisionLists.asMap().values()) {
				list.getAllRevisions(result);
			}
		}

		return result;
	}

	public List<CDORevision> getRevisions(CDOBranchPoint branchPoint) {
		List<CDORevision> result = new ArrayList<CDORevision>();
		CDOBranch branch = branchPoint.getBranch();
		synchronized (revisionLists) {
			for (Map.Entry<CDOIDAndBranch, RevisionList> entry : revisionLists.asMap().entrySet()) {
				if (isKeyInBranch(entry.getKey(), branch))
				// if (ObjectUtil.equals(entry.getKey().getBranch(), branch))
				{
					RevisionList list = entry.getValue();
					InternalCDORevision revision = list.getRevision(branchPoint.getTimeStamp());
					if (revision != null) {
						result.add(revision);
					}
				}
			}
		}

		return result;
	}

	public void addRevision(CDORevision revision) {
		CheckUtil.checkArg(revision, "revision");

		CDOID id = revision.getID();
		CDOIDAndBranch key = createKey(id, revision.getBranch());

		synchronized (revisionLists) {
			RevisionList list = revisionLists.getIfPresent(key);
			if (list == null) {
				list = new RevisionList();
				revisionLists.put(key, list);
			}

			if (list.addRevision((InternalCDORevision) revision)) {
				typeRefIncrease(id, revision.getEClass());
			}
		}
	}

	public InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion) {
		Object key = createKey(id, branchVersion.getBranch());
		synchronized (revisionLists) {
			RevisionList list = revisionLists.getIfPresent(key);
			if (list != null) {
				list.removeRevision(branchVersion.getVersion());
				if (list.isEmpty()) {
					revisionLists.invalidate(key);
					typeRefDecrease(id);
				}
			}
		}

		return null;
	}

	public void clear() {
		synchronized (revisionLists) {
			revisionLists.invalidateAll();
			typeRefDispose();
		}
	}

	@Override
	public String toString() {
		synchronized (revisionLists) {
			return revisionLists.toString();
		}
	}

	protected RevisionList getRevisionList(CDOID id, CDOBranch branch) {
		Object key = createKey(id, branch);
		synchronized (revisionLists) {
			return revisionLists.getIfPresent(key);
		}
	}

	protected static final class RevisionList extends LinkedList<InternalCDORevision> {
		private static final long serialVersionUID = 1L;

		public RevisionList() {
		}

		public synchronized InternalCDORevision getRevision(long timeStamp) {
			if (timeStamp == CDORevision.UNSPECIFIED_DATE) {
				InternalCDORevision revision = isEmpty() ? null : getFirst();
				if (revision != null && !revision.isHistorical()) {
					return revision;
				}
				return null;
			}

			for (Iterator<InternalCDORevision> it = iterator(); it.hasNext();) {
				InternalCDORevision revision = it.next();
				long created = revision.getTimeStamp();
				if (created <= timeStamp) {
					long revised = revision.getRevised();
					if (timeStamp <= revised || revised == CDORevision.UNSPECIFIED_DATE) {
						return revision;
					}
					break;
				}
			}

			return null;
		}

		public synchronized InternalCDORevision getRevisionByVersion(int version) {
			for (Iterator<InternalCDORevision> it = iterator(); it.hasNext();) {
				InternalCDORevision revision = it.next();
				int v = revision.getVersion();
				if (v == version) {
					return revision;
				} else if (v < version) {
					break;
				}
			}

			return null;
		}

		public synchronized boolean addRevision(InternalCDORevision revision) {
			if (revision.getBranch().getID() == 34 && revision.getVersion() == 2 && revision.getEClass().getName().equals("Concept")) {
				System.err.println();
			}
			int version = revision.getVersion();
			for (ListIterator<InternalCDORevision> it = listIterator(); it.hasNext();) {
				InternalCDORevision revisionInList = it.next();
				int v = revisionInList.getVersion();
				if (v == version) {
					return false;
				}

				if (v < version) {
					it.previous();
					it.add(revision);
					return true;
				}
			}

			addLast(revision);
			return true;
		}

		public synchronized void removeRevision(int version) {
			for (Iterator<InternalCDORevision> it = iterator(); it.hasNext();) {
				InternalCDORevision revision = it.next();
				int v = revision.getVersion();
				if (v == version) {
					it.remove();
					break;
				} else if (v < version) {
					break;
				}
			}
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			for (Iterator<InternalCDORevision> it = iterator(); it.hasNext();) {
				InternalCDORevision revision = it.next();
				if (buffer.length() == 0) {
					buffer.append("{");
				} else {
					buffer.append(", ");
				}

				buffer.append(revision);
			}

			buffer.append("}");
			return buffer.toString();
		}

		public void getAllRevisions(Map<CDOBranch, List<CDORevision>> result) {
			for (Iterator<InternalCDORevision> it = iterator(); it.hasNext();) {
				InternalCDORevision revision = it.next();
				CDOBranch branch = revision.getBranch();
				List<CDORevision> resultList = result.get(branch);
				if (resultList == null) {
					resultList = new ArrayList<CDORevision>(1);
					result.put(branch, resultList);
				}

				resultList.add(revision);
			}
		}
	}

	private static final class TypeAndRefCounter {
		private EClass type;

		private int refCounter;

		public TypeAndRefCounter(EClass type) {
			this.type = type;
		}

		public EClass getType() {
			return type;
		}

		public void increase() {
			++refCounter;
		}

		public int decreaseAndGet() {
			return --refCounter;
		}
	}
}
