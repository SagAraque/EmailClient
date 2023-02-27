package com.clienteemail.ViewProviders;

import com.clienteemail.Controller;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.IOException;

public class EmailContent {
    /**
     * Display the email content view. A view controller is needed to call some methods for delete and display content.
     * @param mailBox Parent for the new view
     * @param message Email
     * @param uid Unique email identifier
     * @param controller View controller
     * @throws MessagingException
     * @throws IOException
     */
    public static void generate(VBox mailBox, Message message, String uid, Controller controller) throws MessagingException, IOException {
        String currentFolder = Controller.currentFolder;
        Label subject = new Label();
        Label name = new Label();
        Label from = new Label();
        HBox box = new HBox();
        HBox top = new HBox();
        HBox subjectBox = new HBox();
        WebView content = new WebView();
        ImageView back = new ImageView(new Image(Controller.class.getResourceAsStream("arrow.png")));

        String fromText = message.getFrom()[0].toString();
        String toText = message.getRecipients(Message.RecipientType.TO)[0].toString();
        Object emailBody = message.getContent();
        String contentReturn = "";

        //Check if content is plain text or Multipart (HTML)
        if(emailBody instanceof String){
            contentReturn = (String) emailBody;
        }else if(emailBody instanceof Multipart multipart){
            BodyPart part = multipart.getBodyPart(1);
            contentReturn = part.getContent().toString();
        }

        subject.setId("email_read_subject");
        name.getStyleClass().add("email_text");
        from.getStyleClass().add("email_read_text");
        box.setId("email_read_box");
        content.setId("email_read");
        top.setId("email_read_head");

        back.setFitWidth(20);
        back.setFitHeight(20);
        back.cursorProperty().set(Cursor.HAND);

        back.setOnMouseClicked((e) -> controller.showEmails(currentFolder));
        top.getChildren().add(back);

        //Delete function is only allowed in Inbox folder
        if (currentFolder.equals("INBOX")){
            ImageView trash = new ImageView(new Image(Controller.class.getResourceAsStream("trash.png")));

            trash.setFitWidth(20);
            trash.setFitHeight(20);
            trash.cursorProperty().set(Cursor.HAND);

            trash.setOnMouseClicked((e) -> controller.deleteEmail(uid));
            top.getChildren().add(trash);
        }

        subject.setText(message.getSubject());

        //Inbox: Name <name@example.com>
        //Sent: name@example.com
        char c = fromText.contains("<") ? '<' : '@';
        String address = currentFolder.equals("INBOX") ? fromText : toText;
        name.setText(address.substring(0, address.indexOf(c)));
        from.setText(c == '@' ? address : address.substring(address.indexOf(c)));

        content.getEngine().loadContent(contentReturn); //Set html content to WebView

        subjectBox.getChildren().add(subject);

        box.getChildren().addAll(name, from);
        mailBox.getChildren().clear();
        mailBox.getChildren().addAll(top,subjectBox,box,content);
        mailBox.getStyleClass().add("inbox_container_max");
    }
}
