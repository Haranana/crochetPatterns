package com.example.crochetPatterns.dtos;

import com.example.crochetPatterns.Validation.FieldMatch;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "{user.newPasswordsMismatch}")
public class UserPasswordChangeDTO {

    private Long id;

    @NotEmpty(message = "{user.currentPasswordEmpty}")
    private String currentPassword;

    @NotEmpty(message = "{user.newPasswordEmpty}")
    @Size(min = 6, message = "{user.newPasswordTooShort}")
    private String newPassword;

    @NotEmpty(message = "{user.confirmNewPasswordEmpty}")
    private String confirmNewPassword;
}