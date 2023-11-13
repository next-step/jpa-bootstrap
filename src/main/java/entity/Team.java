package entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private List<Member> members;

    public Team() {
    }

    public Team(List<Member> member) {
        this.members = member;
    }

    public Team(Long id, List<Member> member) {
        this.id = id;
        this.members = member;
    }

    public Long getId() {
        return id;
    }

    public List<Member> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", members=" + members +
                '}';
    }
}
