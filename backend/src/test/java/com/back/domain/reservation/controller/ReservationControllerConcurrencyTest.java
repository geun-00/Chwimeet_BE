package com.back.domain.reservation.controller;

import com.back.BaseTestContainer;
import com.back.config.TestConfig;
import com.back.domain.reservation.common.ReservationStatus;
import com.back.domain.reservation.dto.UpdateReservationStatusReqBody;
import com.back.domain.reservation.repository.ReservationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;


@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@AutoConfigureMockMvc
@Sql({
        "/sql/categories.sql",
        "/sql/regions.sql",
        "/sql/members.sql",
        "/sql/posts.sql",
        "/sql/reservations.sql",
        "/sql/reviews.sql",
        "/sql/notifications.sql"
})
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ReservationControllerConcurrencyTest extends BaseTestContainer {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @WithUserDetails("user2@example.com")
    @DisplayName("예약 승낙 시 동시성 체크 테스트")
    void concurrentApprovalTest() throws Exception {
        Long reservation1Id = 8L;
        Long reservation2Id = 9L;

        UpdateReservationStatusReqBody reqBody = new UpdateReservationStatusReqBody(
                ReservationStatus.PENDING_PAYMENT,
                null, null, null, null, null, null, null
        );
        String content = objectMapper.writeValueAsString(reqBody);

        int threadCount = 2;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger status1 = new AtomicInteger();
        AtomicInteger status2 = new AtomicInteger();

        Runnable rawTask1 = () -> {
            try {
                startLatch.await();
                var result = mockMvc.perform(
                                patch("/api/v1/reservations/{id}/status", reservation1Id)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(content)
                        )
                        .andReturn();
                status1.set(result.getResponse().getStatus());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable rawTask2 = () -> {
            try {
                startLatch.await();
                var result = mockMvc.perform(
                                patch("/api/v1/reservations/{id}/status", reservation2Id)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(content)
                        )
                        .andReturn();
                status2.set(result.getResponse().getStatus());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                doneLatch.countDown();
            }
        };

        Runnable task1 = new DelegatingSecurityContextRunnable(rawTask1, SecurityContextHolder.getContext());
        Runnable task2 = new DelegatingSecurityContextRunnable(rawTask2, SecurityContextHolder.getContext());

        new Thread(task1).start();
        new Thread(task2).start();

        // 동시에 출발
        startLatch.countDown();
        doneLatch.await();

        // === 응답 코드 검증 ===
        // 순서를 모르는 상태에서 하나는 200, 하나는 409 여야 함
        assertThat(List.of(status1.get(), status2.get()))
                .containsExactlyInAnyOrder(200, 409);

        // === 최종 DB 상태 검증 ===
        var r1 = reservationRepository.findById(reservation1Id).orElseThrow();
        var r2 = reservationRepository.findById(reservation2Id).orElseThrow();

        long approvedCount = Stream.of(r1, r2)
                .filter(r -> r.getStatus() == ReservationStatus.PENDING_PAYMENT)
                .count();

        assertThat(approvedCount)
                .as("동일 기간 중 승인된 예약은 정확히 하나여야 한다")
                .isEqualTo(1);
    }
}
