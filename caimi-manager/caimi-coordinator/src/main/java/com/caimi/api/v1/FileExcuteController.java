package com.caimi.api.v1;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import caimi.common.util.FileUtil;

@RestController
public class FileExcuteController {

	private static final Logger logger = LoggerFactory.getLogger(FileExcuteController.class);
	private static final String filePath= "/home/scrapy/wyzw/file";
	
	@RequestMapping("/file")
	public void excute() {
		File txtDir = new File(filePath);
		List<File> story = FileUtil.getAllFile(txtDir, (File file)->{
			String fileName = file.getName();
			if(fileName.endsWith("txt")) {
				return true;
			}
			return false;
		});
		for(File file:story) {
			String fName = file.getName();
			try {
				String content = FileUtil.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
