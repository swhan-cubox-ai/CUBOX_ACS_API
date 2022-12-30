package aero.cubox.api.domain.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "TMP_EM_CGPN")
public class TmpEmCgpn implements Serializable {


    private static final long serialVersionUID = 6686785329421490259L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "cgpn_hr_sn")
    private Integer cgpnHrSn;

    @Column(name = "issu_no")
    private String issuNo;

    @Column(name = "done_yn")
    private String doneYn;

}
