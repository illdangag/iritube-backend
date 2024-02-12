package com.illdangag.iritube.server.data.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountInfoUpdate {
    @Size(min = 1, max = 100, message = "Nickname must be at least 1 character and less then 100 characters.")
    private String nickname;
}
