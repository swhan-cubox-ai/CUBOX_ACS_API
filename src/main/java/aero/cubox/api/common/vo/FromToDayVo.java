package aero.cubox.api.common.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class FromToDayVo {

    public FromToDayVo(String fromDay, String toDay) {

        if ( StringUtils.isBlank(fromDay)) {
            this.fromDay = LocalDateTime.now().plusDays(-6).toString().substring(0, 10);
        } else {
            this.fromDay = fromDay;
        }
        if ( StringUtils.isBlank(toDay)) {
            this.toDay = LocalDateTime.now().toString().substring(0, 10);
        }else {
            this.toDay = toDay;
        }
    }

    public FromToDayVo(String fromDay, String toDay, int days) {

        LocalDateTime currentDateTime = LocalDateTime.now();

        if ( StringUtils.isBlank(fromDay)) {
            this.fromDay = currentDateTime.plusDays((days-1)*-1).toString().substring(0, 10);
        } else {
            this.fromDay = fromDay;
        }
        if ( StringUtils.isBlank(toDay)) {
            this.toDay = currentDateTime.toString().substring(0, 10);
        }else {
            this.toDay = toDay;
        }

    }

    private String fromDay;

    private String toDay;

}
