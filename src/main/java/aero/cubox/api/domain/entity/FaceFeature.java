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
@Table(name = "T_FACE_FEATURE")
public class FaceFeature implements Serializable {

    private static final long serialVersionUID = -5047084224938358166L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "face_id")
    private Integer faceId;

    @Column(name = "emp_cd")
    private String empCd;

    @Column(name = "face_feature_typ")
    private String faceFeatureTyp;

    @Column(name = "feature")
    private String feature;

    @Column(name = "feature_mask")
    private String featureMask;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}


