package caimi.common.util;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {

	public static String read(File file) throws IOException {
		if(!file.exists() || !file.canRead()) {
			throw new IOException("File"+file+"not exists or not readable");
		}
		StringWriter writer = new StringWriter();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));){
			char cbuf[] = new char[4096];
			int clen;
			while((clen=reader.read(cbuf))>0) {
				writer.write(cbuf,0,clen);
			}
		}
		return writer.toString();
	}
	
	/**
	 * 递归遍历所有文件
	 * @param fileDir
	 */
	public static List<File> getAllFile(File dir, FileFilter filter) {
		List<File> result = new ArrayList<File>();
		LinkedList<File> dirs = new LinkedList<File>();
		dirs.add(dir);
		while(!dirs.isEmpty()) {
			File file = dirs.poll();
			if(dir.isDirectory()) {
				File[] files = file.listFiles();
				if(file!=null) {
					dirs.addAll(Arrays.asList(files));
				}
				continue;
			}
			if(filter.accept(file)) {
				result.add(file);
			}
		}
		return result;
	}
}
