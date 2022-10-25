package aero.cubox.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.BindingResult;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class ResultVo<T> {

  public static final String OK = "ok";
  public static final String FAIL = "fail";

  private Integer status = 200;
  private String message = ""; // 화면 표시용
  private String error = ""; // 상세 오류 메시지
  private T data = null;

  private String resultCode = ResultVo.OK;

  public static ResultVo ok()
  {
    ResultVo result = new ResultVo();
    return result;
  }

  public static ResultVo ok(Object data)
  {
    ResultVo result = new ResultVo(data);
    return result;
  }

  public static ResultVo fail(String message)
  {
    ResultVo result = new ResultVo(ResultVo.FAIL, message);
    return result;
  }

  public static ResultVo fail(String resultCode, String message, String error)
  {
    ResultVo result = new ResultVo(resultCode, message, error);
    return result;
  }

  public static ResultVo fail(String message, BindingResult bindingResult)
  {
    ResultVo result = new ResultVo(message, bindingResult);
    return result;
  }

  public static ResultVo fail(String message, Exception ex)
  {
    ResultVo result = new ResultVo(message, ex);
    return result;
  }

  public ResultVo(String resultCode, String message) {
    this.resultCode = resultCode;
    this.message = message;
  }

  private ResultVo(String resultCode, String message, String error) {
    this.resultCode = resultCode;
    this.message = message;
    this.error = error;
  }

  private ResultVo(String message, BindingResult bindingResult)
  {
    this.resultCode = ResultVo.FAIL;
    this.message = message;
    this.error = bindingResult.getAllErrors().toString();
  }

  private ResultVo(String message, Exception ex)
  {
    this.resultCode = ResultVo.FAIL;
    this.message = message;
    this.error = ex.toString();
  }

  private ResultVo(T data)
  {
    this.data = data;
  }
}
