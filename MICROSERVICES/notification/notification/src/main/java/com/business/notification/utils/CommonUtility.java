package com.business.notification.utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

@Slf4j
public class CommonUtility {

	private static final String PATTERN = "#.0000";
	private static final String HALF_EVEN = "HALF_EVEN";
	private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24l;

	public static boolean isNullOrEmpty(final Object arg) {
		if (null == arg) {
			return true;
		}
		if (arg instanceof String) {
			final String lArg = (String) arg;
			return !StringUtils.isNotBlank(lArg);
		}
		if (arg instanceof Collection<?>) {
			return ((Collection<?>) arg).isEmpty();
		}
		if (arg instanceof Map<?, ?>) {
			return ((Map<?, ?>) arg).isEmpty();
		}
		if (arg instanceof Serializable) {
			return arg == null;
		}
		// Add your implementation
		return false;
	}
}
