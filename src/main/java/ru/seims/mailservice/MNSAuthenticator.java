package ru.seims.mailservice;

import ru.seims.utils.logging.Logger;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;
import org.springframework.http.HttpHeaders;

import java.util.Base64;

public class MNSAuthenticator {
   private static String encodedUser;

   public static void loadProvidedUserCredentials() {
       String user = PropertyReader.getPropertyValue(PropertyType.MAILSERVICE, "auth.user");
       String pass = PropertyReader.getPropertyValue(PropertyType.MAILSERVICE, "auth.pass");
       if(user.isEmpty() || pass.isEmpty())
           Logger.log(MNSAuthenticator.class, "Can't load provided MNS user credentials. Connection will be refused.", 2);
       encodedUser = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
   }

   public static String loadServiceRemoteURL() {
       String url = PropertyReader.getPropertyValue(PropertyType.MAILSERVICE, "service.url");
       if(url.isEmpty()) {
           Logger.log(MNSAuthenticator.class, "Can't find MNS service URL. Check mailservice.properties for \"service.url\"");
           return "http://localhost:8080";
       }
       return url;
   }

   public static HttpHeaders getHeaders() {
       return new HttpHeaders() {{
           String authHeader = "Basic " + encodedUser;
           set("Authorization", authHeader);
       }};
   }
}
