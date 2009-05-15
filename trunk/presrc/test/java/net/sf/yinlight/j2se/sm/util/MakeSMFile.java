/** GPL >= 2.0
 * Based upon SecureMessenger
 * Copyright (C) 2002 Eugene Morozov
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
/**
 * Modification started 2009-05-13.
 */
package net.sf.yinlight.j2se.sm.util;


import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;

import net.eiroca.j2me.sm.util.Store;
import net.eiroca.j2me.sm.data.MessageHandlerException;
import net.eiroca.j2me.sm.util.StoreException;
import net.eiroca.j2me.util.CipherDES;
import net.eiroca.j2me.sm.data.Address;

/**
 * Convert unencoded files to files with encoded data for SecureSMS.
 */
public class MakeSMFile {

	static public byte[] convertData(String text, byte [] key) {
    byte[] data = Store.encodeData(text);
		CipherDES chiper = new CipherDES();
    data = chiper.encode(data, key);
    return data;
	}

	static public void convertFile(String input, String output, byte[] key) throws IOException {
		BufferedReader binp = new BufferedReader(new FileReader(input));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = binp.readLine()) != null) {
			sb.append(line);
		}
		binp.close();
		byte[] data = convertData(sb.toString(), key);
		FileOutputStream bout = new FileOutputStream(new File(output));
		bout.write(data, 0, data.length);
		bout.close();
	}

	static public void main(String[] args) {
		try {
			Process proc = Runtime.getRuntime().exec("/bin/pwd");
			proc.waitFor();
			BufferedInputStream bis = new BufferedInputStream(proc.getInputStream());
			byte[] buf = new byte[256];
			int len = bis.read(buf, 0, 256);
			bis.close();
			String line = new String(buf, 0, len);
			System.out.println("line=" + line);
			String input = System.getProperty("input");
			System.out.println("input=" + input);
			String output = System.getProperty("output");
			System.out.println("output=" + output);
			String skey = System.getProperty("skey");
			System.out.println("skey=" + skey);
			Address addr = new Address("", "", skey);
			byte[] bkey = addr.getKeyData();
			for (int i = 0; i < bkey.length; i++) {
				System.out.println("bkey[" + i + "]=" + bkey[i]);
			}
			convertFile(input, output, bkey);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
