package com.kurzdigital.idl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdlParser {
	private static final Pattern elementIdPattern = Pattern.compile(
			"^[DZ]{1}[A-Z]{2}$");
	private static final Pattern subFileTypePattern = Pattern.compile(
			"[0-9]{8,}(DL|ID).+?(DL|ID)");
	private static final Pattern datePattern = Pattern.compile(
			"^[0-9]{8}$");
	private static final Pattern iinPattern = Pattern.compile(
			"ANSI\\s+([0-9]{6})");

	// Unfortunately, the format specified in
	// http://www.aamva.org/DL-ID-Card-Design-Standard/
	// does _not_ match the data I-Nigma (or ZXing) returns.
	//
	// The spec says, there must be a header of five bytes
	// ('@', 0x0a, 0x1e, 0x0d, "ANSI ", ...) but the string
	// we get does _not_ contain the characters 0x1e and 0x0d.
	//
	// Because of this inconsistency and because there seem
	// to be deviating standards anyway (between states, and
	// between USA and Canada, just have a look at
	// https://github.com/googlesamples/android-vision/issues/77)
	// the most simplest way to parse this is to use the
	// field separator (LF) to split the string.
	//
	// Data elements come in the form "[ID][DATA]LF".
	// Where [ID] is a three digit element identifier (like DCF, DCS...)
	// and [DATA] is just a string.
	//
	// Each element is terminated by a line feed (LF = 0x10).
	// Because the elements directly follow each other, each element
	// is also prepended by a LF - except for the very first element
	// what directly follows the subfile header (either DL for driver
	// license or ID).
	public static IdlInfo parse(String data) {
		// Believe it or not, some DLs contain null characters *between* data.
		if (data == null ||
				(data = data.trim().replace("\0", "")).length() < 1) {
			return null;
		}

		String raw = data;
		String iin = "";

		// Try to find the Issuer Identification Number (IIN).
		{
			Matcher m = iinPattern.matcher(data);
			if (m.find()) {
				iin = m.group(1);
			}
		}

		LinkedHashMap<String, String> elements = new LinkedHashMap<>();

		if (data.startsWith("@")) {
			Matcher m = subFileTypePattern.matcher(data);
			if (m.find()) {
				String code = m.group(1);
				elements.put("DL", code);
				// Truncate everything before (and including) second DL.
				data = data.substring(m.end(2));
			}
		}

		for (String s : data.split("\\p{Cntrl}")) {
			if (s.length() < 4) {
				continue;
			}
			String label = s.substring(0, 3);
			if (!elementIdPattern.matcher(label).find()) {
				continue;
			}
			String value = s.substring(3).trim();
			if ("DBC".equals(label)) {
				value = resolveSex(value);
			}
			elements.put(label, value);
		}

		if (elements.size() < 1) {
			return null;
		}

		translateDates(elements);

		return new IdlInfo(iin, raw, elements);
	}

	private static String resolveSex(String pattern) {
		if (pattern == null) {
			return "";
		}
		switch (pattern.trim().charAt(0)) {
			case '1': return "M";
			case '2': return "F";
			default: return "";
		}
	}

	private static void translateDates(Map<String, String> elements) {
		String country = elements.get("DCG");
		String[] keys = new String[]{"DBB", "DBA", "DBD"};
		for (String key : keys) {
			String value = elements.get(key);
			if (value == null || value.isEmpty()) {
				continue;
			}
			value = translateDate(value, country);
			elements.put(key, value);
		}
	}

	private static String translateDate(String date, String country) {
		if (datePattern.matcher(date).find() &&
				("US".equals(country) || "USA".equals(country))) {
			// USA uses MMDDCCYY.
			return date.substring(4, 8) +
					date.substring(0, 2) +
					date.substring(2, 4);
		}
		return date;
	}
}
