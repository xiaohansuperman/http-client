package com.wangliyong.http.client;

import java.io.ByteArrayOutputStream;

public class GZip{

	public GZip() {}

	public static byte[] zip(byte[] buf) throws Exception{
		ByteArrayOutputStream baos = null;
		java.util.zip.GZIPOutputStream gos = null;
		try {
			baos = new ByteArrayOutputStream();
			gos = new java.util.zip.GZIPOutputStream(baos);
			gos.write(buf);
			gos.finish();
			gos.flush();

			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (gos != null) {
				gos.close();
			}
		}
	}

	public static byte[] unzip(byte[] buf) throws Exception {
		java.io.ByteArrayInputStream bais = null;
		java.util.zip.GZIPInputStream gis = null;
		ByteArrayOutputStream baos = null;
		try
		{
			bais = new java.io.ByteArrayInputStream(buf);
			gis = new java.util.zip.GZIPInputStream(bais);
			baos = new ByteArrayOutputStream();

			byte[] tmp = new byte['?'];
			int count = 0;
			count = gis.read(tmp);
			while (count != -1) {
				baos.write(tmp, 0, count);
				count = gis.read(tmp);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (gis != null) {
				gis.close();
			}
		}
	}
}
