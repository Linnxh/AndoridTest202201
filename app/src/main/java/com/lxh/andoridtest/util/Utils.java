package com.lxh.andoridtest.util;

import android.net.http.SslCertificate;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
public class Utils {

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        String hexStr = new String(hexChars);
        Log.i("=============", "bytesToHex==" + hexStr);
        return hexStr;
    }

    /**
     * SSL证书错误，手动校验https证书
     *
     * @param cert https证书
     * @return true通过，false失败
     *
     *
     *
    val wap_jde_test_sh256 = "951e1d5dcbce35dbe01af7774b97f9abc90e51f6bdf04067482b0cec170cebaa"
    val wap_jde_uat_sh256 = "678d83b060da1d1bf0fc88fe20f1f2b1dc1d42d81e9be31e54208c0098de415d"
    val server = Test.getSSLCertFromServer(mViewBinding.webView.certificate)
    if (wap_jde_test_sh256.lowercase() == server.lowercase()) {
    Log.i("=============", "jde_test_equal")
    }
    if (wap_jde_uat_sh256.lowercase() == server.lowercase()) {
    Log.i("=============", "jde_uat_equal")
    }
    Log.i("=============", "server==$server")

     */
    public static String getSSLCertFromServer(SslCertificate cert) {
        Bundle bundle = SslCertificate.saveState(cert);
        if (bundle != null) {
            byte[] bytes = bundle.getByteArray("x509-certificate");
            if (bytes != null) {
                try {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    Certificate ca = cf.generateCertificate(new ByteArrayInputStream(bytes));
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    byte[] key = sha256.digest(ca.getEncoded());
                    Log.i("=============", "getSSLCertFromServer==" + Arrays.toString(key));
                    return bytesToHex(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
