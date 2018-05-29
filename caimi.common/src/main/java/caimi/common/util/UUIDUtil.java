package caimi.common.util;

import java.util.UUID;

public class UUIDUtil {
	
	/**
	 * 创建UUID,BASE58编码， 22 bytes length
	 */
	public static String genId() {
		UUID uuid = UUID.randomUUID();
		return Base58.compressedUUID(uuid);
	}

	/**
	 * 创建UUID，无"-"分隔符,Base16编码
	 */
	public static String genId32() {
		UUID uuid = UUID.randomUUID();
		return (uuid.toString().replaceAll("-", "")).toLowerCase();
	}
}
