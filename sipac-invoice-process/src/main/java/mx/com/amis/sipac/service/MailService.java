package mx.com.amis.sipac.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.persistence.model.EmailToNotify;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;

@Service
public class MailService {
  private static final Logger logger = LoggerFactory.getLogger(MailService.class);

  @Autowired private MailSender mailSender;
  private SimpleMailMessage templateMessage;

  @Value("${send.from.email}")
  private String fromEmail;

  public String builEmailBody(OrderToInvoice order) {
    return builEmailBody(order, null);
  }
  
  public String builEmailBody(OrderToInvoice order, String errorMsg) {

    StringBuffer sb = new StringBuffer();
    if (errorMsg == null) {
      sb.append("<h3>Factura Generada</h3>");
    } else {
      sb.append("<h3>Error al generar la factura: " + errorMsg + " </h3>");
    }
    sb.append("<table><tr>"
        + "<th>Folio</th>"
        + "<th>Siniestro Deudor</th>"
        + "<th>Póliza Deudor</th>"
        + "<th>Siniestro Acreedor</th>"
        + "<th>Póliza Acreedor</th>"
        + "<th>Fecha</th>"
        + "<th>Monto</th>"
        + "</tr>");
    sb.append("<tr><td>");
    sb.append(order.getFolio());
    sb.append("</td>");
    sb.append("<td>");
    sb.append(order.getSiniestroDeudor());
    sb.append("</td>");
    sb.append("<td>");
    sb.append(order.getPolizaDeudor());
    sb.append("</td>");
    sb.append("<td>");
    sb.append(order.getSiniestroAcreedor());
    sb.append("</td>");
    sb.append("<td>");
    sb.append(order.getPolizaAcreedor());
    sb.append("</td>");
    sb.append("<td>");
    sb.append(order.getFechaEstatus());
    sb.append("</td>");
    sb.append("<td>");
    sb.append(order.getMonto());
    sb.append("</td></tr>");
    sb.append("</table>");

    logger.debug(sb.toString());
    return sb.toString();
  }

  public String send(List<EmailToNotify> toEmail, String subject, String msgTxt) {
    logger.debug("Starting Send...");
    this.templateMessage = new SimpleMailMessage();
    this.templateMessage.setSubject(subject);
    this.templateMessage.setFrom(this.fromEmail);
    this.templateMessage.setTo(getEmails(toEmail));

    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
    msg.setText(msgTxt);

    try {
      this.mailSender.send(msg);
    }
    catch(MailException ex){
      System.err.println(ex.getMessage());
    }
    logger.debug("Finished Send...");
    return "OK";
  }
  
  private String[] getEmails(List<EmailToNotify> emailsToNotify) {
    List<String> emails = new ArrayList<String>();
    for(EmailToNotify email : emailsToNotify) {
      emails.add(email.getEmail());
    }
    return emails.toArray(new String[emails.size()]);
  }
}
