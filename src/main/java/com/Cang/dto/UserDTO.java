package com.Cang.dto;


import com.Cang.entity.Blog;
import lombok.Data;

import java.util.List;


/**
 * @author Jdfcc
 */
@Data
public class UserDTO {
    private Long id;
    //    private String phone;
    private String email;
    private String nickName;
    private String icon;
    private List<Blog> blog;
}
