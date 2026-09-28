package vn.huynhtoantravel.service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value; import org.springframework.mail.javamail.*; import org.springframework.stereotype.Service;
import vn.huynhtoantravel.domain.BookingMaster;
@Service
public class EmailService {
 private final JavaMailSender mailSender;
 @Value("${app.admin-email}") private String adminEmail;
 @Value("${spring.mail.username:thungo3011@gmail.com}") private String mailFrom;

 public EmailService(JavaMailSender mailSender){this.mailSender=mailSender;}
 public void sendPaymentConfirmed(BookingMaster b){
   send(b.getUser().getEmail(), "Huynh Toan Travel - Xác nhận "+b.getBookingCode(), customerHtml(b));
   send(adminEmail, "[ĐƠN MỚI] "+b.getBookingCode()+" - cần điều phối", adminHtml(b));
 }
 public void sendOtp(String to, String otp){
   String html = "<div style='font-family:sans-serif;max-width:500px;margin:auto;padding:24px;border:1px solid #e2e8f0;border-radius:16px;background:#f8fafc;'>"
       + "<h2 style='color:#031f32;'>Huỳnh Toàn Travel</h2>"
       + "<p>Mã xác thực đăng nhập của bạn là:</p>"
       + "<div style='font-size:32px;font-weight:900;letter-spacing:6px;color:#f59e0b;padding:16px 0;text-align:center;background:#fff;border-radius:12px;border:1px dashed #f59e0b;'>"
       + otp + "</div>"
       + "<p style='color:#64748b;font-size:13px;margin-top:16px;'>Mã này có hiệu lực trong vòng 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.</p></div>";
   send(to, "Mã xác thực Huỳnh Toàn Travel: " + otp, html);
 }
 private void send(String to,String subject,String html){
   if(to==null || to.isBlank()) return;
   try { 
     MimeMessage m=mailSender.createMimeMessage(); 
     MimeMessageHelper h=new MimeMessageHelper(m,true,"UTF-8"); 
     h.setFrom(new jakarta.mail.internet.InternetAddress(mailFrom, "Huỳnh Toàn Travel"));
     h.setTo(to); 
     h.setSubject(subject); 
     h.setText(html,true); 
     mailSender.send(m); 
     System.out.println("[EMAIL SUCCESS] Đã gửi thư tới: " + to + " | Tiêu đề: " + subject);
   } catch(Exception e){ 
     System.err.println("[EMAIL ERROR] Lỗi gửi mail tới " + to + ": " + e.getMessage());
     e.printStackTrace(); 
   }
 }
 private String customerHtml(BookingMaster b){
   long remain=Math.max(0,b.getGrandTotal()-b.getPaidAmount());
   return "<h2>Cảm ơn bạn đã đặt dịch vụ Huynh Toan Travel</h2><p>Mã đơn: <b>"+b.getBookingCode()+"</b></p><p>Đã nhận: "+money(b.getPaidAmount())+"</p><p>Còn lại: "+money(remain)+"</p><p>QR check-in được phát hành cho vé sau khi giao dịch xác nhận.</p>";
 }
 private String adminHtml(BookingMaster b){
   return "<h2>Đơn mới cần xử lý</h2><p>Mã: <b>"+b.getBookingCode()+"</b></p><p>Khách: "+b.getUser().getFullName()+" - "+b.getUser().getPhone()+"</p><p>Tổng: "+money(b.getGrandTotal())+"</p><p>Vui lòng kiểm tra tuyến, giờ đón, xe/phòng trong trang quản trị.</p>";
 }
 private String money(long v){return String.format("%,d ₫",v);}
}
