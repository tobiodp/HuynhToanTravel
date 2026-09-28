package vn.huynhtoantravel.service;
import com.google.zxing.BarcodeFormat; import com.google.zxing.client.j2se.MatrixToImageWriter; import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service; import java.io.ByteArrayOutputStream; import java.util.Base64;
@Service
public class QrCodeService {
 public byte[] png(String text, int size){
   try { var matrix=new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE,size,size); var out=new ByteArrayOutputStream(); MatrixToImageWriter.writeToStream(matrix,"PNG",out); return out.toByteArray(); }
   catch(Exception e){ throw new IllegalStateException("Không tạo được QR",e); }
 }
 public String dataUri(String text, int size){ return "data:image/png;base64,"+Base64.getEncoder().encodeToString(png(text,size)); }
}
