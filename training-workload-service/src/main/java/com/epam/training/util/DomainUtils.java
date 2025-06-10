package com.epam.training.util;

import com.epam.training.exception.DomainException;
import com.epam.training.exception.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DomainUtils {

    public String getCurrentUserUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        throw new DomainException("User not found");
    }

    public void checkUsername(String usernameToCheck) {
        if (!getCurrentUserUsername().equals(usernameToCheck)) {
            throw new ForbiddenException("You dont have permission to access this page");
        }
    }
}
