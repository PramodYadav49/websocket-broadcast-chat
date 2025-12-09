package com.ci.ClientNotification;

import com.ci.ClientNotification.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.ci.ClientNotification.util.CommonUtil.*;

@SpringBootApplication
public class ClientNotificationApplication implements ApplicationRunner {
    @Autowired
    private EmailService emailService;
	public static void main(String[] args) {
		SpringApplication.run(ClientNotificationApplication.class, args);
	}

    //When Applicaiton is start It will check Application Runner in Use Run method SO it will excute run method
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //We are getting Ipv4 this is static method it is on CommonUtil class
        String message=getLocalIPv4InSideMessage();


        //sendMail Is a Method Inside the EmailService Class
        emailService.sendMail(
                //We Send String Array so We can send multiple Mail Id Where We can send Same Mail
                //We are Set To
                new String[]{"pramod@chandikainnov.com","ratnender@chandikainnov.com"},

                //We are seting Subject
                "Broadcast Service is up You can broadcast The message ",
                //This is message Body
                message
        );

        System.out.println("Startup mail sent!");
    }
}
