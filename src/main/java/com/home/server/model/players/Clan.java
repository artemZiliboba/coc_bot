package com.home.server.model.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Clan {
    private String tag;
    private String name;
    private Long clanLevel;
    private BadgeUrls badgeUrls;
}
