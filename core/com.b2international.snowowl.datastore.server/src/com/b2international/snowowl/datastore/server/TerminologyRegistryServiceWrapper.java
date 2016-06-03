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
package com.b2international.snowowl.datastore.server;

import static com.b2international.snowowl.datastore.index.IndexUtils.isEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptySet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.AlphaNumericComparator;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.InternalTerminologyRegistryService;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemFactory;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemVersionFactory;
import com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * {@link InternalTerminologyRegistryService} implementation which wraps an {@link IndexServerService}.
 * Since, it's just a wrapper, it's instantiation and memory consumption cost is almost free.
 *
 */
public class TerminologyRegistryServiceWrapper implements InternalTerminologyRegistryService {

	private final IndexServerService<?> wrappedIndexService;

	public TerminologyRegistryServiceWrapper(final IndexServerService<?> wrappedIndexService) {
		this.wrappedIndexService = checkNotNull(wrappedIndexService, "wrappedIndexService");
	}

	@Override
	public Collection<ICodeSystemVersion> getCodeSystemVersionsFromRepositoryWithInitVersion(final IBranchPath branchPath, final String repositoryUuid) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(repositoryUuid, "repositoryUuid");
		
		final Query query = new TermQuery(new Term(TerminologyRegistryIndexConstants.VERSION_REPOSITORY_UUID, repositoryUuid));
		return getCodeSystemVersionsFromRepository(branchPath, repositoryUuid, query);
	}
	
	@Override
	public Collection<ICodeSystemVersion> getCodeSystemVersionsFromRepository(final IBranchPath branchPath, final String repositoryUuid) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(repositoryUuid, "repositoryUuid");
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(TerminologyRegistryIndexConstants.VERSION_REPOSITORY_UUID, repositoryUuid)), Occur.MUST);
		query.add(new TermQuery(new Term(TerminologyRegistryIndexConstants.VERSION_VERSION_ID, ICodeSystemVersion.INITIAL_STATE)), Occur.MUST_NOT);
		return getCodeSystemVersionsFromRepository(branchPath, repositoryUuid, query);
	}
	
	private Collection<ICodeSystemVersion> getCodeSystemVersionsFromRepository(final IBranchPath branchPath, final String repositoryUuid, final Query query) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(repositoryUuid, "repositoryUuid");
		
		final int hitCount = wrappedIndexService.getHitCount(branchPath, query, null);
		
		if (1 > hitCount) {
			return emptySet();
		}
		
		final ICodeSystemVersion[] versions = new ICodeSystemVersion[hitCount];
		
		final TopDocs topDocs = wrappedIndexService.search(branchPath, query, hitCount);
		if (isEmpty(topDocs)) {
			return emptySet();
		}
		
		ReferenceManager<IndexSearcher> manager= null;
		IndexSearcher searcher = null;
		
		try {
			 
			manager = wrappedIndexService.getManager(branchPath);
			searcher = manager.acquire();
			
			for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			
				versions[i] = CodeSystemVersionFactory.createCodeSystemVersionEntry(searcher.doc(topDocs.scoreDocs[i].doc));
				
			}
			
			return Arrays.asList(versions);
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
		} finally {
			
			if (null != manager) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					//ignore
					
				}
				
			}
			
		}
	}
	
	@Override
	public Collection<ICodeSystem> getCodeSystems(final IBranchPath branchPath) {
		
		final Query query = new PrefixQuery(new Term(TerminologyRegistryIndexConstants.SYSTEM_SHORT_NAME));
		
		final int hitCount = wrappedIndexService.getHitCount(branchPath, query, null);
		
		if (hitCount < 1) {
			
			return Collections.emptySet();
			
		}
		
		final TopDocs topDocs = wrappedIndexService.search(branchPath, query, hitCount);
		
		final Set<ICodeSystem> $ = Sets.newHashSetWithExpectedSize(hitCount);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		ReferenceManager<IndexSearcher> manager= null;
		IndexSearcher searcher = null;
		
		try {
			 
			manager = wrappedIndexService.getManager(branchPath);
			searcher = manager.acquire();
			
			for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			
				$.add(CodeSystemFactory.createCodeSystemEntry(searcher.doc(topDocs.scoreDocs[i].doc)));
				
			}
			
			return $;
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
		} finally {
			
			if (null != manager) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					//ignore
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public Collection<ICodeSystemVersion> getCodeSystemVersions(final IBranchPath branchPath, final String codeSystemShortName) {
		
		final ICodeSystem codeSystem = getCodeSystemByShortName(branchPath, codeSystemShortName);
		
		if (null == codeSystem) {
			
			return Collections.emptyList();
		}
		

		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(TerminologyRegistryIndexConstants.VERSION_SYSTEM_SHORT_NAME, codeSystemShortName)), Occur.MUST);
		query.add(new TermQuery(new Term(TerminologyRegistryIndexConstants.VERSION_VERSION_ID, ICodeSystemVersion.INITIAL_STATE)), Occur.MUST_NOT);
		
		final int hitCount = wrappedIndexService.getHitCount(branchPath, query, null);
		
		if (hitCount < 1) {
			
			return Collections.emptyList();
			
		}
		
		final TopDocs topDocs = wrappedIndexService.search(branchPath, query, hitCount);
		
		final List<ICodeSystemVersion> $ = Lists.newArrayListWithExpectedSize(hitCount);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		ReferenceManager<IndexSearcher> manager= null;
		IndexSearcher searcher = null;
		
		try {
			 
			manager = wrappedIndexService.getManager(branchPath);
			searcher = manager.acquire();
			
			for (int i = 0; i < topDocs.scoreDocs.length; i++) {
			
				$.add(CodeSystemVersionFactory.createCodeSystemVersionEntry(searcher.doc(topDocs.scoreDocs[i].doc)));
				
			}
			
			return $;
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
		} finally {
			
			if (null != manager) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					//ignore
					
				}
				
			}
			
		}
		
		
	}
	
	@Override
	public ICodeSystem getCodeSystemByShortName(final IBranchPath branchPath, final String codeSystemShortName) {
		
		final TermQuery query = new TermQuery(new Term(TerminologyRegistryIndexConstants.SYSTEM_SHORT_NAME, codeSystemShortName));
		final TopDocs topDocs = wrappedIndexService.search(branchPath, query, 1);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		ReferenceManager<IndexSearcher> manager= null;
		IndexSearcher searcher = null;
		
		try {
			 
			manager = wrappedIndexService.getManager(branchPath);
			searcher = manager.acquire();
			return CodeSystemFactory.createCodeSystemEntry(searcher.doc(topDocs.scoreDocs[0].doc));
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
		} finally {
			
			if (null != manager) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					//ignore
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public ICodeSystem getCodeSystemByOid(final IBranchPath branchPath, final String codeSystemOID) {
		
		final TermQuery query = new TermQuery(new Term(TerminologyRegistryIndexConstants.SYSTEM_OID, codeSystemOID));
		final TopDocs topDocs = wrappedIndexService.search(branchPath, query, 1);
		
		if (IndexUtils.isEmpty(topDocs)) {
			return null;
		}
		
		ReferenceManager<IndexSearcher> manager= null;
		IndexSearcher searcher = null;
		
		try {
			 
			manager = wrappedIndexService.getManager(branchPath);
			searcher = manager.acquire();
			return CodeSystemFactory.createCodeSystemEntry(searcher.doc(topDocs.scoreDocs[0].doc));
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
		} finally {
			
			if (null != manager) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					//ignore
					
				}
				
			}
			
		}
		
		
	}

	@Override
	public Map<String, ICodeSystem> getTerminologyComponentIdCodeSystemMap(final IBranchPath branchPath) {
		
		final Collection<ICodeSystem> set = getCodeSystems(branchPath);
		final Map<String, ICodeSystem> $ = Maps.newHashMap();
		
		for (final ICodeSystem entry : set) {
			// TODO(afeher): handle LCS and UMLS
			$.put(entry.getSnowOwlId(), entry);
		}
		
		return Collections.unmodifiableMap($);
	}

	@Override
	public Map<String, Collection<ICodeSystem>> getTerminologyComponentIdWithMultipleCodeSystemsMap(final IBranchPath branchPath) {
		
		final Collection<ICodeSystem> set = getCodeSystems(branchPath);
		final Map<String, Collection<ICodeSystem>> $ = Maps.newHashMap();
		
		for (final ICodeSystem entry : set) {
			
			if ($.containsKey(entry.getSnowOwlId())) {
				
				$.get(entry.getSnowOwlId()).add(entry);
				
			} else {
				
				$.put(entry.getSnowOwlId(), Lists.newArrayList(entry));
				
			}
			
		}

		return Collections.unmodifiableMap($);
	}

	@Override
	public String getTerminologyComponentIdByShortName(final IBranchPath branchPath, final String codeSystemShortName) {
		final ICodeSystem codeSystem = getCodeSystemByShortName(branchPath, codeSystemShortName);
		if (null == codeSystem) {
			return null;
		}
		return codeSystem.getSnowOwlId();
	}
	
	@Override
	public String getVersionId(final IBranchPath branchPath, final ICodeSystem codeSystem) {
		
		if (null == codeSystem) {
			return null;
		}
		
		String version = "";
		final AlphaNumericComparator comparator = new AlphaNumericComparator();
		
		for (final ICodeSystem presentCodeSystem : getCodeSystems(branchPath)) {
			if (isEquals(codeSystem, presentCodeSystem)) {
				
				for (final ICodeSystemVersion codeSystemVersion : getCodeSystemVersions(branchPath, presentCodeSystem.getShortName())) {
					final String versionId = codeSystemVersion.getVersionId();
					if (comparator.compare(versionId, version) > 0) {
						version = versionId;
					}
				}
			}
		}
		
		
		return StringUtils.isEmpty(version) ? String.valueOf(Integer.valueOf(0)) : version;
	}
	
	//copy pasted from TREC
	private boolean isEquals(final ICodeSystem codeSystem, final ICodeSystem eObject) {
		// if OID is not empty for the two code systems, compare those, else fall back to short name comparison
		if (!codeSystem.getOid().isEmpty() && !eObject.getOid().isEmpty()) {
			if (codeSystem.getOid().equals(eObject.getOid())) {
				return true;
			}
		} else {
			// TODO find a better comparison than compare short names.
			if (eObject.getShortName().equals(codeSystem.getShortName())) {
				return true;
			}
		}
		
		return false;
	}
	
}