package com.example.PROTOTYPE2.shared.security;

import com.example.PROTOTYPE2.auth.entity.Researcher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class ResearcherDetailsImpl implements UserDetails {

    private final Integer id;
    private final String email;
    private final String password;
    private final String name;

    public ResearcherDetailsImpl(Integer id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static ResearcherDetailsImpl build(Researcher researcher) {
        return new ResearcherDetailsImpl(
                researcher.getResearcherId(),
                researcher.getEmail(),
                researcher.getPasswordHash(),
                researcher.getName()
        );
    }

    public Integer getId() { return id; }
    public String getName() { return name; }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_RESEARCHER"));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
