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
package com.b2international.snowowl.datastore.version;

import static com.b2international.commons.status.Statuses.ok;
import static com.b2international.commons.status.Statuses.toSerializable;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IStatus;

import bak.pcj.CharCollection;
import bak.pcj.UnmodifiableCharCollection;
import bak.pcj.set.CharOpenHashSet;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.google.common.collect.Sets;

/**
 * Abstract version name validator.
 * @see IVersionNameValidator
 */
public abstract class VersionNameValidator implements IVersionNameValidator {
	
	/**Reserved characters. Source: <a href=http://msdn.microsoft.com/en-us/library/aa365247%28VS.85%29>Naming Files, Paths, and Namespaces - MSDN</a>*/
	private static final CharCollection RESERVED_CHARACTERS = new UnmodifiableCharCollection(new CharOpenHashSet( new char[] {'<', '>', ':',	'/', '\'', '|',	'?', '*' } ));

	/**Reserved Windows and DOS utility file names. Source: <a href=http://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words>Reserved file names</a>*/
	private static final Collection<String> RESERVED_UTILITY_FILENAMES = Collections.unmodifiableSet(Sets.newHashSet(
			"CON",
			"PRN",
			"AUX",
			"CLOCK$",
			"NUL",
			"COM1",
			"COM2",
			"COM3",
			"COM4",
			"COM5",
			"COM6",
			"COM7",
			"COM8",
			"COM9",
			"LPT1",
			"LPT2",
			"LPT3",
			"LPT4",
			"LPT5",
			"LPT6",
			"LPT7",
			"LPT8",
			"LPT9"
			));
	
	/**Reserved internal NTFS file names. Source: <a href=http://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words>Reserved file names</a>*/
	private static final Collection<String> RESERVED_INTERNAL_NTFS_FILENAMES = Collections.unmodifiableSet(Sets.newHashSet(
			"$Mft",
			"$MftMirr",
			"$LogFile",
			"$Volume",
			"$AttrDef",
			"$Bitmap",
			"$Boot",
			"$BadClus",
			"$Secure",
			"$Upcase",
			"$Extend",
			"$Quota",
			"$ObjId",
			"$Reparse"
			));
	
	/**Application specific reserved words.*/
	private static final Collection<String> APPLICATION_RESERVED_WORDS = Collections.unmodifiableSet(Sets.newHashSet(
			IBranchPath.MAIN_BRANCH,
			ICodeSystemVersion.UNVERSIONED,
			ICodeSystemVersion.INITIAL_STATE
			));
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.version.IVersionNameValidator#validate(java.lang.String)
	 */
	@Override
	public IStatus validate(@Nullable final String versionName) {
		
		try {
			
			checkNotEmpty(versionName);
			checkNotReserved(versionName);
			checkByCharacters(versionName);
			checkUniqueness(versionName);
			checkContainsNonDigitCharacter(versionName);
			
		} catch (final InvalidVersionNameException e) {
			return e.getStatus();
		}
		
		return toSerializable(ok());
	}

	/**Returns with a collection of existing version names.<br>By default returns with an empty collection.*/
	protected Collection<String> getExistingVersionNames() {
		return Collections.emptySet();
	}

	/**Check whether the given version name is a reserved word or not.*/
	private void checkNotReserved(final String versionName) throws InvalidVersionNameException {
		if (isReserved(versionName)) {
			throw new InvalidVersionNameException("'" + versionName + "' is a reserved word.");
		}
	}

	/**Checks the emptiness of the given argument.*/
	private void checkNotEmpty(final String versionName) throws InvalidVersionNameException {
		if (StringUtils.isEmpty(versionName)) {
			throw new InvalidVersionNameException("Version name should be specified.");
		}
	}
	
	/**Validates the uniqueness of the given version name. Returns with a status with error severity if the given name is not unique.*/
	private void checkUniqueness(final String versionName) throws InvalidVersionNameException {
		if (getExistingVersionNames().contains(versionName)) {
			throw new InvalidVersionNameException("Version name should be unique.");
		}
	}
	
	/**Validates the given version name by characters. Returns with an error status if the string contains any white spaces or any of the reserved characters*/
	private void checkByCharacters(final String versionName) throws InvalidVersionNameException {
		
		for (int i = 0; i < versionName.length(); i++) {
			
			final char c = versionName.charAt(i);
			
			if (Character.isWhitespace(c)) {
				throw new InvalidVersionNameException("Version name should not contain any whitespace characters.");
			}
			
			if (RESERVED_CHARACTERS.contains(c)) {
				throw new InvalidVersionNameException("Version name should not contain '" + c + "' character.");
			}
			
		}
	}

	/**Checks whether the given version name argument is a reserved word or not. Returns with {@code true} if reserved.*/
	private boolean isReserved(final String versionName) {
		return RESERVED_INTERNAL_NTFS_FILENAMES.contains(versionName)
				|| RESERVED_UTILITY_FILENAMES.contains(versionName)
				|| APPLICATION_RESERVED_WORDS.contains(versionName);
	}
	
	private void checkContainsNonDigitCharacter(final String versionName) throws InvalidVersionNameException {
		for (char c : versionName.toCharArray()) {
			if (!Character.isDigit(c)) {
				return;
			}
		}
		throw new InvalidVersionNameException("Version name should contain at least one non-digit character.");
	}
	
}