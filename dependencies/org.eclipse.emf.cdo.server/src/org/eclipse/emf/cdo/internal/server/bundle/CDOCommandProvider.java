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
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.common.lock.IDurableLockingManager;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class CDOCommandProvider implements CommandProvider
{
  private static final String NEW_LINE = "\r\n"; //$NON-NLS-1$

  private static final String INDENT = "   "; //$NON-NLS-1$

  public CDOCommandProvider(BundleContext bundleContext)
  {
    bundleContext.registerService(CommandProvider.class.getName(), this, null);
  }

  public String getHelp()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("---CDO commands---" + NEW_LINE);
    buffer.append(INDENT + "cdo list - list all active repositories" + NEW_LINE);
    buffer.append(INDENT + "cdo start - start repositories from a config file" + NEW_LINE);
    buffer.append(INDENT + "cdo stop - stop a repository" + NEW_LINE);
    buffer.append(INDENT + "cdo export - export the contents of a repository to an XML file" + NEW_LINE);
    buffer.append(INDENT + "cdo import - import the contents of a repository from an XML file" + NEW_LINE);
    buffer.append(INDENT + "cdo sessions - dump the sessions of a repository" + NEW_LINE);
    buffer.append(INDENT + "cdo packages - dump the packages of a repository" + NEW_LINE);
    buffer.append(INDENT + "cdo branches - dump the branches of a repository" + NEW_LINE);
    buffer.append(INDENT + "cdo locks - dump the durable locking areas of a repository" + NEW_LINE);
    buffer.append(INDENT + "cdo deletelocks - delete a durable locking area of a repository" + NEW_LINE);
    return buffer.toString();
  }

  public Object _cdo(CommandInterpreter interpreter)
  {
    try
    {
      String cmd = interpreter.nextArgument();
      if ("list".equals(cmd))
      {
        list(interpreter);
        return null;
      }

      if ("start".equals(cmd))
      {
        start(interpreter);
        return null;
      }

      if ("stop".equals(cmd))
      {
        stop(interpreter);
        return null;
      }

      if ("export".equals(cmd))
      {
        exportXML(interpreter);
        return null;
      }

      if ("import".equals(cmd))
      {
        importXML(interpreter);
        return null;
      }

      if ("sessions".equals(cmd))
      {
        sessions(interpreter);
        return null;
      }

      if ("packages".equals(cmd))
      {
        packages(interpreter);
        return null;
      }

      if ("branches".equals(cmd))
      {
        branches(interpreter);
        return null;
      }

      if ("locks".equals(cmd))
      {
        locks(interpreter);
        return null;
      }

      if ("deletelocks".equals(cmd))
      {
        deleteLocks(interpreter);
        return null;
      }

      interpreter.println(getHelp());
    }
    catch (CommandException ex)
    {
      interpreter.println(ex.getMessage());
    }
    catch (Exception ex)
    {
      interpreter.printStackTrace(ex);
    }

    return null;
  }

  protected void list(CommandInterpreter interpreter) throws Exception
  {
    IManagedContainer container = CDOServerApplication.getContainer();
    for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        interpreter.println(repository.getName());
      }
    }
  }

  protected void start(CommandInterpreter interpreter) throws Exception
  {
    String configFile = nextArgument(interpreter, "Syntax: cdo start <config-file>");

    IManagedContainer container = CDOServerApplication.getContainer();
    RepositoryConfigurator repositoryConfigurator = new RepositoryConfigurator(container);
    IRepository[] repositories = repositoryConfigurator.configure(new File(configFile));

    interpreter.println("Repositories started:");
    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        interpreter.println(repository.getName());
      }
    }
  }

  protected void stop(CommandInterpreter interpreter) throws Exception
  {
    InternalRepository repository = getRepository(interpreter, "Syntax: cdo stop <repository-name>");
    LifecycleUtil.deactivate(repository);
    interpreter.println("Repository stopped");
  }

  protected void exportXML(CommandInterpreter interpreter) throws Exception
  {
    String syntax = "Syntax: cdo export <repository-name> <export-file>";
    InternalRepository repository = getRepository(interpreter, syntax);
    String exportFile = nextArgument(interpreter, syntax);
    OutputStream out = null;

    try
    {
      out = new FileOutputStream(exportFile);

      CDOServerExporter.XML exporter = new CDOServerExporter.XML(repository);
      exporter.exportRepository(out);
      interpreter.println("Repository exported");
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  protected void importXML(CommandInterpreter interpreter) throws Exception
  {
    String syntax = "Syntax: cdo import <repository-name> <import-file>";
    InternalRepository repository = getRepository(interpreter, syntax);
    String importFile = nextArgument(interpreter, syntax);
    InputStream in = null;

    try
    {
      in = new FileInputStream(importFile);
      LifecycleUtil.deactivate(repository);

      CDOServerImporter.XML importer = new CDOServerImporter.XML(repository);
      importer.importRepository(in);

      IManagedContainer container = CDOServerApplication.getContainer();
      CDOServerUtil.addRepository(container, repository);

      interpreter.println("Repository imported");
    }
    finally
    {
      IOUtil.close(in);
    }
  }

  protected void sessions(CommandInterpreter interpreter)
  {
    InternalRepository repository = getRepository(interpreter, "Syntax: cdo sessions <repository-name>");
    InternalSessionManager sessionManager = repository.getSessionManager();
    for (InternalSession session : sessionManager.getSessions())
    {
      interpreter.println(session);
      for (InternalView view : session.getViews())
      {
        interpreter.println(INDENT + view);
      }
    }
  }

  protected void packages(CommandInterpreter interpreter)
  {
    InternalRepository repository = getRepository(interpreter, "Syntax: cdo packages <repository-name>");
    InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
    for (InternalCDOPackageUnit packageUnit : packageRegistry.getPackageUnits())
    {
      interpreter.println(packageUnit);
      for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        interpreter.println(INDENT + packageInfo);
      }
    }
  }

  protected void branches(CommandInterpreter interpreter)
  {
    InternalRepository repository = getRepository(interpreter, "Syntax: cdo branches <repository-name>");
    branches(interpreter, repository.getBranchManager().getMainBranch(), "");
  }

  protected void locks(final CommandInterpreter interpreter)
  {
    final InternalRepository repository = getRepository(interpreter,
        "Syntax: cdo locks <repository-name> [<username-prefix>]");
    final String userIDPrefix = nextArgument(interpreter, null);

    new WithAccessor()
    {
      @Override
      protected void doExecute(IStoreAccessor accessor)
      {
        repository.getLockingManager().getLockAreas(userIDPrefix, new IDurableLockingManager.LockArea.Handler()
        {
          public boolean handleLockArea(LockArea area)
          {
            interpreter.println(area.getDurableLockingID());
            interpreter.println(INDENT + "userID = " + area.getUserID());
            interpreter.println(INDENT + "branch = " + area.getBranch());
            interpreter.println(INDENT + "timeStamp = " + CDOCommonUtil.formatTimeStamp(area.getTimeStamp()));
            interpreter.println(INDENT + "readOnly = " + area.isReadOnly());
            interpreter.println(INDENT + "locks = " + area.getLocks());
            return true;
          }
        });
      }
    }.execute(repository);
  }

  protected void deleteLocks(CommandInterpreter interpreter)
  {
    String syntax = "Syntax: cdo deletelocks <repository-name> <area-id>";
    final InternalRepository repository = getRepository(interpreter, syntax);
    final String durableLockingID = nextArgument(interpreter, syntax);

    new WithAccessor()
    {
      @Override
      protected void doExecute(IStoreAccessor accessor)
      {
        repository.getLockingManager().deleteLockArea(durableLockingID);
      }
    }.execute(repository);
  }

  private void branches(CommandInterpreter interpreter, InternalCDOBranch branch, String prefix)
  {
    interpreter.println(prefix + branch);
    prefix += INDENT;
    for (InternalCDOBranch child : branch.getBranches())
    {
      branches(interpreter, child, prefix);
    }
  }

  private String nextArgument(CommandInterpreter interpreter, String syntax)
  {
    String argument = interpreter.nextArgument();
    if (argument == null && syntax != null)
    {
      throw new CommandException(syntax);
    }

    return argument;
  }

  private InternalRepository getRepository(CommandInterpreter interpreter, String syntax)
  {
    String repositoryName = nextArgument(interpreter, syntax);
    InternalRepository repository = getRepository(repositoryName);
    if (repository == null)
    {
      throw new CommandException("Repository not found: " + repositoryName);
    }

    return repository;
  }

  private InternalRepository getRepository(String name)
  {
    IManagedContainer container = CDOServerApplication.getContainer();
    for (Object element : container.getElements(RepositoryFactory.PRODUCT_GROUP))
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        if (repository.getName().equals(name))
        {
          return repository;
        }
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  protected static abstract class WithAccessor
  {
    public void execute(InternalRepository repository)
    {
      IStoreAccessor accessor = repository.getStore().getReader(null);
      StoreThreadLocal.setAccessor(accessor);

      try
      {
        doExecute(accessor);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    protected abstract void doExecute(IStoreAccessor accessor);
  }

  /**
   * @author Eike Stepper
   */
  private static final class CommandException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public CommandException(String message)
    {
      super(message);
    }
  }
}
