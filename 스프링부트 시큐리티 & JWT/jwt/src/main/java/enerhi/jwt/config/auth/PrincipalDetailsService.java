package enerhi.jwt.config.auth;

import enerhi.jwt.config.repository.UserRepository;
import enerhi.jwt.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8080/login => 여기서 동작을 안한다. form 로그인을 disable 했기 때문에.
// PrincipalDetailsService 를 사용하는 필터를 만들어 줌.
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PrincipalDetailsService의 loadUserByUsername()");
        User userEntity = userRepository.findByUsername(username);
        System.out.println("userEntity = " + userEntity);
        return new PrincipalDetails(userEntity);
    }
}
