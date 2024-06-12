package com.kurzdigital.idl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.Assert;

public class IdlParserTest {
	@Test
	public void resolve() {
		Assert.assertEquals(IdlElement.COUNTRY, IdlElement.get("DCG"));
	}

	@Test
	public void parse() throws IOException {
		String data = new String(Files.readAllBytes(
				Paths.get("src/test/resources/sample_idl")));
		IdlInfo info = IdlParser.parse(data);
		Assert.assertEquals("030001", info.iin);
		Assert.assertEquals("DLUSA030001", info.getSummary());
		Map<String, String> elements = info.elements;
		Assert.assertEquals(11, elements.size());
		Assert.assertEquals("USA",
				elements.get(IdlElement.COUNTRY.getId()));
		Assert.assertEquals("WASHINGTON",
				elements.get(IdlElement.ADDRESS_CITY.getId()));
		Assert.assertEquals("ARCHINE DE GIOVANNI",
				elements.get(IdlElement.CUSTOMER_FAMILY_NAME.getId()));
		Assert.assertEquals("RENATA",
				elements.get(IdlElement.CUSTOMER_FIRST_NAME_ALT.getId()));
		Assert.assertEquals("M", elements.get(IdlElement.SEX.getId()));
		Assert.assertEquals("12/11/1945",
				elements.get(IdlElement.DATE_OF_BIRTH.getId()));
		Assert.assertEquals("08/17/2010",
				elements.get(IdlElement.DATE_OF_ISSUE.getId()));
		Assert.assertEquals("NONE",
				elements.get(IdlElement.EYE_COLOR.getId()));
		Assert.assertEquals("63 in",
				elements.get(IdlElement.BODY_HEIGHT.getId()));
	}
}
