package org.triska.model;

import java.util.Comparator;

public class CameraPicComparatorByDate implements Comparator<CameraPic> {

	@Override
	public int compare(CameraPic o1, CameraPic o2) {
		long t1 = o1.getCurrentTime().getTime();
		long t2 = o2.getCurrentTime().getTime();
		if (t2 > t1)
			return 1;
		else if (t1 > t2)
			return -1;
		else
			return 0;
	}

}
