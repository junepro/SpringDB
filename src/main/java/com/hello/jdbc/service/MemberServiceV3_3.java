package com.hello.jdbc.service;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;


/**
 * 트랜잭션 - 트랜잭션 템플릿 - 트랜잭션 반복하는 코드 제거 할 수 있슴 (TRY ~CATCH)
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 memberRepository;


    public MemberServiceV3_3(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Transactional //호출될때 트랜잭션 걸고 시작
    public void accountTransfer(String fromId, String toId, int money) throws
            SQLException {

                //비즈니스 로직
                bizLogic(fromId, toId, money);

        }
    private void bizLogic( String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById( fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }


    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
