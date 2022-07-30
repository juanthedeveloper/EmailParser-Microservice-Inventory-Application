package org.jcpdev;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailParser {

    public static void main(String[] args) throws Exception {

        Logger logger = Logger.getLogger("Email Parser Microservice");
        FileHandler fileHandler = new FileHandler("log.log");
        logger.addHandler(fileHandler);

        Properties props;
        props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        Store store = null;
        MimeMultipart rawEmailBody;
        Folder inbox = null;
        Message[] messages;

        try {
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", secret.email, secret.password);
            inbox = store.getFolder("MicroServiceFolder");
            if (!store.isConnected()) {
                logger.log(Level.INFO, "Connecting to store");
                store.connect();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString());
            store.close();
            System.exit(1);
        }
        while (true) {
            try {
                inbox.open(Folder.READ_WRITE);
                messages = inbox.getMessages();
                if (messages.length > 0) {
                    for (Message message : messages
                    ) {
                        rawEmailBody = (MimeMultipart) message.getContent();
                        if (getTextFromEmailBody(rawEmailBody).contains("Old School Runescape")) {
                            //do something tod b
                        } else {
                            //NOT FOUND, DONT DELETE?
                        }
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
                inbox.close(true);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.toString());
                store.close();
                System.exit(2);
            }
        }
    }
    private static String getTextFromEmailBody(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromEmailBody((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}
