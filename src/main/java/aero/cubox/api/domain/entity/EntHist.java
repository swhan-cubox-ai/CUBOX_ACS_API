package aero.cubox.api.domain.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "T_ENT_HIST")
public class EntHist implements Serializable {

    private static final long serialVersionUID = -8500911657159176772L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Integer id;

    @Column(name = "evt_dt")
    private Timestamp evtDt;

    @Column(name = "ent_evt_typ")
    private String entEvtTyp;

    @Column(name = "terminal_cd")
    private String terminalCd;

    @Column(name = "emp_cd")
    private String empCd;

    @Column(name = "emp_nm")
    private String empNm;

    @Column(name = "face_id")
    private Integer faceId;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "card_class_typ")
    private String cardClassTyp;

    @Column(name = "card_state_typ")
    private String cardStateTyp;

    @Column(name = "card_tag_typ")
    private String cardTagTyp;

    @Column(name = "beg_dt")
    private Timestamp begDt;

    @Column(name = "end_dt")
    private Timestamp endDt;

    @Column(name = "auth_way_typ")
    private String authWayTyp;

    @Column(name = "match_score")
    private Float matchScore;

    @Column(name = "face_threshold")
    private Float faceThreshold;

    @Column(name = "capture_at")
    private Timestamp captureAt;

    @Column(name = "tag_at")
    private Timestamp tagAt;

    @Column(name = "tag_card_no")
    private Float tagCardNo;

    @Column(name = "tag_emp_cd")
    private Float tagEmpCd;

    @Column(name = "temper")
    private Float temper;

    @Column(name = "mask_confidence")
    private Float maskConfidence;

    @Column(name = "terminal_typ")
    private String terminalTyp;

    @Column(name = "door_cd")
    private String doorCd;

    @Column(name = "door_nm")
    private String doorNm;

    @Column(name = "building_cd")
    private String buildingCd;

    @Column(name = "building_nm")
    private String buildingNm;

    @Column(name = "dept_cd")
    private String deptCd;

    @Column(name = "dept_nm")
    private String deptNm;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}

