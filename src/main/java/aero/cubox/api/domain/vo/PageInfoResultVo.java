package aero.cubox.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.BindingResult;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class PageInfoResultVo<T> extends PageInfo<T> {

  public static final String OK = "ok";
  public static final String FAIL = "fail";

  private Integer status = 200;
  private String message = "";
  private String error = "";

  private String resultCode = PageInfoResultVo.OK;

  @JsonIgnore
  public boolean isOk() {
    return PageInfoResultVo.OK.equals(resultCode);
  }

  public PageInfoResultVo(List<T> list) {
    super(list);
  }

  public PageInfoResultVo(List<T> list, int navigatePages) {
    super(list, navigatePages);
  }

  public PageInfoResultVo(String resultCode, String message) {
    this.resultCode = resultCode;
    this.message = message;
  }

  public PageInfoResultVo(String resultCode, String message, String error) {
    this.resultCode = resultCode;
    this.message = message;
    this.error = error;
  }

  public PageInfoResultVo(String message, BindingResult bindingResult) {
    this.resultCode = PageInfoResultVo.FAIL;
    this.message = message;
    this.error = bindingResult.getAllErrors().toString();
  }

  public PageInfoResultVo(String message, Exception ex) {
    this.resultCode = PageInfoResultVo.FAIL;
    this.message = message;
    this.error = ex.toString();
  }

}
