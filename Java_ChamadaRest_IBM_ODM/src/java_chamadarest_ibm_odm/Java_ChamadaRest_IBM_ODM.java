package java_chamadarest_ibm_odm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Java_ChamadaRest_IBM_ODM {

    public static void main(String[] args)
            throws KeyManagementException, CertificateException, NoSuchAlgorithmException, IOException {

        callODM();

    }

    @SuppressWarnings("unused")
    public static void callODM()
            throws IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException {

        String username = new Credenciais().getUsuario();
        String password = new Credenciais().getSenha();

        String endpoint = "https://decisionmanager.xxxxx.com.br/DecisionService/rest/v1/xxxxxxxx/xxxxxxx/1.0";

        String userpass = username + ":" + password;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

        String encoded = Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

        String json = "";

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        }};

        URL url = new URL(endpoint);
        System.out.println("URL para chamada: " + url);

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = (String hostname, SSLSession session) -> true;

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("charset", "utf-8");
        con.setRequestProperty("Accept", "application/json");

        // con.setRequestProperty("Authorization", basicAuth);
        con.setRequestProperty("Authorization", "Basic " + encoded);

        try (OutputStream os = con.getOutputStream()) {
            byte[] body = json.getBytes("utf-8");
            os.write(body, 0, body.length);
        } catch (Exception e) {
            System.out.println("Verifique a VPN !");
            System.out.println("Erro ao escrever body chamada Rest ODM: " + e);

        }

        if (con.getResponseCode() == 200 || con.getResponseCode() == 201) {
            System.out.println("Conexao OK  => " + con.getResponseCode());
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                String responseRequest = response.toString();
                System.out.println("Retorno ODM: " + responseRequest);

            } catch (Exception e) {
                System.out.println("Erro ao realizar chamada Rest ODM: " + e);
            }
        } else {
            System.out.println("Conexao não OK  => " + con.getResponseCode());
        }
        System.out.println();
    }
}
