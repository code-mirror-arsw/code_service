package com.code_mirror.code_service.infrastructure.messaging;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FcmMessage {
    @JsonProperty("to")
    private String to;
    @JsonProperty("source")
    private NotificationType source;
    @JsonProperty("data")
    private Map<String,String> data;
}

