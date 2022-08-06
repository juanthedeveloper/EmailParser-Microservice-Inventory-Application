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
        Message[] messages;
        Store store = null;
        Folder etsyTransactionsInbox = null;

        try {
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", secret.email, secret.password);
            etsyTransactionsInbox = store.getFolder("EtsyTransctions");
            if (!store.isConnected()) {
                logger.log(Level.INFO, "Connecting to store");
                store.connect();
            }
        } catch (Exception e) {
            caughtException(logger, store, e);
        }
        logger.log(Level.INFO, "Setup successful");
        while (true) {
            try {
                if (!etsyTransactionsInbox.isOpen()) {
                    etsyTransactionsInbox.open(Folder.READ_WRITE);
                }
                messages = etsyTransactionsInbox.getMessages();
                if (messages.length > 0) {
                    for (Message message : messages
                    ) {
                        if (message.getSubject().contains("You made a sale on Etsy")) {
                           emailIsNewSale(logger,message);
                        } else {
                           emailIsNotNewSale(logger, message);
                        }
                    }
                    etsyTransactionsInbox.close(true);
                }
            } catch (Exception e) {
                caughtException(logger, store, e);
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
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append("\n").append(org.jsoup.Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromEmailBody((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private static void emailIsNewSale(Logger logger, Message message) throws MessagingException, IOException {
        String orderNumber = (String) message.getSubject().subSequence(message.getSubject().indexOf('#'), message.getSubject().indexOf(']'));
        logger.log(Level.INFO, "Processing Order " + orderNumber);
        if (message.isMimeType("multipart/*")) {
            String emailBodyText = getTextFromEmailBody((MimeMultipart) message.getContent());

            if (emailBodyText.contains("Old School Runescape")) {
                //do something tod b
            } else {
                logger.log(Level.WARNING, "Email contents did not match items.");
            }
            message.setFlag(Flags.Flag.DELETED, true);
            logger.log(Level.INFO, "Email for order " + orderNumber + " processed and deleted.\n");
        } else {
            message.setFlag(Flags.Flag.DELETED, true);
            message.setFlag(Flags.Flag.FLAGGED, true);
            logger.log(Level.WARNING, "Could not get body from email Messaged flagged for review.\n");
        }
    }

    private static void emailIsNotNewSale(Logger logger, Message message) throws MessagingException {
        message.setFlag(Flags.Flag.DELETED, true);
        logger.log(Level.INFO, "Unrelated email deleted.\n");
    }

    private static void caughtException(Logger logger, Store store, Exception e) throws MessagingException {
        logger.log(Level.SEVERE, e.toString());
        store.close();
        System.exit(1);
    }
}
