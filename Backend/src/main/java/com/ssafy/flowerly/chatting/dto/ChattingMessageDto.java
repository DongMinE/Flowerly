package com.ssafy.flowerly.chatting.dto;

import com.ssafy.flowerly.entity.ChattingMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChattingMessageDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long chattingId;
        private Long memberId;
        private String type;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long memberId;
        private String type;
        private String content;
        private LocalDateTime sendTime;

        public static ChattingMessageDto.Response of(ChattingMessage chattingMessage) {
            return Response.builder()
                    .memberId(chattingMessage.getMemberId())
                    .type(chattingMessage.getType())
                    .content(chattingMessage.getContent())
                    .sendTime(chattingMessage.getSendTime())
                    .build();
        }
    }
}
