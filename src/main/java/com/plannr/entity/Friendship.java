package com.plannr.entity;

import com.plannr.enums.FriendshipStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


//unique constraint also in DB
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "friend_id"})
})
@Getter
@Setter
@ToString
@Entity
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //unidirectional relationship with User
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private User user1;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "friend_id", nullable = false)
    private User user2;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // Keeps track of the user that sent the request for use in the front end

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status", nullable = false)
    private FriendshipStatusEnum status = FriendshipStatusEnum.APPROVED;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    // Custom setters ensure ordering prefer constructor
    public void setUser1(User user) {
        if (this.user2 != null && user.getId() > this.user2.getId()) {
            throw new IllegalArgumentException("User1 ID must be less than User2 ID");
        }
        this.user1 = user;
    }

    public void setUser2(User user) {
        if (this.user1 != null && user.getId() < this.user1.getId()) {
            throw new IllegalArgumentException("User2 ID must be greater than User1 ID");
        }
        this.user2 = user;
    }

    //constructor ensures no duplicate entries by comparing user ids and enforcing user1 < user2
    public Friendship(User user1, User user2, User sender) {
        this.sender = sender;

        if (user1.getId() == null || user2.getId() == null) {
            throw new IllegalArgumentException("Users must have IDs before creating a friendship.");
        }
        // Ensure consistent ordering
        if (user1.getId() < user2.getId()) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
    }

    public Friendship() {
    }
}