package caimi.common.util;

import java.util.UUID;

public class UUIDUtil {
	
	/**
	 * ����UUID,BASE58���룬 22 bytes length
	 */
	public static String genId() {
		UUID uuid = UUID.randomUUID();
		return Base58.compressedUUID(uuid);
	}

	/**
	 * ����UUID����"-"�ָ���,Base16����
	 */
	public static String genId32() {
		UUID uuid = UUID.randomUUID();
		return (uuid.toString().replaceAll("-", "")).toLowerCase();
	}
}
