package hexlet.code.app.util;

import hexlet.code.app.exception.ResourceNotFoundExcepiton;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).get();
    }

    public boolean isOwner(Long id, String email) {
        var maybeUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExcepiton("This id " + id + " not found"));
        return maybeUser.getEmail().equals(email);
    }

    public boolean isExists(String email) {
        var maybeUser = userRepository.findByEmail(email);
        return maybeUser.isPresent();
    }
}
