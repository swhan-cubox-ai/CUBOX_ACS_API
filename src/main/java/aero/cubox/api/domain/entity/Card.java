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
@Table(name = "T_CARD")
public class Card implements Serializable {

    private static final long serialVersionUID = 1758041395891941432L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "emp_id")
    private Integer empId;

    @Column(name = "emp_cd")
    private String empCd;

    @Column(name = "bet_dt")
    private Timestamp betDt;

    @Column(name = "end_dt")
    private Timestamp endDt;

    @Column(name = "card_class_typ")
    private String cardClassTyp;

    @Column(name = "card_state_typ")
    private String cardStateTyp;

    @Column(name = "mdm_dt")
    private Timestamp mdmDt;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}

