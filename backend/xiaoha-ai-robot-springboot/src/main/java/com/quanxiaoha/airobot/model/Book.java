package com.quanxiaoha.airobot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    private String title;

    private String author;

    private Integer publishYear;
    //类型
    private List<String>genres;

    private String description;
}
