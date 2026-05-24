package ru.vlsu.ispi.movieproject.dto.user;

import lombok.Data;
import ru.vlsu.ispi.movieproject.enums.Role;

@Data
public class ChangeRoleRequest {
    private Role role;
}
