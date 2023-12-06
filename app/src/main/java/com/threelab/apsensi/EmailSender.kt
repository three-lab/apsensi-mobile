package com.threelab.apsensi
//
//
//import android.se.omapi.Session
//import java.net.PasswordAuthentication
//import java.util.Properties
//import javax.mail.Authenticator
//import javax.mail.Message
//import javax.mail.MessagingException
//import javax.mail.Session
//import javax.mail.Transport
//import javax.mail.internet.InternetAddress
//import javax.mail.internet.MimeMessage
//
//class EmailSender {
//
//    private val username = "your_email@gmail.com"
//    private val password = "your_email_password"
//    private val smtpHost = "smtp.gmail.com"
//    private val smtpPort = "587"
//
//    fun sendOtpEmail(toEmail: String, otp: String) {
//        val properties = Properties()
//        properties["mail.smtp.auth"] = "true"
//        properties["mail.smtp.starttls.enable"] = "true"
//        properties["mail.smtp.host"] = smtpHost
//        properties["mail.smtp.port"] = smtpPort
//
//        val session: Session = Session.getInstance(properties, object : Authenticator() {
//            override fun getPasswordAuthentication(): PasswordAuthentication {
//                return PasswordAuthentication(username, password)
//            }
//        })
//
//        try {
//            val message = MimeMessage(session)
//            message.setFrom(InternetAddress(username))
//            message.addRecipient(Message.RecipientType.TO, InternetAddress(toEmail))
//            message.subject = "Verification Code"
//            message.setText("Your OTP is: $otp")
//
//            Transport.send(message)
//            println("Email sent successfully.")
//        } catch (e: MessagingException) {
//            e.printStackTrace()
//        }
//    }
//}
