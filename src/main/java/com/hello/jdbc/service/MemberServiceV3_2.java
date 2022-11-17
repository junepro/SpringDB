package com.hello.jdbc.service;

import com.hello.jdbc.domain.Member;
import com.hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜잭션 - 트랜잭션 템플릿 - 트랜잭션 반복하는 코드 제거 할 수 있슴 (TRY ~CATCH)
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_2 {
    //private final DataSource dataSource;
    private final TransactionTemplate txTemplate; //사용할려면 transactionmanager 필요
    private final MemberRepositoryV3 memberRepository;
   // private final PlatformTransactionManager transactionManager;


    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }


    public void accountTransfer(String fromId, String toId, int money) throws
            SQLException {

        txTemplate.executeWithoutResult((status)->{
            try {
                //비즈니스 로직
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
//         전체 로직이 위에 Template 하나로 해결
//        //트랜잭션 시작
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//
//        try {
//            //비즈니스 로직
//            bizLogic(fromId, toId, money);
//            transactionManager.commit(status); //성공시 커밋
//
//        } catch (Exception e) {
//            transactionManager.rollback(status); //실패시 롤백
//            throw new IllegalStateException(e);
//        }
//
        }
   //파라미터 순서 바꾸는거 ctrl+ f6
    private void bizLogic( String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById( fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);  //커넥션 풀 고려
                con.close();

            } catch (Exception e) {
                log.info("error",e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
