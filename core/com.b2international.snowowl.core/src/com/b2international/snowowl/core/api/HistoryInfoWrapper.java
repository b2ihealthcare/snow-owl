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
package com.b2international.snowowl.core.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class responsible for wrapping and group {@link HistoryInfo}s by date. 
 * 
 */
public final class HistoryInfoWrapper {

	private static HistoryInfoWrapper INSTANCE;
	private HistoryInfoComparator comparator;
	
	private HistoryInfoWrapper() {
		this.comparator = new HistoryInfoComparator();
	}
	
	public static HistoryInfoWrapper getInstance() {
		if (INSTANCE == null) {
			synchronized (HistoryInfoWrapper.class) {
				if (INSTANCE == null) {
					INSTANCE = new  HistoryInfoWrapper(); 
				}
			}
		}
		return INSTANCE;
	}
	
	public HistoryInfoComparator getComparator() {
		return comparator;
	}
	
	/**
	 * Wraps and groups {@link HistoryInfo}s by date.
	 * @param infos the passed in {@link HistoryInfo}s to wrap and group.
	 * @return a {@link Map} where the {@link HistoryDateStatus} keys determines the HistoryInfo revision dates.
	 */
	public Map<HistoryDateStatus, List<IHistoryInfo>> wrapHistoryInfos(final List<IHistoryInfo> infos) {
		final Map<HistoryDateStatus, List<IHistoryInfo>> wrappedInfos = new TreeMap<HistoryDateStatus, List<IHistoryInfo>>(new HistoryDateStatusComparator());
		final List<HistoryDateCategory> historyDateCategories = getHistoryDateCategory(infos);
		
		for (final HistoryDateCategory category : historyDateCategories) {
			if (category.hasInfos()) {
				wrappedInfos.put(category.getStatus(), category.getInfos());
			}
		}
		
		for (final List<IHistoryInfo> list : wrappedInfos.values()) {
			Collections.sort(list, new HistoryInfoComparator());
		}

		return wrappedInfos;
	}
	
	public enum HistoryDateStatus {
		
		TODAY("Today"),
		YESTERDAY("Yesterday"),
		THIS_WEEK("This week"),
		LAST_WEEK("Last week"),
		THIS_MONTH("This month"),
		OLDER_THAN_THIS_MONTH("Older than this month");
		
		private final String name;
		
		private HistoryDateStatus(final String name) {
			this.name = name;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return name;
		}
	}
	
	private List<HistoryDateCategory> getHistoryDateCategory(final List<IHistoryInfo> infos) {

		final List<HistoryDateCategory> categories = new LinkedList<HistoryInfoWrapper.HistoryDateCategory>();
		
		//get a calendar instance initialized to the current time
		final GregorianCalendar currentCalendar = new GregorianCalendar();
		categories.add(new HistoryDateCategory(HistoryDateStatus.TODAY, currentCalendar, null));
		
		//get yesterday
		final GregorianCalendar yesterdayCalendar = new GregorianCalendar();
		yesterdayCalendar.roll(GregorianCalendar.DAY_OF_YEAR, -1);
		categories.add(new HistoryDateCategory(HistoryDateStatus.YESTERDAY, yesterdayCalendar, null));
		
		//get this week
		final GregorianCalendar weekCalendar = new GregorianCalendar();
		weekCalendar.set(GregorianCalendar.DAY_OF_WEEK, 1);
		categories.add(new HistoryDateCategory(HistoryDateStatus.THIS_WEEK, weekCalendar, yesterdayCalendar));
		
		//get this last week
		final GregorianCalendar lastWeekCalendar = new GregorianCalendar();
		lastWeekCalendar.set(GregorianCalendar.DAY_OF_WEEK, 1);
		lastWeekCalendar.roll(GregorianCalendar.DAY_OF_YEAR, -7);
		categories.add(new HistoryDateCategory(HistoryDateStatus.LAST_WEEK, lastWeekCalendar, weekCalendar));
		
		//get this month
		final GregorianCalendar monthCalendar = new GregorianCalendar();
		monthCalendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
		categories.add(new HistoryDateCategory(HistoryDateStatus.THIS_MONTH, monthCalendar, lastWeekCalendar));
		
		//everything before this month is previous
		categories.add(new HistoryDateCategory(HistoryDateStatus.OLDER_THAN_THIS_MONTH, null, monthCalendar));
		
		for (final HistoryDateCategory category : categories) {
			category.collectInfos(infos);
		}
		return categories;
	}
	
	private final class HistoryDateCategory {
		
		private final HistoryDateStatus status;
		private final GregorianCalendar fromDate;
		private final GregorianCalendar toDate;
		private final List<IHistoryInfo> infos = new ArrayList<IHistoryInfo>();
		
		private HistoryDateCategory(final HistoryDateStatus status, final GregorianCalendar fromDate, final GregorianCalendar toDate) {
			this.status = status;
			this.fromDate = fromDate;
			this.toDate = toDate;
		}
		
		private HistoryDateStatus getStatus() {
			return status;
		}
		
		private boolean collectInfos(final List<IHistoryInfo> infos) {
			for (final IHistoryInfo info : infos) {
				//get the current file revision's date
				final GregorianCalendar fileRevDate = new GregorianCalendar();
				fileRevDate.setTimeInMillis(info.getTimeStamp());
				
				final int fileRevDay = fileRevDate.get(GregorianCalendar.DAY_OF_YEAR);
				final int fileRevYear = fileRevDate.get(GregorianCalendar.YEAR);
				
				if (fromDate == null){
					//check to see if this revision is within the toDate range
					if (((fileRevDay < toDate.get(GregorianCalendar.DAY_OF_YEAR)) && (fileRevYear == toDate.get(GregorianCalendar.YEAR))) || (fileRevYear < toDate.get(GregorianCalendar.YEAR))){
						this.infos.add(info);
					}
				} else if (toDate == null){
					//check to see if this revision falls on the same day as the fromDate
					if ((fileRevDay == fromDate.get(GregorianCalendar.DAY_OF_YEAR)) && (fileRevYear == fromDate.get(GregorianCalendar.YEAR))){
						this.infos.add(info);
					}
				} else {
					//check the range
					if ((fileRevYear >= fromDate.get(GregorianCalendar.YEAR)) && (fileRevYear <= toDate.get(GregorianCalendar.YEAR)) &&
						(fileRevDay >= fromDate.get(GregorianCalendar.DAY_OF_YEAR)) && (fileRevDay < toDate.get(GregorianCalendar.DAY_OF_YEAR))) {
						this.infos.add(info);
					}
				}	
			}
			return this.infos.isEmpty() ? false : true;
		}
		
		private boolean hasInfos() {
			return infos != null && !infos.isEmpty() ? true : false;
		}
		
		private List<IHistoryInfo> getInfos() {
			return infos;
		}
		
	}
	
	private final class HistoryDateStatusComparator implements Comparator<HistoryDateStatus> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final HistoryDateStatus o1, final HistoryDateStatus o2) {
			final int v1 = getVale(o1);
			final int v2 = getVale(o2);
			return v1 != v2 ? v1 < v2 ? -1 : 1 : 0;
		}
		
		private int getVale(final HistoryDateStatus status) {
			switch(status) {
				case TODAY: return 0;
				case YESTERDAY: return 1;
				case THIS_WEEK: return 2;
				case LAST_WEEK: return 3;
				case THIS_MONTH: return 4;
				case OLDER_THAN_THIS_MONTH: return 5;
				default: throw new IllegalArgumentException();
			}
		}
	}
	
	public final class HistoryInfoComparator implements Comparator<IHistoryInfo> {

		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final IHistoryInfo o1, final IHistoryInfo o2) {
			final long t1 = o1.getTimeStamp();
			final long t2 = o2.getTimeStamp();
			return t1 != t2 ? t1 < t2 ? 1 : -1 : 0;
		}
	}
	
	
}