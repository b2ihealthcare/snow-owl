/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.ServerCDOView;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.syncing.FailoverParticipant;
import org.eclipse.emf.cdo.internal.server.syncing.OfflineClone;
import org.eclipse.emf.cdo.internal.server.syncing.RepositorySynchronizer;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalRepositorySynchronizer;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Various static methods that may help with CDO {@link IRepository repositories} and server-side {@link CDOView views}.
 *
 * @author Eike Stepper
 * @apiviz.exclude
 */
public final class CDOServerUtil
{
  private CDOServerUtil()
  {
  }

  /**
   * @since 4.0
   */
  public static CDOView openView(ISession session, CDOBranchPoint branchPoint, boolean legacyModeEnabled,
      CDORevisionProvider revisionProvider)
  {
    return new ServerCDOView((InternalSession)session, branchPoint, legacyModeEnabled, revisionProvider);
  }

  /**
   * @since 4.0
   */
  public static CDOView openView(ISession session, CDOBranchPoint branchPoint, boolean legacyModeEnabled)
  {
    CDORevisionManager revisionManager = session.getManager().getRepository().getRevisionManager();
    CDORevisionProvider revisionProvider = new ManagedRevisionProvider(revisionManager, branchPoint);
    return new ServerCDOView((InternalSession)session, branchPoint, legacyModeEnabled, revisionProvider);
  }

  /**
   * @since 4.0
   */
  public static CDOView openView(IView view, boolean legacyModeEnabled)
  {
    ISession session = view.getSession();
    CDOBranchPoint branchPoint = CDOBranchUtil.copyBranchPoint(view);
    return openView(session, branchPoint, legacyModeEnabled, view);
  }

  /**
   * @since 4.0
   */
  public static CDOView openView(IStoreAccessor.CommitContext commitContext, boolean legacyModeEnabled)
  {
    ISession session = commitContext.getTransaction().getSession();
    CDOBranchPoint branchPoint = commitContext.getBranchPoint();
    return openView(session, branchPoint, legacyModeEnabled, commitContext);
  }

  /**
   * @since 3.0
   * @deprecated Not yet supported.
   */
  @Deprecated
  public static org.eclipse.emf.cdo.server.embedded.CDOSessionConfiguration createSessionConfiguration()
  {
    return new org.eclipse.emf.cdo.internal.server.embedded.EmbeddedClientSessionConfiguration();
  }

  /**
   * @since 3.0
   */
  public static ISessionManager createSessionManager()
  {
    return new SessionManager();
  }

  public static IRepository createRepository(String name, IStore store, Map<String, String> props)
  {
    Repository repository = new Repository.Default();
    initRepository(repository, name, store, props);
    return repository;
  }

  /**
   * @since 3.0
   */
  public static IRepositorySynchronizer createRepositorySynchronizer(
      CDOSessionConfigurationFactory remoteSessionConfigurationFactory)
  {
    RepositorySynchronizer synchronizer = new RepositorySynchronizer();
    synchronizer.setRemoteSessionConfigurationFactory(remoteSessionConfigurationFactory);
    return synchronizer;
  }

  /**
   * @since 3.0
   */
  public static ISynchronizableRepository createOfflineClone(String name, IStore store, Map<String, String> props,
      IRepositorySynchronizer synchronizer)
  {
    OfflineClone repository = new OfflineClone();
    initRepository(repository, name, store, props);
    repository.setSynchronizer((InternalRepositorySynchronizer)synchronizer);
    return repository;
  }

  /**
   * @since 4.0
   */
  public static ISynchronizableRepository createFailoverParticipant(String name, IStore store,
      Map<String, String> props, IRepositorySynchronizer synchronizer, boolean master, boolean allowBackupCommits)
  {
    FailoverParticipant repository = new FailoverParticipant();
    initRepository(repository, name, store, props);
    repository.setSynchronizer((InternalRepositorySynchronizer)synchronizer);
    repository.setType(master ? CDOCommonRepository.Type.MASTER : CDOCommonRepository.Type.BACKUP);
    return repository;
  }

  /**
   * @since 3.0
   */
  public static ISynchronizableRepository createFailoverParticipant(String name, IStore store,
      Map<String, String> props, IRepositorySynchronizer synchronizer, boolean master)
  {
    return createFailoverParticipant(name, store, props, synchronizer, master, false);
  }

  /**
   * @since 4.0
   */
  public static ISynchronizableRepository createFailoverParticipant(String name, IStore store,
      Map<String, String> props, IRepositorySynchronizer synchronizer)
  {
    return createFailoverParticipant(name, store, props, synchronizer, false);
  }

  /**
   * @since 4.0
   */
  public static ISynchronizableRepository createFailoverParticipant(String name, IStore store, Map<String, String> props)
  {
    return createFailoverParticipant(name, store, props, null);
  }

  private static void initRepository(Repository repository, String name, IStore store, Map<String, String> props)
  {
    repository.setName(name);
    repository.setStore((InternalStore)store);
    repository.setProperties(props);
  }

  public static void addRepository(IManagedContainer container, IRepository repository)
  {
    String productGroup = RepositoryFactory.PRODUCT_GROUP;
    String type = RepositoryFactory.TYPE;
    String name = repository.getName();

    container.putElement(productGroup, type, name, repository);
    LifecycleUtil.activate(repository);
  }

  public static IRepository getRepository(IManagedContainer container, String name)
  {
    return RepositoryFactory.get(container, name);
  }

  public static Element getRepositoryConfig(String repositoryName) throws ParserConfigurationException, SAXException,
      IOException
  {
    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo-server.xml"); //$NON-NLS-1$

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(configFile);
    NodeList elements = document.getElementsByTagName("repository"); //$NON-NLS-1$
    for (int i = 0; i < elements.getLength(); i++)
    {
      Node node = elements.item(i);
      if (node instanceof Element)
      {
        Element element = (Element)node;
        String name = element.getAttribute("name"); //$NON-NLS-1$
        if (ObjectUtil.equals(name, repositoryName))
        {
          return element;
        }
      }
    }

    throw new IllegalStateException("Repository config not found: " + repositoryName); //$NON-NLS-1$
  }

  /**
   * An abstract {@link IRepository.ReadAccessHandler read-access handler} that grants or denies access to single
   * {@link CDORevision revisions}.
   *
   * @author Eike Stepper
   * @since 2.0
   * @apiviz.exclude
   */
  public static abstract class RepositoryReadAccessValidator implements IRepository.ReadAccessHandler
  {
    public RepositoryReadAccessValidator()
    {
    }

    public void handleRevisionsBeforeSending(ISession session, CDORevision[] revisions,
        List<CDORevision> additionalRevisions) throws RuntimeException
    {
      List<String> violations = new ArrayList<String>();
      for (CDORevision revision : revisions)
      {
        String violation = validate(session, revision);
        if (violation != null)
        {
          violations.add(violation);
        }
      }

      if (!violations.isEmpty())
      {
        throwException(session, violations);
      }

      for (Iterator<CDORevision> it = additionalRevisions.iterator(); it.hasNext();)
      {
        CDORevision revision = it.next();
        String violation = validate(session, revision);
        if (violation != null)
        {
          OM.LOG.info("Revision can not be delivered to " + session + ": " + violation); //$NON-NLS-1$ //$NON-NLS-2$
          it.remove();
        }
      }
    }

    protected void throwException(ISession session, List<String> violations) throws RuntimeException
    {
      StringBuilder builder = new StringBuilder();
      builder.append("Revisions can not be delivered to "); //$NON-NLS-1$
      builder.append(session);
      builder.append(":"); //$NON-NLS-1$
      for (String violation : violations)
      {
        builder.append("\n- "); //$NON-NLS-1$
        builder.append(violation);
      }

      throwException(builder.toString());
    }

    protected void throwException(String message) throws RuntimeException
    {
      throw new IllegalStateException(message);
    }

    protected abstract String validate(ISession session, CDORevision revision);
  }
}
