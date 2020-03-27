package org.duder.user.rest;

import lombok.Builder;
import lombok.Data;
import org.duder.user.dto.Code;

@Data
@Builder
public class Response {
    private Code code;
    private String message;
}
