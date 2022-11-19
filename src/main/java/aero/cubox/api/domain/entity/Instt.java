package aero.cubox.api.domain.entity;

import com.google.api.client.util.DateTime;
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
@Table(name = "T_INSTT")
public class Instt implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "instt_cd")
    private String insttCd;

    @Column(name = "instt_nm")
    private String insttNm;

    @Column(name = "created_at")
    private DateTime createdAt;

    @Column(name = "updated_at")
    private DateTime updatedAt;


}
