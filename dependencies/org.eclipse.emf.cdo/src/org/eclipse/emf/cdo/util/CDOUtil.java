/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;
import org.eclipse.emf.cdo.view.CDOStaleObject;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerModelBased;
import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerUI;
import org.eclipse.emf.internal.cdo.analyzer.CDOFetchRuleManagerThreadLocal;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOFactoryImpl;
import org.eclipse.emf.internal.cdo.object.CDOObjectWrapper;
import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingPolicyImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOXATransactionImpl;
import org.eclipse.emf.internal.cdo.transaction.CDOXATransactionImpl.CDOXAInternalAdapter;
import org.eclipse.emf.internal.cdo.view.CDORevisionPrefetchingPolicyImpl;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Iterator;
import java.util.Map;

/**
 * Various static methods that may help in CDO client applications.
 *
 * @author Eike Stepper
 */
public final class CDOUtil
{
  private static final ThreadLocal<Boolean> legacyModeDefault = new InheritableThreadLocal<Boolean>()
  {
    @Override
    protected Boolean initialValue()
    {
      return false;
    }
  };

  static
  {
    if (!OMPlatform.INSTANCE.isOSGiRunning())
    {
      registerResourceFactory(Resource.Factory.Registry.INSTANCE);
    }
  }

  private CDOUtil()
  {
  }

  /**
   * @since 4.0
   */
  public static boolean registerResourceFactory(Resource.Factory.Registry registry)
  {
    if (registry == null)
    {
      return false;
    }

    Map<String, Object> map = registry.getProtocolToFactoryMap();
    if (!map.containsKey(CDOURIUtil.PROTOCOL_NAME))
    {
      map.put(CDOURIUtil.PROTOCOL_NAME, CDOResourceFactory.INSTANCE);
      return true;
    }

    return false;
  }

  /**
   * @since 3.0
   */
  public static CDOSession getSession(Object object)
  {
    if (object == null)
    {
      return null;
    }

    CDOSession session = AdapterUtil.adapt(object, CDOSession.class);
    if (session != null)
    {
      return session;
    }

    CDOView view = AdapterUtil.adapt(object, CDOView.class);
    if (view != null)
    {
      return view.getSession();
    }

    CDOObject cdoObject = AdapterUtil.adapt(object, CDOObject.class);
    if (cdoObject != null)
    {
      return cdoObject.cdoView().getSession();
    }

    CDORemoteSessionManager remoteSessionManager = AdapterUtil.adapt(object, CDORemoteSessionManager.class);
    if (remoteSessionManager != null)
    {
      return remoteSessionManager.getLocalSession();
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static boolean prepareDynamicEPackage(EPackage startPackage)
  {
    if (CDOFactoryImpl.prepareDynamicEPackage(startPackage))
    {
      for (EPackage subPackage : startPackage.getESubpackages())
      {
        prepareDynamicEPackage(subPackage);
      }

      return true;
    }

    return false;
  }

  /**
   * @since 2.0
   */
  public static CDOCollectionLoadingPolicy createCollectionLoadingPolicy(int initialChunkSize, int resolveChunkSize)
  {
    return new CDOCollectionLoadingPolicyImpl(initialChunkSize, resolveChunkSize);
  }

  /**
   * @since 2.0
   */
  public static CDORevisionPrefetchingPolicy createRevisionPrefetchingPolicy(int chunkSize)
  {
    if (chunkSize <= 0)
    {
      return CDORevisionPrefetchingPolicy.NO_PREFETCHING;
    }

    return new CDORevisionPrefetchingPolicyImpl(chunkSize);
  }

  /**
   * @since 4.1
   */
  public static CDOFetchRuleManager createThreadLocalFetchRuleManager()
  {
    return new CDOFetchRuleManagerThreadLocal();
  }

  /**
   * @since 4.1
   */
  public static CDOFeatureAnalyzer createModelBasedFeatureAnalyzer()
  {
    return new CDOFeatureAnalyzerModelBased();
  }

  /**
   * @since 4.1
   */
  public static CDOFeatureAnalyzer createUIFeatureAnalyzer()
  {
    return new CDOFeatureAnalyzerUI();
  }

  /**
   * @since 4.1
   */
  public static CDOFeatureAnalyzer createUIFeatureAnalyzer(long maxTimeBetweenOperation)
  {
    return new CDOFeatureAnalyzerUI(maxTimeBetweenOperation);
  }

  /**
   * @since 4.0
   */
  public static CDOXATransaction createXATransaction(Notifier... notifiers)
  {
    CDOXATransaction xaTransaction = new CDOXATransactionImpl();
    for (Notifier notifier : notifiers)
    {
      CDOViewSet viewSet = getViewSet(notifier);
      if (viewSet == null)
      {
        throw new IllegalArgumentException("Notifier is not associated with a CDOViewSet: " + notifier);
      }

      try
      {
        xaTransaction.add(viewSet);
      }
      catch (IllegalArgumentException ex)
      {
        OM.LOG.warn(ex);
      }
    }

    return xaTransaction;
  }

  /**
   * @since 2.0
   */
  public static CDOXATransaction getXATransaction(CDOViewSet viewSet)
  {
    EList<Adapter> adapters = viewSet.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOXAInternalAdapter)
      {
        return ((CDOXAInternalAdapter)adapter).getXATransaction();
      }
    }

    return null;
  }

  /**
   * @since 4.0
   */
  public static CDOViewSet getViewSet(Notifier notifier)
  {
    if (notifier instanceof CDOViewSet)
    {
      return (CDOViewSet)notifier;
    }

    EList<Adapter> adapters = notifier.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOViewSet)
      {
        return (CDOViewSet)adapter;
      }
    }

    if (notifier instanceof InternalEObject)
    {
      InternalEObject object = (InternalEObject)notifier;
      EObject container = object.eContainer();
      if (container != null)
      {
        CDOViewSet viewSet = getViewSet(container);
        if (viewSet != null)
        {
          return viewSet;
        }
      }

      Resource.Internal resource = object.eDirectResource();
      if (resource != null)
      {
        CDOViewSet viewSet = getViewSet(resource);
        if (viewSet != null)
        {
          return viewSet;
        }
      }
    }

    if (notifier instanceof Resource)
    {
      Resource resource = (Resource)notifier;
      ResourceSet resourceSet = resource.getResourceSet();
      if (resourceSet != null)
      {
        CDOViewSet viewSet = getViewSet(resourceSet);
        if (viewSet != null)
        {
          return viewSet;
        }
      }
    }

    return null;
  }

  /**
   * @since 3.0
   */
  public static boolean isStaleObject(Object object)
  {
    return object instanceof CDOStaleObject;
  }

  /**
   * @since 3.0
   */
  public static void cleanStaleReference(EObject eObject, EStructuralFeature eFeature)
  {
    if (!eFeature.isMany() && eFeature.getEContainingClass() != null)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)getCDOObject(eObject);
      cdoObject.eStore().unset(cdoObject, eFeature);
    }
  }

  /**
   * @since 3.0
   */
  public static void cleanStaleReference(EObject eObject, EStructuralFeature eFeature, int index)
  {
    if (eFeature.isMany() && eFeature.getEContainingClass() != null)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)getCDOObject(eObject);
      try
      {
        cdoObject.eStore().remove(cdoObject, eFeature, index);
      }
      catch (ObjectNotFoundException ex)
      {
        // Ignore the exception
      }
    }
  }

  /**
   * @since 2.0
   */
  public static void load(EObject eObject, CDOView view)
  {
    InternalCDOObject cdoObject = FSMUtil.adapt(eObject, view);
    CDOStateMachine.INSTANCE.read(cdoObject);

    for (Iterator<InternalCDOObject> it = FSMUtil.iterator(cdoObject.eContents(), (InternalCDOView)view); it.hasNext();)
    {
      InternalCDOObject content = it.next();
      load(content, view);
    }
  }

  /**
   * @since 2.0
   */
  public static EObject getEObject(EObject object)
  {
    if (object instanceof InternalCDOObject)
    {
      return ((InternalCDOObject)object).cdoInternalInstance();
    }

    return object;
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EObject object)
  {
    if (object instanceof CDOObject)
    {
      return (CDOObject)object;
    }

    return FSMUtil.adaptLegacy((InternalEObject)object);
  }

  /**
   * @since 2.0
   */
  public static CDORevision getRevisionByVersion(CDOObject object, int version)
  {
    if (FSMUtil.isTransient(object))
    {
      return null;
    }

    CDORevision revision = CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
    return getRevisionByVersion(object, revision.getBranch(), version, revision);
  }

  /**
   * @since 3.0
   */
  public static CDORevision getRevisionByVersion(CDOObject object, CDOBranch branch, int version)
  {
    if (FSMUtil.isTransient(object))
    {
      return null;
    }

    CDORevision revision = CDOStateMachine.INSTANCE.read((InternalCDOObject)object);
    return getRevisionByVersion(object, branch, version, revision);
  }

  private static CDORevision getRevisionByVersion(CDOObject object, CDOBranch branch, int version, CDORevision revision)
  {
    if (revision.getVersion() != version)
    {
      CDOSession session = object.cdoView().getSession();
      if (!session.getRepositoryInfo().isSupportingAudits())
      {
        throw new IllegalStateException(Messages.getString("CDOUtil.0")); //$NON-NLS-1$
      }

      revision = session.getRevisionManager().getRevisionByVersion(object.cdoID(), branch.getVersion(version), 0, true);
    }

    return revision;
  }

  /**
   * @since 2.0
   */
  public static EList<Resource> getResources(ResourceSet resourceSet)
  {
    EList<Resource> result = new BasicEList<Resource>();
    EList<Resource> resources = resourceSet.getResources();
    for (Resource resource : resources)
    {
      if (resource instanceof CDOResource)
      {
        CDOResource cdoResource = (CDOResource)resource;
        if (cdoResource.isRoot())
        {
          continue;
        }
      }

      result.add(resource);
    }

    return result;
  }

  /**
   * Returns <code>true</code> if the given {@link CDOSession session} contains a dirty {@link CDOTransaction
   * transaction}, <code>false</code> otherwise.
   *
   * @since 2.0
   * @see CDOTransaction
   */
  public static boolean isSessionDirty(CDOSession session)
  {
    for (CDOView view : session.getElements())
    {
      if (view.isDirty())
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @since 3.0
   * @deprecated As of 4.0 use CDOView.isInvalidationRunnerActive()
   */
  @Deprecated
  public static boolean isInvalidationRunnerActive()
  {
    throw new UnsupportedOperationException("Use CDOView.isInvalidationRunnerActive()");
  }

  /**
   * @since 3.0
   */
  public static boolean isLegacyObject(EObject object)
  {
    return object instanceof CDOObjectWrapper;
  }

  /**
   * @since 3.0
   */
  public static boolean isLegacyModeDefault()
  {
    return legacyModeDefault.get();
  }

  /**
   * @since 3.0
   */
  public static void setLegacyModeDefault(boolean on)
  {
    legacyModeDefault.set(on);
  }

  /**
   * @since 4.0
   */
  public static void setCredentialsProvider(URI uri, IPasswordCredentialsProvider provider)
  {
    CDOURIData data = new CDOURIData(uri);
    data.setUserName(null);
    data.setPassWord(null);
    data.setResourcePath(null);
    data.setBranchPath(null);
    data.setTimeStamp(CDOBranchPoint.UNSPECIFIED_DATE);
    data.setTransactional(false);

    String resource = data.toString();
    IPluginContainer.INSTANCE.putElement("org.eclipse.net4j.util.credentialsProviders", "password", resource, provider);
  }
}
