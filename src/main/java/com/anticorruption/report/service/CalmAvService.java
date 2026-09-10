package com.anticorruption.report.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CalmAvService {
	
	@Value("${clamav.host:localhost}")
	private String host;
	
	@Value("${clamav.port:3310}")
	private int port;
	
	public void scan(MultipartFile file) {
		if(file != null && !file.isEmpty()) {
			
			try (Socket socket = new Socket(host, port);
				DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
					InputStream input = socket.getInputStream()) {
				
				socket.setSoTimeout(300000) ;
				
				stream.write("zINSTREAM\0".getBytes(StandardCharsets.US_ASCII));
				
				byte[] buffer = new byte[8192];
				
				byte[] data = file.getBytes();
				
				int offset = 0;
				
				while(offset  < data.length) {
					int length = Math.min(buffer.length,data.length- offset);
					stream.writeInt(length);
					stream.write(data, offset, length);
					offset+= length;
				}
				stream.writeInt(0);
				stream.flush();
				
					byte[] response = socket.getInputStream().readAllBytes();
					
					String result = new String(response, StandardCharsets.US_ASCII).trim();
					
					if(result.endsWith("FOUND")) {
						throw new IllegalArgumentException("File is corrupted/Infected");
					}
					
					if(!result.endsWith("OK")) {
						throw new IllegalArgumentException("File validation failed");
					}
					
 			} catch (IOException e) {
 				throw new IllegalArgumentException("Unable to perform scan. ClamAV is unavailable");
 			}
		}
	}
}
