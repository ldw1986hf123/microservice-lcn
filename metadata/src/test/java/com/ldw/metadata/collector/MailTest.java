package com.ldw.metadata.collector;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@SpringBootTest
public class MailTest {
    static String HOST = "smtp.163.com"; // smtp服务器
    static String FROM = "yiyangyu111@163.com"; // 发件人地址
    static String USER = "yiyangyu111@163.com"; // 用户名
    static String PWD = "GOZLLKNGYMQBCEQE"; // 163的授权码
    static String SUBJECT = "手表服务抛异常了"; // 邮件标题
    static String[] TOS = new String[]{"ldw1986hf123@163.com"};

     /*   #mail
    mail.account = yiyangyu111@163.com
    mail.password = GOZLLKNGYMQBCEQE
    mail.smtp.host = smtp.163.com
    mail.send.milliseconds = 10000
    mail.content.url = http://daas-dev.deepexi.com/daas-management/index.html#/quality/query-task/sheet-details?taskId=taskIdParam
   */
     @Test
     public void inform() {
         Properties props = new Properties();
         props.put("mail.smtp.host", HOST);//设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
         props.put("mail.smtp.auth", "true");  //需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
         Session session = Session.getDefaultInstance(props);//用props对象构建一个session
         session.setDebug(true);
         MimeMessage message = new MimeMessage(session);//用session为参数定义消息对象
         try {
             message.setFrom(new InternetAddress(FROM));// 加载发件人地址
             InternetAddress[] sendTo = new InternetAddress[TOS.length]; // 加载收件人地址
             for (int i = 0; i < TOS.length; i++) {
                 sendTo[i] = new InternetAddress(TOS[i]);
             }
             message.addRecipients(Message.RecipientType.TO, sendTo);
             message.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(FROM));//设置在发送给收信人之前给自己（发送方）抄送一份，不然会被当成垃圾邮件，报554错
             message.setSubject(SUBJECT);//加载标题
             Multipart multipart = new MimeMultipart();//向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
             BodyPart contentPart = new MimeBodyPart();//设置邮件的文本内容
             contentPart.setText("asda");
             multipart.addBodyPart(contentPart);
             message.setContent(multipart);//将multipart对象放到message中
             message.saveChanges(); //保存邮件
             Transport transport = session.getTransport("smtp");//发送邮件
             transport.connect(HOST, USER, PWD);//连接服务器的邮箱
             transport.sendMessage(message, message.getAllRecipients());//把邮件发送出去
             transport.close();//关闭连接
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
}
