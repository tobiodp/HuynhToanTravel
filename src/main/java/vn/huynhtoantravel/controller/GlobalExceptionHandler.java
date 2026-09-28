package vn.huynhtoantravel.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request, Model model) {
        log.warn("IllegalArgumentException on [{}]: {}", request.getRequestURI(), ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage(), "status", 400));
        }
        model.addAttribute("status", 400);
        model.addAttribute("error", "Yêu cầu không hợp lệ");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(SecurityException.class)
    public Object handleSecurity(SecurityException ex, HttpServletRequest request, Model model) {
        log.warn("SecurityException on [{}]: {}", request.getRequestURI(), ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage(), "status", 403));
        }
        model.addAttribute("status", 403);
        model.addAttribute("error", "Truy cập bị từ chối");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public Object handleIllegalState(IllegalStateException ex, HttpServletRequest request, Model model) {
        log.warn("IllegalStateException on [{}]: {}", request.getRequestURI(), ex.getMessage());
        if (isApiRequest(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage(), "status", 400));
        }
        model.addAttribute("status", 400);
        model.addAttribute("error", "Trạng thái không hợp lệ");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled Exception on [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Đã xảy ra lỗi hệ thống: " + ex.getMessage(), "status", 500));
        }
        model.addAttribute("status", 500);
        model.addAttribute("error", "Lỗi máy chủ nội bộ");
        model.addAttribute("message", "Đã có lỗi xảy ra trong quá trình xử lý yêu cầu của bạn. Vui lòng thử lại sau.");
        return "error";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return (uri != null && uri.startsWith("/api/")) || (accept != null && accept.contains("application/json"));
    }
}
