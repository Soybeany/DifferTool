import com.clevertap.apns.CertificateUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * <br>Created by Soybeany on 2019/8/22.
 */
public class Main {

    public static void main(String... args) throws Exception {
        FileInputStream cert = new FileInputStream("D:\\apnsTest.p12");
//        String token = "a2520e33b411c5e29c9d0888547118b1e9f46f808bd4f1bca1440776c4b246ad";
        String token = "2afdad6d2a3d65d5733256313258fd2f2e82c00a274707349b5964421052a123";
        HttpRequest.Builder post = HttpRequest.newBuilder(URI.create("https://api.push.apple.com/3/device/" + token))
                .header("apns-topic", "com.csair.meteoro")
//                .header("apns-expiration", "0")
                .header("apns-id", "eabeae54-14a8-11e5-b60b-1697f925ec7c")
                .header("apns-priority", "10")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"aps\" : { \"alert\" : \"Hello\" } }"));
        HttpClient client = HttpClient.newBuilder().sslContext(getSSLContext(cert, "abAB123123", true)).build();
        HttpResponse<String> response = client.send(post.build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("结果:" + response.body() + " " + response.statusCode() + "\n" + response.headers());
    }

    private static SSLContext getSSLContext(InputStream certificate, String password, boolean production) throws Exception {
        password = password == null ? "" : password;
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(certificate, password.toCharArray());

        final X509Certificate cert = (X509Certificate) ks.getCertificate(ks.aliases().nextElement());
        CertificateUtils.validateCertificate(production, cert);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();
        SSLContext sslContext = SSLContext.getInstance("TLS");

        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);
        sslContext.init(keyManagers, tmf.getTrustManagers(), null);
        return sslContext;
    }
}
