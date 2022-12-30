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
@Table(name = "TMP_EM_PBSVNT")
public class TmpEmPbsvnt implements Serializable {


    private static final long serialVersionUID = 7651698146269957826L;
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
