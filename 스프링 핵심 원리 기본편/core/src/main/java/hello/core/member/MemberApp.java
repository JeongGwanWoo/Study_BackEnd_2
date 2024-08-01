package hello.core.member;

public class MemberApp {

    public static void main(String[] args) {
        MemberService memberService = new MemberServiceimpl();
        Member member = new Member(1L, "mamberA", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("find Member = " + findMember.getName());
    }
}
