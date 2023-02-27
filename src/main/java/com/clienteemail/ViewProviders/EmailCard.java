package com.clienteemail.ViewProviders;

import com.clienteemail.Controller;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmailCard {
    /**
     * Return an email card. A view controller is needed to call some methods for delete and display content.
     * @param message Email
     * @param uid Unique email identifier
     * @param controller View controller
     * @return
     * @throws MessagingException
     */
    public static HBox generate(Message message, String uid, Controller controller) throws MessagingException {
        String currentFolder = Controller.currentFolder;
        Date sentDate = message.getReceivedDate();

        String fromText = message.getFrom()[0].toString();
        int pos = fromText.indexOf('<'); // Name <name@example.com>
        if(pos != -1) fromText =  fromText.substring(0, pos);

        HBox container = new HBox();
        Label from = new Label(fromText);
        Label subject = new Label(message.getSubject());
        Label date = new Label();
        ImageView cross = new ImageView(new Image(Controller.class.getResourceAsStream("cross.png")));

        if(message.isSet(Flags.Flag.SEEN)) //Check if email has been read and apply the class
            container.getStyleClass().add("email_container_read");

        container.getStyleClass().add("email_container");
        from.getStyleClass().addAll(currentFolder.equals("INBOX") ? "email_from" : "email_from_large", "email_text");
        subject.getStyleClass().addAll(currentFolder.equals("INBOX") ? "email_subject" : "email_subject_large", "email_text");
        date.getStyleClass().addAll("email_text", "email_date");

        container.getChildren().addAll(from,subject);

        date.setText(getFormatDate(sentDate));

        container.getChildren().add(date);


        if(currentFolder.equals("INBOX")){ // Is only allowed to delete in Inbox folder
            date.setTranslateX(85);

            cross.setFitWidth(10);
            cross.setFitHeight(10);
            cross.setTranslateX(80);
            cross.setOnMouseClicked((e) -> controller.deleteEmail(uid));

            container.getChildren().add(cross);
        }

        container.setOnMouseClicked((e) -> controller.getEmail(uid));

        return container;
    }

    /**
     * Compare current date with sent date and apply dd/MM/yy or HH:mm format
     * @param sent Sent date
     * @return String date with the corresponding format
     */
    private static String getFormatDate(Date sent){
        SimpleDateFormat formatDay = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm");
        Date now = new Date(System.currentTimeMillis());

        if(formatDay.format(now).equals(formatDay.format(sent))){
            return formatHour.format(sent);
        }else {
            return formatDay.format(sent);
        }
    }
}
