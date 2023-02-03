package com.kurzdigital.idl;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class IdlInfo implements Serializable {
	public final String iin;
	public final String raw;
	public final LinkedHashMap<String, String> elements;

	public IdlInfo(String iin, String raw,
			LinkedHashMap<String, String> elements) {
		this.iin = iin;
		this.raw = raw;
		this.elements = elements;
	}

	public String getSummary() {
		String country = elements.get("DCG");
		return "DL" +
				(country != null ? country : "") +
				(iin != null ? iin : "");
	}
}
