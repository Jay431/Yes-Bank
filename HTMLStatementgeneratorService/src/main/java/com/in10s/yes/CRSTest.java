package com.in10s.yes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author raghuram
 */

public class CRSTest {
	
	public static boolean validateKey(String value, String strRegexprn) {
		// logger.info("in valiodate method()");
		String line = value;
		boolean b = false;

		// logger.info("value :=" + line);

		if (strRegexprn.equalsIgnoreCase("NO")) {

			return true;

		} else {
			String pattern = strRegexprn;
			// logger.info("Regular Expression:=" + pattern);
			// Create a Pattern object
			Pattern r = Pattern.compile(pattern);

			// Now create matcher object.
			if (line != null) {

				if (!line.isEmpty()) {
					if (line.length() == 50) {
						Matcher m = r.matcher(line);
						if (m.find()) {

							return true;
						} else {

							return false;
							// System.out.println("NO MATCH");
						}

					} else {
						return false;
					}
				} else {

					return b;
				}

			} else {

				return b;
			}

		}
		
	}
}
