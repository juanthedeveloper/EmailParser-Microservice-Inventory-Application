package org.jcpdev;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailParser {

    public static void main(String[] args) throws MessagingException {
        Folder inbox = null;
        String result = null;
        Session session;
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        Properties props;

        Store store = null;
        try {
            props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", secret.email, secret.password);
            inbox = store.getFolder("MicroServiceFolder");
            if (!store.isConnected()) {
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
                Message[] messages = inbox.getMessages();
                if (messages.length > 0) {
                    for (Message message : messages
                    ) {
                        if (message.isMimeType("multipart/*")) {
                            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                            result = getTextFromMimeMultipart(mimeMultipart);
                        }
                        if (result.contains("Old School Runescape")) {
                            //do something tod b
                        } else {
                            //NOT FOUND, DONT DELETE?
                        }
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
                inbox.close(true);
            } catch (Exception e) {
                e.printStackTrace();
                store.close();
                System.exit(2);
            }
        }
    }


    private static String getTextFromMimeMultipart(
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
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }


}
