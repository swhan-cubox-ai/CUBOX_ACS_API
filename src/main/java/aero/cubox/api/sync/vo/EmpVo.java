package aero.cubox.api.sync.vo;

import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmpVo implements Serializable {

    private String empCd;

    private String empNm;

    private String deptCd;

    private String deptNm;

    private String insttCd;

    private String insttNm;

    private String belongNm;

    private String cardClassTyp;

    private String cardStateTyp;

    private Integer faceId;

    private String expiredDt;

    private String mdmDt;

    private String deleteYn;

    private String deletedAt;

    private String createdAt;

    private String updatedAt;

    private String feature;

    private String featureMask;
}
