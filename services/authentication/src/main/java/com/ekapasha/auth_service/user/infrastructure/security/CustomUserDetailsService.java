package com.ekapasha.auth_service.user.infrastructure.security;

import com.ekapasha.auth_service.user.domain.entity.User;
import com.ekapasha.auth_service.user.domain.repository.UserReadRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserReadRepository userReadRepository;

  /**
   * Locates the user based on the username. In the actual implementation, the search
   * possibly be case sensitive, or case insensitive depending on how the
   * implementation instance is configured. In this case, the <code>UserDetails</code>
   * object that comes back may have a username that is of a different case than what
   * was actually requested.
   *
   * @param username the username identifying the user whose data is required.
   * @return a fully populated user record (never <code>null</code>)
   * @throws UsernameNotFoundException if the user could not be found or the user has no
   *                                   GrantedAuthority
   */
  @Override
  public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    User user =
        this.userReadRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    List<GrantedAuthority> authorities = List.of();

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getHashedPassword(), authorities);
  }
}
