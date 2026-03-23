package com.carbon.model;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name =  "groups")
public class Group {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String inviteCode;

    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_groups_owner"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    @ManyToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "fk_group_members_group")),
        inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_group_members_user"))
    )
    private Set<User> members = new HashSet<>();

    public int getId(){
        return id;
    }

    public User getOwner(){
        return owner;
    }

    public String getName(){
        return name;
    }

    public Set<User> getMembers(){
        return members;
    }

    public String getInviteCode(){
        return inviteCode;
    }

    public int getTotalPoints(){
        int points = 0;
        for(User u : members){
            points += u.getPoints();
        }
        return points;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setOwner(User owner){
        this.owner = owner;
    }

    public void setInviteCode(String inviteCode){
        this.inviteCode = inviteCode;
    }
}
