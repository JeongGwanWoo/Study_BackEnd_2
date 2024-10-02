package studyloginjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyloginjwt.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
}
