package com.matchandtrade.doc.util;

import com.matchandtrade.exception.DocMakerException;

public class AssertUtil {

	public static void isEquals(Object expected, Object actual) {
		if (!objectsAreNull(expected, actual) && expected != null) {
			if (!expected.equals(actual)) {
				throw new DocMakerException("Expected [" + expected + "] but found [" + actual + "].");
			}
		}
	}
	
	private static boolean objectsAreNull(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		} else {
			return false;
		}
	}

	public static void isTrue(boolean b) {
		if (b != true) {
			throw new DocMakerException("Expected true but value was false.");
		}
	}
}
