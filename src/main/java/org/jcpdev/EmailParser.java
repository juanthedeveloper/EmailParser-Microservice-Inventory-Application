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
        String orderNumber;
        Message[] messages;
        Store store = null;
        MimeMultipart rawEmailBody;
        Folder inbox = null;
        String emailBodyText="";

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
                if (!inbox.isOpen()){
                    inbox.open(Folder.READ_WRITE);
                }
                messages = inbox.getMessages();
                if (messages.length > 0) {
                    for (Message message : messages
                    ) {
                        orderNumber = (String) message.getSubject().subSequence(message.getSubject().indexOf('#'), message.getSubject().indexOf(']'));
                        logger.log(Level.INFO, "Processing Order " + orderNumber);
                        if (message.isMimeType("multipart/*")) {
                            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                            emailBodyText = getTextFromEmailBody(mimeMultipart);
                        }
                        if (emailBodyText.contains("Old School Runescape")) {
                            rawEmailBody = (MimeMultipart) message.getContent();
                            if (getTextFromEmailBody(rawEmailBody).contains("Old School Runescape")) {
                                //do something tod b
                            } else {
                                logger.log(Level.WARNING, "Email contents did not match items.");
                            }
                            message.setFlag(Flags.Flag.DELETED, true);
                            logger.log(Level.INFO, "Email for order " + orderNumber + " marked for deletion");
                        }

                    }
                    inbox.close(true);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.toString());
                store.close();
                System.exit(3);
            }
        }
    }
    private static String getTextFromEmailBody(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append("\n").append(bodyPart.getContent());
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append("\n").append(org.jsoup.Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromEmailBody((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }


}
