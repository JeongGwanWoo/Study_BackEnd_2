package hello.core.member;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//@Component //memoryMemberRepository (컴포넌트 맨앞 소문자로 바뀜)
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }
}
