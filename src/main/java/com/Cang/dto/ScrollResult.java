package com.Cang.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Jdfcc
 */
@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
