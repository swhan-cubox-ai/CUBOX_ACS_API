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
@Table(name = "T_DOORALARM_HIST")
public class DoorAlarmHist implements Serializable {

    private static final long serialVersionUID = -437351157295571939L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "evt_dt")
    private Timestamp evtDt;

    @Column(name = "door_alarm_typ")
    private String doorAlarmTyp;

    @Column(name = "terminal_id")
    private Integer terminalId;

    @Column(name = "terminal_cd")
    private String terminalCd;

    @Column(name = "terminal_typ")
    private String terminalTyp;

    @Column(name = "model_nm")
    private String modelNm;

    @Column(name = "mgmt_num")
    private String mgmtNum;

    @Column(name = "ip_addr")
    private String ipAddr;

    @Column(name = "complex_auth_typ")
    private String complexAuthTyp;

    @Column(name = "door_id")
    private Integer doorId;

    @Column(name = "door_nm")
    private String doorNm;

    @Column(name = "building_id")
    private Integer buildingId;

    @Column(name = "building_nm")
    private String buildingNm;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}

