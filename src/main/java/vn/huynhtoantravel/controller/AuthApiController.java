package vn.huynhtoantravel.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import vn.huynhtoantravel.domain.Role;
import vn.huynhtoantravel.domain.User;
import vn.huynhtoantravel.repository.RoleRepository;
import vn.huynhtoantravel.repository.UserRepository;
import vn.huynhtoantravel.service.EmailService;

import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Value("${app.google.client-id:684087082646-viidr5jkf7t6gcgu2nampcef0cevh6pf.apps.googleusercontent.com}")
    private String configuredGoogleClientId;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    // Cache lưu OTP trong bộ nhớ: email -> OtpData
    private static final ConcurrentHashMap<String, OtpEntry> OTP_CACHE = new ConcurrentHashMap<>();

    private record OtpEntry(String code, long expiryTime) {}

    public AuthApiController(UserRepository userRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder,
                             AuthenticationManager authenticationManager,
                             UserDetailsService userDetailsService,
                             EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    /**
     * 1. Kiểm tra Email xem đã tồn tại tài khoản chưa
     */
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Địa chỉ email không hợp lệ!"));
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);

        if (userOpt.isPresent()) {
            User u = userOpt.get();
            return ResponseEntity.ok(Map.of(
                "exists", true,
                "email", normalizedEmail,
                "fullName", u.getFullName() != null ? u.getFullName() : "Khách hàng"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "exists", false,
                "email", normalizedEmail
            ));
        }
    }

    /**
     * 2. Đăng nhập bằng Email và Mật khẩu (AJAX)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, password)
            );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            Optional<User> uOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);
            String role = "ROLE_USER";
            if (uOpt.isPresent() && uOpt.get().getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()))) {
                role = "ROLE_ADMIN";
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng nhập thành công!",
                "redirect", "ROLE_ADMIN".equals(role) ? "/admin" : "/"
            ));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu không chính xác! Vui lòng thử lại hoặc đăng nhập bằng mã OTP."));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi đăng nhập: " + ex.getMessage()));
        }
    }

    /**
     * 3. Đăng ký tài khoản mới & Tự động đăng nhập luôn
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String fullName,
                                      @RequestParam(required = false) String phone,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng nhập địa chỉ email hợp lệ!"));
        }
        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu phải có ít nhất 6 ký tự!"));
        }
        if (fullName == null || fullName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng nhập họ và tên của bạn!"));
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email này đã được sử dụng!"));
        }

        User u = new User();
        u.setEmail(normalizedEmail);
        u.setFullName(fullName.trim());
        u.setPhone(phone != null ? phone.trim() : "");
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setEnabled(true);

        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_USER");
            r.setDescription("Customer");
            return roleRepository.save(r);
        });
        u.getRoles().add(userRole);

        userRepository.save(u);

        // Tự động đăng nhập người dùng ngay sau khi đăng ký
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(normalizedEmail);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng ký thành công và đã tự động đăng nhập!",
                "redirect", "/"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng ký thành công! Vui lòng đăng nhập.",
                "redirect", "/login"
            ));
        }
    }

    /**
     * 4. Gửi mã xác thực OTP qua Email
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Địa chỉ email không hợp lệ!"));
        }

        String normalizedEmail = email.trim().toLowerCase();
        // Tạo mã 6 chữ số ngẫu nhiên
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        long expiry = System.currentTimeMillis() + 5 * 60 * 1000; // 5 phút

        OTP_CACHE.put(normalizedEmail, new OtpEntry(otp, expiry));

        // In ra console log để dễ theo dõi/test
        System.out.println("=================================================");
        System.out.println("[HUYNH TOAN TRAVEL] OTP GỬI TỚI " + normalizedEmail + " LÀ: " + otp);
        System.out.println("=================================================");

        try {
            emailService.sendOtp(normalizedEmail, otp);
        } catch (Exception e) {
            System.err.println("Lỗi gửi mail: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Mã xác thực đã được gửi tới email " + normalizedEmail + " (Hiệu lực trong 5 phút).",
            "email", normalizedEmail,
            "debugOtp", otp
        ));
    }

    /**
     * 5. Xác thực OTP & Tự động Đăng nhập
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email,
                                       @RequestParam String otp,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        if (email == null || otp == null || otp.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng nhập đầy đủ email và mã xác thực!"));
        }

        String normalizedEmail = email.trim().toLowerCase();
        OtpEntry entry = OTP_CACHE.get(normalizedEmail);

        if (entry == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chưa có mã xác thực nào được gửi cho email này. Vui lòng bấm 'Gửi mã'!"));
        }

        if (System.currentTimeMillis() > entry.expiryTime()) {
            OTP_CACHE.remove(normalizedEmail);
            return ResponseEntity.badRequest().body(Map.of("error", "Mã xác thực đã hết hạn. Vui lòng yêu cầu mã mới!"));
        }

        if (!entry.code().equals(otp.trim())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mã xác thực 6 chữ số không đúng! Vui lòng kiểm tra lại."));
        }

        // Mã hợp lệ -> Xoá khỏi cache
        OTP_CACHE.remove(normalizedEmail);

        // Kiểm tra tài khoản trong database; nếu chưa có thì tự động tạo tài khoản nhanh
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);
        User u;
        if (userOpt.isEmpty()) {
            u = new User();
            u.setEmail(normalizedEmail);
            String defaultName = normalizedEmail.split("@")[0];
            if (defaultName.length() > 1) {
                defaultName = Character.toUpperCase(defaultName.charAt(0)) + defaultName.substring(1);
            }
            u.setFullName(defaultName);
            u.setPasswordHash(passwordEncoder.encode(new Random().nextInt(1_000_000) + "Abc@!"));
            u.setEnabled(true);

            Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
                Role r = new Role();
                r.setName("ROLE_USER");
                r.setDescription("Customer");
                return roleRepository.save(r);
            });
            u.getRoles().add(userRole);
            userRepository.save(u);
        } else {
            u = userOpt.get();
        }

        // Thiết lập phiên đăng nhập Spring Security
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(normalizedEmail);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            boolean isAdmin = u.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Xác thực mã OTP thành công! Đang chuyển hướng...",
                "redirect", isAdmin ? "/admin" : "/"
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi tạo phiên đăng nhập: " + ex.getMessage()));
        }
    }

    /**
     * 6. Xác thực đăng nhập Google thật bằng Google ID Token (Google Identity Services)
     */
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestParam String credential,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        if (credential == null || credential.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mã xác thực Google không hợp lệ!"));
        }

        try {
            // Xác thực token với Google tokeninfo endpoint
            String verifyUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + java.net.URLEncoder.encode(credential.trim(), java.nio.charset.StandardCharsets.UTF_8);
            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest googleReq = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(verifyUrl))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();

            java.net.http.HttpResponse<String> googleResp = httpClient.send(googleReq, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (googleResp.statusCode() != 200) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token Google không hợp lệ hoặc đã hết hạn!"));
            }

            com.fasterxml.jackson.databind.JsonNode payload = objectMapper.readTree(googleResp.body());

            String email = payload.has("email") ? payload.get("email").asText() : null;
            boolean emailVerified = payload.has("email_verified") && payload.get("email_verified").asBoolean();
            String name = payload.has("name") ? payload.get("name").asText() : "Khách hàng Google";
            String aud = payload.has("aud") ? payload.get("aud").asText() : "";

            if (email == null || !emailVerified) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email Google chưa được xác thực!"));
            }

            String normalizedEmail = email.trim().toLowerCase();
            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);
            User u;
            if (userOpt.isEmpty()) {
                u = new User();
                u.setEmail(normalizedEmail);
                u.setFullName(name);
                u.setPasswordHash(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                u.setEnabled(true);

                Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_USER");
                    r.setDescription("Customer");
                    return roleRepository.save(r);
                });
                u.getRoles().add(userRole);
                userRepository.save(u);
            } else {
                u = userOpt.get();
                if (!u.isEnabled()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên!"));
                }
            }

            // Tạo phiên đăng nhập Spring Security
            UserDetails userDetails = userDetailsService.loadUserByUsername(normalizedEmail);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            boolean isAdmin = u.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng nhập Google thành công!",
                "email", normalizedEmail,
                "fullName", u.getFullName(),
                "redirect", isAdmin ? "/admin" : "/"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi xác thực Google: " + e.getMessage()));
        }
    }
}
