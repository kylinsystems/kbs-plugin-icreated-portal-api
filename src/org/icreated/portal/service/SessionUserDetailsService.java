package org.icreated.portal.service;

import org.icreated.portal.bean.SessionUser;
import org.icreated.portal.utils.IdempierePasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.icreated.portal.service.UserService;

@Service
public class SessionUserDetailsService implements UserDetailsService {
 
	@Autowired
	UserService userService;
	
	@Autowired
	private IdempierePasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
    	
        SessionUser user = userService.findSessionUserByValue(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        passwordEncoder.setSalt(user.getSalt());
        return user;
    }
}