package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "team_id")
    private Long teamId;

    public Member() {
    }

    public Member(String name) {
        this.name = name;
    }

    public Member(String name, Long teamId) {
        this.name = name;
        this.teamId = teamId;
    }

    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teamId=" + teamId +
                '}';
    }
}
