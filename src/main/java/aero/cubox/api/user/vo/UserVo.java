package aero.cubox.api.user.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1758041395891941432L;

    @ApiModelProperty(value="사용자 ID")
    private Integer id;

    @ApiModelProperty(value="로그인 ID")
    private String loginId;

    @JsonIgnore
    @ApiModelProperty(value="로그인 비밀번호")
    private String loginPwd;

    @ApiModelProperty(value="사용자명")
    private String userNm;

}
