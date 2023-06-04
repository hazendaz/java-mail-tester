/*
 * base-parent (https://github.com/hazendaz/base-parent)
 *
 * Copyright 2019-2023 Hazendaz.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of The Apache Software License,
 * Version 2.0 which accompanies this distribution, and is available at
 * https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Contributors:
 *     Hazendaz (Jeremy Landis).
 */
package com.github.mail;

import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/** 
 * Code adapted from https://community.oracle.com/thread/4052611 to confirm 'dat' file conversion of pdf in java mail 1.5.x+.
 */
public class TestMailIssue {

  private static final String SENDER = "jeremylandis@hotmail.com";

  private static final String RECEIVER = "jeremylandis@hotmail.com";

  private static final String SMTP_HOST = "TBD";

  private static final String SMTP_HOST_PORT = "TBD";

  private static final String SMTP_HOST_PWD = "TBD";

  private static final String PATH_FILE_NAME = "PDF Document to test mail issue, with filname longer than 60 and mail api 1.5.6.pdf";

  // WORKS

  // private static final String BODY_PART_FILE_NAME = "PDF Document to test mail issue, with filname longer tha.pdf";

  // DOESN'T WORK

  private static final String BODY_PART_FILE_NAME = "PDF Document to test mail issue, with filname longer than 60.pdf";

  public static void main(String[] args) throws AddressException, MessagingException {

    // Run in modern mode
    System.setProperty("mail.mime.encodeparameters", "true");

    // Run in legacy mode
    // System.setProperty("mail.mime.encodeparameters", "false");

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", SMTP_HOST);
    props.put("mail.smtp.port", SMTP_HOST_PORT);

    Session mailSession = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(SENDER, SMTP_HOST_PWD);
      }
    });

    Message message = new MimeMessage(mailSession);
    message.setFrom(new InternetAddress(SENDER));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(RECEIVER));
    message.setSubject(
        "test mail issue - java mail version:" + message.getClass().getPackage().getImplementationVersion());

    StringBuilder text = new StringBuilder();
    text.append("email body, attachement file is: ");
    text.append(PATH_FILE_NAME);
    text.append("\n");
    text.append("used file name: ");
    text.append(BODY_PART_FILE_NAME);
    text.append("\n");
    text.append("file name length: ");
    text.append(BODY_PART_FILE_NAME.length());
    text.append("\n");
    text.append("mail properties: ");
    text.append(props.toString());
    text.append("\n");

    BodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setText(text.toString());

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);

    DataSource source = new FileDataSource(new File(PATH_FILE_NAME).getAbsolutePath());

    messageBodyPart = new MimeBodyPart();
    messageBodyPart.setDataHandler(new DataHandler(source));
    messageBodyPart.setFileName(BODY_PART_FILE_NAME);

    multipart.addBodyPart(messageBodyPart);

    message.setContent(multipart);

    Transport.send(message);

  }

}
