package aero.cubox.api.domain.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

import aero.cubox.api.domain.enums.EnumYN;
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
@Table(name = "T_USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1287284372570055604L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "login_pwd")
    private String loginPwd;

    @Column(name = "user_nm")
    private String userNm;


}
