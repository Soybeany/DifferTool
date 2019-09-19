package sso;

import okhttp3.*;

/**
 * <br>Created by Soybeany on 2019/9/5.
 */
public class TestEFB {

    public static void main(String... arg) throws Exception {
        login();
//       sendMsg();
    }


    private static void login() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        FormBody.Builder body = new FormBody.Builder();
        body.add("account", "194522");
        body.add("password", "UScxiG0NbqqGRdp4nTTvVw==");
        body.add("platform", "iPad");
        body.add("osVersion", "12.1.1");
        body.add("appVersion", "1.20.75");
//        body.add("mobileCode", "31511");
//        body.add("token", "VH1XOL6BHWR3M2VMWZ3Q23OSUKKF4L0H-641e6d179b54d1e1939b60458cfef312");
        body.add("deviceName", "“spinlee”的 iPad");
        body.add("udid", "404453ed02b6e4f31f14ab7c49a3cf74");
        body.add("pushToken", "760210f14ca2d603e1c0d833cf13daa4773f620857351f029f9d285221b73953");
        body.add("firstLogin", "false");
        Request request = builder.url("http://10.95.13.11:8080/efb/auth/User/userIPadLogin.do").post(body.build()).build();

        Response response = client.newCall(request).execute();
        System.out.println("状态码:" + response.code());
        try (ResponseBody responseBody = response.body()) {
            System.out.println(responseBody.string());
        }
    }

    private static void sendMsg() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();

        Request request = builder.url("http://10.95.13.11:8080/efb/auth/User/sendVerifySms.do?account=231892&token=VH1XOL6BHWR3M2VMWZ3Q23OSUKKF4L0H-641e6d179b54d1e1939b60458cfef312").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            System.out.println(responseBody.string());
        }
    }

}
