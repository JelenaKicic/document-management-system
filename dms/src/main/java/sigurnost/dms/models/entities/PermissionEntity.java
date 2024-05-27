package sigurnost.dms.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic
    @Column(name = "userId", nullable = false, length = 255)
    private String userId;

    @Basic
    @Column(name = "operation", nullable = false, length = 45)
    private String operation;

}