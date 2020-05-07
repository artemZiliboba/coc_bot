package com.kaptyuk.artem.base.model.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Spells {
    private String name;
    private Long level;
    private Long maxLevel;
    private String village;
}
