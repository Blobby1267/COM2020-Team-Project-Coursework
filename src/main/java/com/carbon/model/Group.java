package com.carbon.model;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


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
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
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
