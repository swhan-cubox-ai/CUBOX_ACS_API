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
@Table(name = "T_CARD_MDM_HIST")
public class CardMdmHist implements Serializable {

    private static final long serialVersionUID = 8419098876416702379L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tbl_nm")
    private String tblNm;

    @Column(name = "emp_cd")
    private String empCd;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "beg_dt")
    private Timestamp begDt;

    @Column(name = "end_dt")
    private Timestamp endDt;

    @Column(name = "card_class_typ")
    private String cardClassTyp;

    @Column(name = "card_state_typ")
    private String cardStateTyp;

    @Column(name = "card_sttus_se")
    private String cardSttusSe;

    @Column(name = "mdm_dt")
    private Timestamp mdmDt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}

