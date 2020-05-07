package com.home.server.model.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Achievements {
    private String name;
    private Long stars;
    private Long value;
    private Long target;
    private String info;
    private String completionInfo;
    private String village;
}
