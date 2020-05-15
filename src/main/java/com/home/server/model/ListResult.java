package com.home.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListResult<I> {
    //private Paging paging;
    private List<I> items;

    public ListResult(List<I> list) {
        this.items = list;
    }

    private ListResult(List<I> items, Paging listInfo) {
        this.items = items;
        //this.paging = listInfo;
    }

    public static <T> ListResult<T> of(List<T> list) {
        return new ListResult<>(list);
    }
}
