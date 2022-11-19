package aero.cubox.api.domain.entity;

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
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "T_EMP")
public class Emp implements Serializable {

    private static final long serialVersionUID = 1758041395891941432L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "emp_cd")
    private String empCd;

    @Column(name = "emp_nm")
    private String empNm;

    @Column(name = "dept_cd")
    private String deptCd;

    @Column(name = "dept_nm")
    private String deptNm;

    @Column(name = "instt_cd")
    private String insttCd;

    @Column(name = "instt_nm")
    private String insttNm;

    @Column(name = "belong_nm")
    private String belongNm;

    @Column(name = "face_id")
    private Integer faceId;

    @Column(name = "expired_dt")
    private Timestamp expiredDt;

    @Column(name = "mdm_dt")
    private Timestamp mdmDt;

    @Column(name = "delete_yn")
    private String deleteYn;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
