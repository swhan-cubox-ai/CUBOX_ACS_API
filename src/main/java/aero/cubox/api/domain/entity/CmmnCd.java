package aero.cubox.api.domain.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "T_CMMN_CD")
public class CmmnCd implements Serializable {

    private static final long serialVersionUID = 1758041395891941432L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "cd_typ")
    private String cdTyp;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_nm")
    private String cdNm;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;


}
