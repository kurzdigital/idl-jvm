# IDL Parser for the JVM

Parse an [International Driver License][idl].

## How to use

### Reading the barcode

IDLs are usually encoded in a [PDF417][pdf417] barcode.
Reading this barcode is not part of this library.

Here are some popular barcode scanning libraries:

* On Android, you can use the [Google Code Scanner][codescanner] from ML Kit.
* If you want more control over the scanning process, you can use ML Kit's
	[barcode scanning API][vision] on Android and/or iOS.
* For everything on the JVM, you might want to use the [ZXing][zxing] barcode
	scanning library.
* For other platforms, there is an excellent [C++ fork][zxingcpp] of the ZXing
	library that runs on a variety of systems. Including
	[Android][barcodescannerview].

### Parsing IDL data

To parse the `String` data from a barcode scanning library do:

```java
import com.kurzdigital.idl.IdlParser;

IdlInfo info = IdlParser.parse(data);
```

If the data cannot be parsed, `IdlParser.parse()` will return `null`.

`IdlInfo` contains these members:

```java
public final String iin; // Issuer Identification Number
public final String raw; // just the raw data you put in
public final LinkedHashMap<String, String> elements; // Element ID and value
```

### Resolving Element IDs

You can map the most common three letter Element IDs (the keys in the
`LinkedHashMap`) with the `IdlElement` enum:

```java
Assert.assertEquals(IdlElement.COUNTRY, IdlElement.get("DCG"));
```

For example, to print all elements and their corresponding `IdlElement`
mapping:

```java
for (Map.Entry<String, String> entry : info.elements.entrySet()) {
	String key = entry.getKey();
	String value = entry.getValue();
	IdlElement name = IdlElement.get(key);
	System.out.println((name != null ? name : key) " => " + value);
}
```

Conversely, you can use an `IdlElement` to access an element from the
`LinkedHashMap` like this:

```java
String country = info.elements.get(IdlElement.COUNTRY.getId());
```

## How to include

### Gradle

Add the JitPack repository to your root `build.gradle` at the end of
repositories:

```groovy
allprojects {
	repositories {
		// …
		maven { url 'https://jitpack.io' }
	}
}
```

Then add the dependency in your `app/build.gradle`:

```groovy
dependencies {
	// …
	implementation 'com.github.kurzdigital:idl-jvm:1.0.0'
}
```

### Maven

Add the JitPack repository to your `pom.xml`:

```xml
	<repositories>
		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
	</repositories>
```

Add the dependency:

```xml
	<dependency>
		<groupId>com.github.kurzdigital</groupId>
		<artifactId>idl-jvm</artifactId>
		<version>1.0.0</version>
	</dependency>
```

[idl]: http://www.aamva.org/DL-ID-Card-Design-Standard/
[pdf417]: https://en.wikipedia.org/wiki/PDF417
[codescanner]: https://developers.google.com/ml-kit/code-scanner
[vision]: https://developers.google.com/ml-kit/vision/barcode-scanning
[zxing]: https://github.com/zxing/zxing
[zxingcpp]: https://github.com/nu-book/zxing-cpp
[barcodescannerview]: https://github.com/markusfisch/BarcodeScannerView
