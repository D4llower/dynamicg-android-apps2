package com.dynamicg.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

public class AppSignature {

	public static String get(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = null;

		try {
			packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			return null;
		}
		Signature[] signatures = packageInfo.signatures;

		byte[] cert = signatures[0].toByteArray();

		InputStream input = new ByteArrayInputStream(cert);

		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X509");
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		X509Certificate c = null;
		try {
			c = (X509Certificate) cf.generateCertificate(input);
		} catch (CertificateException e) {
			return e.toString();
		}

		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] publicKey = md.digest(c.getPublicKey().getEncoded());

			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<publicKey.length;i++) {
				String appendString = Integer.toHexString(0xFF & publicKey[i]);
				if(appendString.length()==1)hexString.append("0");
				hexString.append(appendString);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e1) {
			return e1.toString();
		}
	}

}
