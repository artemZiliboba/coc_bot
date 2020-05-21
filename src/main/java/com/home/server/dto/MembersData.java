package com.home.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class MembersData {
    private List<OneMember> oneMemberList;
}
