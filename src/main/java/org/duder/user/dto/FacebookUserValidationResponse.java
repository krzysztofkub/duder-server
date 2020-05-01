package org.duder.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FacebookUserValidationResponse {
    private String app_id;
    private String type;
    private String application;
    private Long data_access_expires_at;
    private Long expires_at;
    private Boolean is_valid;
    private Long issued_at;
    private Map<String, String> metadata;
    private List<String> scopes;
    private String user_id;
}

