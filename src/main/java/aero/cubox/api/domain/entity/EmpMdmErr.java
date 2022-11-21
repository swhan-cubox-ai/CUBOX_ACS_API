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
@Table(name = "T_EMP_MDM_ERR")
public class EmpMdmErr implements Serializable {

    private static final long serialVersionUID = -8264423736734082213L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tbl_nm")
    private String tblNm;

    @Column(name = "cgpn_hr_sn")
    private String cgpnHrSn;

    @Column(name = "hr_no")
    private String hrNo;

    @Column(name = "error")
    private String error;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}

