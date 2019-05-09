/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.runcmd.provider.pwtool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.kohsuke.MetaInfServices;

import com.fortify.runcmd.spi.IValueProvider;

@MetaInfServices
public class PwToolProvider implements IValueProvider {
	private static final String PRP_PWTOOL_PATH = "pwtool.path";
	private static final String PRP_PWTOOL_KEYSFILE = "pwtool.keysFile";

	@Override
	public String getName() {
		return "pwtool";
	}

	@Override
	public String getValue(String key) {
		String pwToolPath = System.getProperty(PRP_PWTOOL_PATH);
		String pwToolKeysFile = System.getProperty(PRP_PWTOOL_KEYSFILE);
		// TODO Check whether pwToolPath and pwToolKeysFile have been set
		// TODO If pwToolPath has not been set, try to find it on system path 
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(pwToolPath, "-d", pwToolKeysFile);
		try {
			Process process = processBuilder.start();
			try ( OutputStream os = process.getOutputStream() ) {
				os.write(key.getBytes());
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null && !"Decoded password:".equals(line.trim())) {}
			
			String output = reader.readLine();

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				return output;
			} else {
				throw new RuntimeException("pwtool returned non-zero exit code");
			}
		} catch (IOException|InterruptedException e) {
			throw new RuntimeException("Error invoking "+pwToolPath, e);
		}
	}
}
