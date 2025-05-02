package github.keshaparrot.fitnesshelper.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException {


    public DuplicateEmailException(String email) {
        super("user with email "+ email + " already exists");
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}

