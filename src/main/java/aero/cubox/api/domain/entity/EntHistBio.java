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
@Table(name = "T_ENT_HIST_BIO")
public class EntHistBio implements Serializable {

    private static final long serialVersionUID = -8500911657159176772L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "endHistId")
    private Integer end_hist_id;

    @Column(name = "entFaceImg")
    private byte[] ent_face_img;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;


}

