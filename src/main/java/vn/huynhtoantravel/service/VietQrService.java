package vn.huynhtoantravel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VietQrService {
    @Value("${app.vietqr.bank-id}")
    private String bankId;

    @Value("${app.vietqr.account-no}")
    private String accountNo;

    @Value("${app.vietqr.account-name}")
    private String accountName;

    @Value("${app.vietqr.template:compact2}")
    private String template;

    public String quickLink(long amount, String bookingCode) {
        return "https://img.vietqr.io/image/" + enc(bankId) + "-" + enc(accountNo) + "-" + enc(template) + ".png?amount=" + amount + "&addInfo=" + enc(bookingCode) + "&accountName=" + enc(accountName);
    }

    public String sepayQrLink(long amount, String bookingCode) {
        return "https://qr.sepay.vn/img?bank=" + enc(bankId) + "&acc=" + enc(accountNo) + "&template=" + enc(template) + "&amount=" + amount + "&des=" + enc(bookingCode);
    }

    public String getBankId() {
        return bankId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getTemplate() {
        return template;
    }

    public String getBankName() {
        if (bankId == null) return "Ngân hàng";
        return switch (bankId.trim().toUpperCase()) {
            case "MB", "MBBANK" -> "MB Bank (Ngân hàng Quân Đội)";
            case "VCB", "VIETCOMBANK" -> "Vietcombank (Ngoại thương Việt Nam)";
            case "TCB", "TECHCOMBANK" -> "Techcombank (Kỹ Thương)";
            case "ICB", "VIETINBANK", "CTG" -> "VietinBank (Công Thương Việt Nam)";
            case "BIDV" -> "BIDV (Đầu tư và Phát triển)";
            case "ACB" -> "ACB (Á Châu)";
            case "VPB", "VPBANK" -> "VPBank (Việt Nam Thịnh Vượng)";
            case "TPB", "TPBANK" -> "TPBank (Tiên Phong)";
            case "STB", "SACOMBANK" -> "Sacombank (Sài Gòn Thương Tín)";
            case "HDB", "HDBANK" -> "HDBank (Phát triển TP.HCM)";
            case "VIB" -> "VIB (Quốc tế Việt Nam)";
            case "SHB" -> "SHB (Sài Gòn - Hà Nội)";
            default -> bankId.toUpperCase();
        };
    }

    private String enc(String s) {
        return URLEncoder.encode(s != null ? s : "", StandardCharsets.UTF_8);
    }
}
