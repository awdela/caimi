package caimi.common.util;

import java.util.UUID;

public class UUIDUtil {
	
	/**
	 * 
	 */
	public static String genId() {
		UUID uuid = UUID.randomUUID();
		return Base58.compressedUUID(uuid);
	}

	/**
	 * 
	 */
	public static String genId32() {
		UUID uuid = UUID.randomUUID();
		return (uuid.toString().replaceAll("-", "")).toLowerCase();
	}
}
