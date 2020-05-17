package com.home.server.model.developer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
    private Integer code;
    private String message;
    private String detail;
}
