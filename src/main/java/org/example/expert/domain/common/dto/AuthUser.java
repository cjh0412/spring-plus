package org.example.expert.domain.common.dto;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Slf4j
@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;


    public AuthUser(Long id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }

    public UserRole getUserRole() {
        log.info("AuthUser에서 getUserRole 호출 - authorities: {}", authorities);
        if (authorities == null || authorities.isEmpty()) {
            log.error("AuthUser에 저장된 권한이 없음");
            throw new IllegalArgumentException("권한을 찾을 수 없습니다.");
        }

        for (GrantedAuthority authority : authorities) {
            log.info("AuthUser에서 반환하는 권한: {}", authority.getAuthority());
            return UserRole.valueOf(authority.getAuthority());
        }
        throw new IllegalArgumentException("권한을 찾을 수 없습니다.");
    }

}
