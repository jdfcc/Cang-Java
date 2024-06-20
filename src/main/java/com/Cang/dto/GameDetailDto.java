package com.Cang.dto;

import com.Cang.entity.Game;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameDetailDto
 * @DateTime 2024/5/30 9:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GameDetailDto extends Game implements Serializable {
    private String steamImages;
}
