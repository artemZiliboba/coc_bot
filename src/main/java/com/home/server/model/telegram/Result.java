package com.home.server.model.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {
    private Long id;
    private String is_bot;
    private String first_name;
    private String username;
    private String can_join_groups;
    private String can_read_all_group_messages;
    private String supports_inline_queries;
}
