package com.clienteemail;

import com.sun.mail.imap.IMAPFolder;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mail {
    private static Session session;
    private static Store store;
    private final String user, password;

    public Mail(String user, String password) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.store.protocol", "imaps");

        session = Session.getDefaultInstance(prop, null);
        store = session.getStore("imap");

        store.connect("host", user, password);
        this.user = user;
        this.password = password;
    }

    /**
     * Return and open a folder with the indicated mode
     * @param folder Folder
     * @param mode Folder mode. Can be Read-only or Read-Write
     * @return
     * @throws MessagingException
     */
    public IMAPFolder getFolder(String folder, int mode) throws MessagingException {
        IMAPFolder inBox = (IMAPFolder) store.getFolder(folder);
        inBox.open(mode);

        return inBox;
    }

    /**
     * Create the Sent folder if exists
     * @throws MessagingException
     */
    public void createFolder() throws MessagingException {
        Folder sent = store.getFolder("SENT");

        if(!sent.exists()) sent.create(Folder.HOLDS_MESSAGES);
    }

    /**
     * Send an email and copy it to the sent folder
     * @param to Email Recipient
     * @param subject Email subject
     * @param body Email body. Can be HTML
     * @throws MessagingException
     */
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "mail.damiansu.com");
        props.put("mail.smtp.auth", true);

        Session sendSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        MimeMessage message = new MimeMessage(sendSession);
        message.setSubject(subject);
        message.setFrom(new InternetAddress(user));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setContent(body, "text/html");
        Transport.send(message);

        Folder folder = store.getFolder("SENT");
        folder.open(Folder.READ_WRITE);
        message.setFlag(Flags.Flag.SEEN, true); // Set as seen
        folder.appendMessages(new Message[]{message});
        folder.close(true);
    }

    /**
     * Delete an email by UID
     * @param uid Unique email identifier
     * @param folder Email folder
     * @throws MessagingException
     */
    public void deleteEmail(String uid, String folder) throws MessagingException {
        IMAPFolder f = (IMAPFolder) store.getFolder(folder);
        f.open(Folder.READ_WRITE);
        Message message = f.getMessageByUID(Long.parseLong(uid));

        message.setFlag(Flags.Flag.DELETED, true);
        f.close(true);
    }

    public Store getStore(){
        return store;
    }

    public Session getSession(){
        return session;
    }

    public void close() throws MessagingException {
        store.close();
    }
}
