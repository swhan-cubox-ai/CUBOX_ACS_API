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
@Table(name = "T_FIRE_HIST")
public class FireHist implements Serializable {

    private static final long serialVersionUID = -1758247128653460418L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "f1")
    private String f1;

    @Column(name = "f2")
    private String f2;

    @Column(name = "f3")
    private String f3;

    @Column(name = "f4")
    private String f4;

    @Column(name = "f5")
    private String f5;

    @Column(name = "f6")
    private String f6;

    @Column(name = "msg")
    private String msg;

    @Column(name = "fire_yn")
    private String fireYn;

    @Column(name = "created_at")
    private Timestamp createdAt;

}

