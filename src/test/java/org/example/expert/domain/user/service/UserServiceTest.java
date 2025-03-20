package org.example.expert.domain.user.service;


import org.example.expert.domain.user.dto.response.UserSearchResponse;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;


//// 데이터 저장
//    // 저장할 데이터 개수
    private static final int BATCH_SIZE = 10000;

    @BeforeEach
    void setData() {
        // 데이터 저장
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < 1_000_000; i++) {
            String email = "email" + i+"@gmail.com";
            String nickname = "nickname" + i;
            String password = "password";

            User user = new User(email, password, UserRole.ROLE_USER, nickname);
            userList.add(user);

            // 저장
            if (userList.size() % BATCH_SIZE == 0) {
                userRepository.saveAll(userList);
                userList.clear(); // 저장 후 리스트 비우기
            }
        }

    }
//
//    // 인덱스 설정
    @BeforeEach
    void setIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE INDEX idx_nickname ON users(nickname)");

        } catch (Exception e) {
            throw new RuntimeException("오류 발생: " + e.getMessage());
        }
    }
//
    @Test
    void testFindUserByNickName() {
        // given
        String searchNickname = "nickname1";

        // when
        long startTime = System.nanoTime(); // 시작
        List<UserSearchResponse> result = userService.findUserByNickName(searchNickname);
        long endTime = System.nanoTime(); // 끝

        long elapsedTime = (endTime - startTime) / 1_000_000;
        System.out.println("검색 시간: " + elapsedTime + "ms");

        // then
        assertThat(result).isNotEmpty();
        System.out.println("검색 결과: " + result.size() + "명");


        // 일부 데이터 출력
        result.stream().limit(5).forEach(user ->
                System.out.println(user.getNickname() + " - " + user.getEmail()));
    }


}