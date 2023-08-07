package com.Cang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Jdfcc
 * @Description FileEntity
 * @DateTime 2023/7/20 11:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {

    private Long id;

    private Long uploader;

    private LocalDateTime uploadTime;

}
