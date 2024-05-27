package sigurnost.dms.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "actions")
public class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "time", nullable = false)
    private Date time;

    @Basic
    @Column(name = "type", nullable = false, length = 255)
    private String type;

    @Basic
    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Basic
    @Column(name = "document", nullable = false, length = 255)
    private String document;

}