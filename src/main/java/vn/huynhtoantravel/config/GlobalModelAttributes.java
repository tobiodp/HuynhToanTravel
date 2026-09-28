package vn.huynhtoantravel.config;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAttributes {

    @org.springframework.beans.factory.annotation.Value("${app.google.client-id:684087082646-viidr5jkf7t6gcgu2nampcef0cevh6pf.apps.googleusercontent.com}")
    private String googleClientId;

    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal, Authentication authentication) {
        model.addAttribute("googleClientId", googleClientId);
        if (principal != null && authentication != null) {
            String email = principal.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("currentUserEmail", email);
            model.addAttribute("adminEmail", email);
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("currentUserEmail", null);
            model.addAttribute("adminEmail", null);
            model.addAttribute("isAdmin", false);
        }
    }
}
