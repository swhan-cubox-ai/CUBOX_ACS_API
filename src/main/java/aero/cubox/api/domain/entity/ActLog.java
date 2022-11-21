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
@Table(name = "T_ACT_LOG")
public class ActLog implements Serializable {

    private static final long serialVersionUID = 4419617238793108742L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "act_typ")
    private String actTyp;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "menu_nm")
    private String menuNm;

    @Column(name = "activities")
    private String activities;

    @Column(name = "created_at")
    private Timestamp createdAt;

}

