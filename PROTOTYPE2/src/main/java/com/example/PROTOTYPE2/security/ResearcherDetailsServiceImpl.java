package com.example.PROTOTYPE2.security;



import com.example.PROTOTYPE2.entity.Researcher;
import com.example.PROTOTYPE2.repository.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResearcherDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ResearcherRepository researcherRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Researcher researcher = researcherRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Researcher not found: " + email));
        return ResearcherDetailsImpl.build(researcher);
    }
}
