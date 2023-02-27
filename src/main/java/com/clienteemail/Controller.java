package com.clienteemail;

import com.clienteemail.ViewProviders.EmailCard;
import com.clienteemail.ViewProviders.EmailContent;
import com.clienteemail.ViewProviders.SendEmailView;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.SortTerm;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.util.Duration;
import javax.mail.*;
import java.io.IOException;

public class Controller {
    private Mail mail;
    public static String currentFolder;
    private String user = "email";
    private String password = "password";
    @FXML
    public VBox mailBox;
    @FXML
    public HBox sentButton, inboxButton, alert;
    @FXML
    public Label sentLabel, inboxLabel, alertText;
    @FXML
    public ScrollPane scroll;

    @FXML
    public void initialize(){
        try{
            alert.setVisible(false);
            mail = new Mail(user, password);
            mail.createFolder(); //Create sent folder if not exists
            currentFolder = "INBOX";

            IMAPFolder folderI = mail.getFolder("INBOX", Folder.READ_ONLY);

            inboxLabel.setText("Recibidos ("+ folderI.getUnreadMessageCount() +")");

            folderI.close(true);

            showEmails(currentFolder);

        }catch (MessagingException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void changeFolder(MouseEvent event){
        HBox[] buttons = new HBox[]{inboxButton, sentButton};
        int pos = currentFolder.equals("INBOX") ? 0 : 1; //buttons array position
        alert.setVisible(false); //Hidde alert when folder is changed

        if(event.getSource() != buttons[pos]){
            buttons[pos].getStyleClass().remove(1); //Remove button_selected from current folder button
            buttons[Math.abs(pos - 1)].getStyleClass().add("button_selected");
            currentFolder = currentFolder.equals("INBOX") ? "SENT" : "INBOX";
        }

        showEmails(currentFolder);
    }

    /**
     * Load and create all email cards from the folder
     * @param folder Folder where emails will be loaded
     */
    public void showEmails(String folder){
        try{
            mailBox.getChildren().clear();

            IMAPFolder inBox = mail.getFolder(folder, Folder.READ_ONLY);

            for (Message m : inBox.getSortedMessages(new SortTerm[]{SortTerm.REVERSE, SortTerm.DATE})){
                String uid = String.valueOf(inBox.getUID(m));
                HBox container = generateEmail(m, uid);

                mailBox.getChildren().add(container);
            }

            inBox.close(true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get email from the current folder by UID and display the content
     * @param uid Unique email identifier
     */
    public void getEmail(String uid){
        try{
            IMAPFolder inBox = mail.getFolder(currentFolder,Folder.READ_WRITE);

            Message m = inBox.getMessageByUID(Long.parseLong(uid));
            displayEmailContent(m, String.valueOf(inBox.getUID(m)));
            m.setFlag(Flags.Flag.SEEN, true); //Set email as seen

            if(currentFolder.equals("INBOX"))
                inboxLabel.setText("Recibidos ("+ inBox.getUnreadMessageCount() +")"); //Reload unread message indicator

            inBox.close(true);

        }catch (MessagingException | IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Delete Email from the current folder by UID
     * @param uid Unique email identifier
     */
    public void deleteEmail(String uid) {
        try {
            mail.deleteEmail(uid, currentFolder);
            showEmails(currentFolder);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send email. Check always if the recipient is empty
     * @param to Email recipient
     * @param subject Email Subject
     * @param editor HTMLEditor with the email body
     */
    public void sendEmail(TextField to, TextField subject, HTMLEditor editor){
        try {
            String toText = to.getText().trim();
            String subjectText = subject.getText().trim();
            String editorText = editor.getHtmlText().trim();

            if (!toText.equals("")) {
                mail.sendEmail(toText, subjectText, editorText);
                showEmails(currentFolder); // Reload current folder view
                showAlert("Email enviado"); // Display an alert
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void reload() throws MessagingException {
        IMAPFolder folderI = mail.getFolder("INBOX", Folder.READ_ONLY);
        inboxLabel.setText("Recibidos ("+ folderI.getUnreadMessageCount() +")");
        folderI.close(true);

        showEmails(currentFolder);
        showAlert("Emails cargados");
    }

    /**
     * Display an alert with custom message. The message disappears after 2 seconds
     * @param text Alert message
     */
    private void showAlert(String text){
        alert.setVisible(false); // Remove old alerts
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), alert);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        alertText.setText(text);
        alert.setVisible(true);
        fadeIn.playFromStart();

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), alert);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        new Thread(() ->{
            try {
                Thread.sleep(2000);
                fadeOut.playFromStart();
                fadeOut.setOnFinished((e) -> alert.setVisible(false));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @FXML
    /**
     * Display the email sending view
     */
    public void sendEmailView(){
        alert.setVisible(false);
        SendEmailView.generate(mailBox, this);
    }

    /**
     * Generate email cards
     * @param message Email
     * @param uid Unique email identifier
     * @return Email card
     * @throws MessagingException
     */
    private HBox generateEmail(Message message, String uid) throws MessagingException{
        return EmailCard.generate(message, uid, this);
    }

    /**
     * Display the email content view
     * @param message Email
     * @param uid Unique email identifier
     * @throws MessagingException
     * @throws IOException
     */
    private void displayEmailContent(Message message, String uid) throws MessagingException, IOException {
        alert.setVisible(false);
        EmailContent.generate(mailBox, message, uid, this);
    }
}