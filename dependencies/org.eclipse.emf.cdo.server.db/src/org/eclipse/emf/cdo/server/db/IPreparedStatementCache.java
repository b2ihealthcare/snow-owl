/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Caches JDBC {@link PreparedStatement statements} according to given {@link ReuseProbability reuse probabilities}.
 *
 * @author Stefan Winkler
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPreparedStatementCache
{
  public void setConnection(Connection connection);

  public PreparedStatement getPreparedStatement(String sql, ReuseProbability reuseProbability);

  public void releasePreparedStatement(PreparedStatement ps);

  /**
   * An enum for the degree of probability to which a prepared statement is reused later on. This is used for managing
   * the cache of prepared statements so that statements which are more likely reused are kept in the cache longer. Rule
   * of thumb:
   * <ul>
   * <li>For global statements which are used regularly (such as lookup object in cdo_objects) use
   * {@link ReuseProbability#MAX MAX}.
   * <li>For constant object-specific statements which are used regularly use {@link ReuseProbability#HIGH HIGH}.
   * <li>For object-specific statements which are assembled from constants which are used regularly use
   * {@link ReuseProbability#MEDIUM MEDIUM}.
   * <li>For all other dynamic statements, like queries, use {@link ReuseProbability#LOW LOW}
   * </ul>
   *
   * @author Stefan Winkler
   * @since 2.0
   * @noextend This interface is not intended to be extended by clients.
   */
  public static enum ReuseProbability
  {
    MAX, HIGH, MEDIUM, LOW;
  }
}
