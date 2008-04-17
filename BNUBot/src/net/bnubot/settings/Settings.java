/**
 * This file is distributed under the GPL
 * $Id$
 */

package net.bnubot.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import net.bnubot.util.Out;
import net.bnubot.util.SortedProperties;
import net.bnubot.vercheck.CurrentVersion;

public class Settings {
	private static final File propsFile = new File("settings.ini");
	private static final Properties props = new SortedProperties();
	private static boolean anythingChanged = false;

	static {
		if(propsFile.exists()) try {
			props.load(new FileInputStream(propsFile));
		} catch(Exception e) {
			Out.exception(e);
		}
	}

	private static String getKey(String header, String setting) {
		if(header == null)
			return "general_" + setting;
		return header + "_" + setting;
	}

	public static String read(String header, String setting, String defaultValue) {
		String key = getKey(header, setting);
		if(props.containsKey(key))
			return props.getProperty(key);

		write(key, defaultValue);
		return defaultValue;
	}

	public static boolean read(String header, String setting, boolean defaultValue) {
		return Boolean.parseBoolean(read(header, setting, Boolean.toString(defaultValue)));
	}

	public static int read(String header, String setting, int defaultValue) {
		return Integer.parseInt(read(header, setting, Integer.toString(defaultValue)));
	}

	public static long read(String header, String setting, long defaultValue) {
		return Long.parseLong(read(header, setting, Long.toString(defaultValue)));
	}

	/**
	 * This method will not handle a NULL value for defaultValue
	 */
	public static <T extends Enum<T>> T read(String header, String setting, T defaultValue) {
		String readValue = read(header, setting, defaultValue.name());
		try {
			return Enum.valueOf(defaultValue.getDeclaringClass(), readValue);
		} catch(Exception e) {
			Out.error(Settings.class, "Invalid " + defaultValue.getDeclaringClass().getSimpleName() + ": " + readValue);
			return defaultValue;
		}
	}

	public static void write(String header, String setting, String value) {
		write(getKey(header, setting), value);
	}

	private static void write(String key, String value) {
		if(value == null)
			value = new String();

		// Don't allow modification of keys unless they haven't changed
		if(props.containsKey(key) && props.getProperty(key).equals(value))
			return;

		anythingChanged = true;
		Out.debug(Settings.class, "setting " + key + "=" + value);
		props.setProperty(key, value);
	}

	public static void write(String header, String setting, boolean value) {
		write(header, setting, Boolean.toString(value));
	}

	public static void write(String header, String setting, int value) {
		write(header, setting, Integer.toString(value));
	}

	public static void write(String header, String setting, long value) {
		write(header, setting, Long.toString(value));
	}

	public static <T extends Enum<T>> void write(String header, String setting, T value) {
		write(header, setting, value.name());
	}

	public static void store() {
		if(!anythingChanged)
			return;

		Out.debug(Settings.class, "Writing settings.ini");

		try {
			// Generate the comment first, because the settings.ini file could be lost if CurrentVersion.version() fails
			String comment = CurrentVersion.version().toString();
			props.store(new FileOutputStream(propsFile), comment);
			anythingChanged = false;
		} catch (Exception e) {
			Out.fatalException(e);
		}
	}

}
