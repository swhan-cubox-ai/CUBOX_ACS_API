package aero.cubox.api.sync.vo;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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

    private Integer faceId;

    private Timestamp expiredDt;

    private Timestamp mdmDt;

    private String deleteYn;

    private Timestamp deletedAt;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String feature;
}
