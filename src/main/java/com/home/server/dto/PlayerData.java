package com.home.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerData {
    private Integer plr_id;
    private String username;
    private String tag;
    private String tag_clan;
    private Integer trophies;
    private Integer vs_trophies;
    private Integer th;
}
