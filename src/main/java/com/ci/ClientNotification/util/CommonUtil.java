package com.ci.ClientNotification.util;

public class CommonUtil {
    public static String getLocalIPv4InSideMessage() {
        String ip;
        try {
            //We are getting Ip LocalHost
             ip= java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "Unable to fetch IP";
        }

        //Inside Message We put Ip SO we can send this body to Some one who can hit api From any where

        String message =
                "🎉 Broadcast Service is UP!\n\n" +
                        "You can now send broadcast messages using the API below:\n\n" +
                        "If You Are Not Register Please Register Then Broadcast \n\n" +
                        "➡ Register User (Auth) Endpoint:\n" +
                        "http://" + ip + ":8080/api/auth/register\n\n" +
                        "➡ Broadcast API Endpoint:\n" +
                        "http://" + ip + ":8080/api/admin/broadcast\n\n" +
                        "➡ Authentication:\n" +
                        "Use Basic Auth with your provided Username and Password.\n\n" +

                        "➡ Request Body (Raw JSON for broadcast):\n" +
                        "{\n" +
                        "  \"message\": \"Your broadcast message here\"\n" +
                        "}\n\n" +

                        "➡ Request Body (Raw JSON for register):\n" +
                        "{\n" +
                        "  \"username\": \"admin\",\n" +
                        "  \"password\": \"password123\"\n" +
                        "}\n\n" +

                        "If you face any issues, please contact on pramodyadavci49@gmail.com.\n";
        return message;
    }

}
