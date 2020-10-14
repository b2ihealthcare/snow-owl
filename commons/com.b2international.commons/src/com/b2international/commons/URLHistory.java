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
package com.b2international.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class is for storing repository address history.
 */
public class URLHistory {

	private List<String> addresses;
	private static final String DELIMITER = "|";
	private static final int MAX_SIZE = 5;
	private static URLHistory urlHistory;

	/**
	 * Private constructor.
	 */
	private URLHistory() {
		this.addresses = new ArrayList<String>();
	}

	/**
	 * Set addresses.
	 * 
	 * @param addresses list of repository addresses stored as string
	 */
	public void setAddresses(List<String> addresses) {
		this.addresses = null;
		this.addresses = addresses;
	}

	/**
	 * Retrieve addresses.
	 * 
	 * @return list of addresses stores as string
	 */
	public List<String> getAddressesAsList() {
		Collections.reverse(addresses);
		return this.addresses;
	}

	/**
	 * Retrieve addresses as single string (concatenation of list element with
	 * delimiter).
	 * 
	 * @return concatenation of repository address history with delimiter
	 */
	public String getAddressesAsString() {
		StringBuffer buffer = new StringBuffer();
		for (String listElement : this.addresses) {
			if (!listElement.isEmpty()) {
				buffer.append(listElement);
				buffer.append(DELIMITER);
			}
		}
		return new String(buffer);
	}

	/**
	 * Converts a string of repository address histories list of strings and sets
	 * the addresses.
	 * 
	 * @param addresses - concatenation of repository address history with delimiter
	 */
	public void setAddresses(String addresses) {
		StringTokenizer stringTokenizer = new StringTokenizer(addresses, DELIMITER);
		int tokenNumber = stringTokenizer.countTokens();
		List<String> retList = new ArrayList<String>(tokenNumber);
		for (int i = 0; i < tokenNumber; i++) {
			String parsedString = stringTokenizer.nextToken();
			if (!(parsedString == null || parsedString.trim().length() == 0)) {
				retList.add(parsedString);
			}
		}
		this.setAddresses(retList);
	}

	/**
	 * Add a concatenation of repository address to the repository address
	 * histories.
	 * 
	 * @param addresses - concatenation of repository address history with delimiter
	 */
	public void addAddress(String addresses) {
		StringTokenizer stringTokenizer = new StringTokenizer(addresses, DELIMITER);
		int tokenNumber = stringTokenizer.countTokens();
		List<String> retList = new ArrayList<String>(tokenNumber);
		for (int i = 0; i < tokenNumber; i++) {
			String parsedString = stringTokenizer.nextToken();
			if (!(parsedString == null || parsedString.trim().length() == 0)) {
				retList.add(parsedString);
			}
		}
		this.add(retList);
	}

	/**
	 * Add a single repository address to the repository address histories.
	 * 
	 * @param address - a single repository address
	 */
	public void add(String address) {
		if (isFull()) {
			List<String> tempList = new ArrayList<String>(MAX_SIZE + 1);
			tempList = this.addresses;
			tempList.add(address);
			tempList.subList(0, 1).clear();
			this.addresses = tempList;
		} else {
			this.addresses.add(address);
		}
	}

	/**
	 * Add a list of addresses.
	 * 
	 * @param addresses - a list of repository addresses
	 */
	public void add(List<String> addresses) {
		for (String listElement : addresses) {
			add(listElement);
		}
	}

	/**
	 * Checks whether the list is full or not.
	 * 
	 * @return is the queue full
	 */
	public boolean isFull() {
		if (this.addresses.size() == MAX_SIZE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Static method for singleton.
	 * 
	 * @return instance of URLHistory
	 */
	public static URLHistory getInstance() {
		if (urlHistory == null) {
			urlHistory = new URLHistory();
		}
		return urlHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.addresses.toString();
	}

	/**
	 * Get the latest repository address from the queue as string.
	 * 
	 * @return latest repository address
	 */
	public String getServerAddress() {
		if (this.addresses == null) {
			return "";
		} else {
			int momenatryAddressSize = this.addresses.size();
			try {
				int lastIndexOf = this.addresses.get(momenatryAddressSize - 1).lastIndexOf("/");// FIXME check
																								// StringIndexOutOfBoundsException
				return this.addresses.get(momenatryAddressSize - 1).substring(0, lastIndexOf).trim();
			} catch (StringIndexOutOfBoundsException e) {
				return this.addresses.get(momenatryAddressSize - 1);
			}
		}
	}

	/**
	 * Get the latest repository ID from the queue as string.
	 * 
	 * @return latest repository ID
	 */
	public String getRepositoryID() {
		if (this.addresses == null) {
			return "";
		} else {
			int momenatryAddressSize = this.addresses.size();
			try {
				int lastIndexOf = this.addresses.get(momenatryAddressSize - 1).lastIndexOf("/");// FIXME check
																								// StringIndexOutOfBoundsException
				return this.addresses.get(momenatryAddressSize - 1).substring(lastIndexOf + 1).trim();
			} catch (StringIndexOutOfBoundsException e) {
				return this.addresses.get(momenatryAddressSize - 1);
			}
		}
	}

}