package com.example.PROTOTYPE2.security;

import com.example.PROTOTYPE2.entity.Researcher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//This is a Wrapper class that translates my Researcher Class into something
// Spring Security Can Understand, without this SS has no way to work with the
//researcher object during authentication.
public class ResearcherDetailsImpl implements UserDetails {
    //Spring Security has its own interface called UserDetails that it uses internally
    // - to represent an authenticated user.
    // When Spring Security wants to know "who is this person,
    // what are their roles, is their account active?"
    // it expects an object that implements UserDetails.

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
