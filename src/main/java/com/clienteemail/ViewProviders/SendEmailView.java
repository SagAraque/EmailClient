package com.clienteemail.ViewProviders;

import com.clienteemail.Controller;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;

public class SendEmailView {
    /**
     * Display the sending email view. A view controller is needed to call send email method.
     * @param mailBox Parent for sending email view
     * @param controller View controller
     */
    public static void generate(VBox mailBox, Controller controller){
        HBox toBox = new HBox();
        HBox subjectBox = new HBox();
        Label toLabel = new Label("Para");
        Label subjectLabel = new Label("Asunto");
        TextField to = new TextField();
        TextField subject = new TextField();
        HTMLEditor editor = new HTMLEditor();
        Button sendBtn = new Button("Enviar");

        toBox.getStyleClass().add("email_write_container");
        subjectBox.getStyleClass().add("email_write_container");

        toLabel.getStyleClass().add("email_write_label");
        subjectLabel.getStyleClass().add("email_write_label");

        to.getStyleClass().add("email_write_field");
        subject.getStyleClass().add("email_write_field");

        toBox.getChildren().addAll(toLabel, to);
        subjectBox.getChildren().addAll(subjectLabel, subject);

        editor.setId("email_write_editor");
        sendBtn.getStyleClass().add("email_write_send");
        sendBtn.setOnMouseClicked((e) -> controller.sendEmail(to, subject, editor));

        mailBox.getChildren().clear();
        mailBox.getChildren().addAll(toBox, new Separator(), subjectBox, new Separator(), editor, sendBtn);
    }
}
