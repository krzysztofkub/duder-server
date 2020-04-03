package org.duder.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.duder.user.dto.Code;

@Data
@Builder
@AllArgsConstructor
public class Response {
    private Code code;
    private String message;

    public Response(Code code) {
        this.code = code;
    }
}
