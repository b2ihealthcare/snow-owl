/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.common.id;

import java.io.Serializable;
import java.lang.reflect.Array;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.net4j.util.ImplementationError;

/**
 * Identifies CDO objects uniquely in a CDO {@link CDOCommonRepository repository}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link CDOID.Type}
 */
public interface CDOID extends Serializable, Comparable<CDOID>
{
  public static final CDOID NULL = org.eclipse.emf.cdo.internal.common.id.CDOIDNullImpl.INSTANCE;

  public Type getType();

  public boolean isNull();

  public boolean isObject();

  public boolean isTemporary();

  /**
   * @since 3.0
   */
  public boolean isDangling();

  /**
   * @since 2.0
   */
  public boolean isExternal();

  /**
   * @since 2.0
   */
  public String toURIFragment();

  /**
   * Enumerates the possible types of CDO {@link CDOID IDs}.
   * 
   * @author Eike Stepper
   */
  public enum Type
  {
    NULL('N'), //
    OBJECT(' '), // Superceded by ObjectType.getID()

    /**
     * @since 2.0
     */
    EXTERNAL_OBJECT('E'),

    /**
     * @since 3.0
     */
    DANGLING_OBJECT('D'),

    /**
     * @since 2.0
     */
    EXTERNAL_TEMP_OBJECT('e'), //
    TEMP_OBJECT('t');

    private static Enum<?>[] chars;

    private char id;

    private Type(char id)
    {
      registerChar(id, this);
      this.id = id;
    }

    private static void registerChar(char id, Enum<?> literal)
    {
      if (chars == null)
      {
        chars = (Enum<?>[])Array.newInstance(Enum.class, id + 1);
      }
      else if (chars.length < id)
      {
        Enum<?>[] newChars = (Enum<?>[])Array.newInstance(Enum.class, id + 1);
        System.arraycopy(chars, 0, newChars, 0, chars.length);
        chars = newChars;
      }

      if (chars[id] != null)
      {
        throw new ImplementationError("Duplicate id: " + id);
      }

      chars[id] = literal;
    }

    /**
     * @since 4.0
     */
    public static Enum<?> getLiteral(char id)
    {
      return chars[id];
    }

    /**
     * @since 4.0
     */
    public char getID()
    {
      return id;
    }
  }

  /**
   * Enumerates the possible <b>sub</b> types of CDO {@link CDOID IDs} with the main type {@link Type#OBJECT OBJECT}.
   * 
   * @author Eike Stepper
   * @since 3.0
   */
  public enum ObjectType
  {
    LONG('L'), //
    STRING('S'), //
    LONG_WITH_CLASSIFIER('l'), //
    STRING_WITH_CLASSIFIER('s'), //
    UUID('U');

    private char id;

    private ObjectType(char id)
    {
      Type.registerChar(id, this);
      this.id = id;
    }

    /**
     * @since 4.0
     */
    public char getID()
    {
      return id;
    }
  }
}
