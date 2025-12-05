package com.back.domain.reservation.dto;

import com.back.domain.reservation.common.ReservationDeliveryMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateReservationReqBody(
        @NotNull
        ReservationDeliveryMethod receiveMethod,
        String receiveAddress1,
        String receiveAddress2,

        @NotNull
        ReservationDeliveryMethod returnMethod,

        @Size(max = 5)
        List<Long> optionIds,

        @NotNull
        LocalDateTime reservationStartAt,
        @NotNull
        LocalDateTime reservationEndAt
) {
        public static UpdateReservationReqBody of(
                ReservationDeliveryMethod receiveMethod,
                String receiveAddress1,
                String receiveAddress2,
                ReservationDeliveryMethod returnMethod,
                List<Long> optionIds,
                LocalDateTime reservationStartAt,
                LocalDateTime reservationEndAt
        ) {
                return new UpdateReservationReqBody(
                        receiveMethod,
                        receiveAddress1,
                        receiveAddress2,
                        returnMethod,
                        optionIds,
                        reservationStartAt,
                        reservationEndAt
                );
        }
}
