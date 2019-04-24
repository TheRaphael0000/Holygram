package ch.hearc.holygram.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.hearc.holygram.models.User;
import ch.hearc.holygram.services.UserServiceImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserServiceImpl userService;

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userService.findByUsername(username);
		if (user == null)
			throw new UsernameNotFoundException(username);

		return new HolygramUserDetails(user);
	}
}