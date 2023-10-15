package com.spyro.messenger.friends.repo;

import com.spyro.messenger.friends.entity.FriendRequest;
import com.spyro.messenger.friends.entity.FriendRequestCondition;
import com.spyro.messenger.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepo extends JpaRepository<FriendRequest, String> {
    @Query("select f from FriendRequest f where f.sender = ?1 and f.condition <> 'APPROVED'")
    List<FriendRequest> findSentRequests(User sender);
    @Transactional
    @Modifying
    @Query("delete from FriendRequest f where f.sender = ?1 or f.recipient = ?1")
    void deleteAllByUser(User user);
    @Query("select f from FriendRequest f where f.recipient = ?1 and f.condition <> 'APPROVED'")
    List<FriendRequest> findReceivedRequests(User recipient);
    @Query("select f from FriendRequest f where (f.sender = ?1 and f.recipient = ?2) or (f.sender = ?2 and f.recipient = ?1)")
    Optional<FriendRequest> findByTwoFriends(User sender, User recipient);

    @Query("select f1.recipient from FriendRequest f1 where f1.sender = ?1 and f1.condition = ?2 union " +
            "select f2.sender from FriendRequest f2 where f2.recipient = ?1 and f2.condition = ?2")
    List<User> findAllSimilarRequestsForUser(User user, FriendRequestCondition condition);

    default List<User> findAllFriends(User user) {
        return findAllSimilarRequestsForUser(user, FriendRequestCondition.APPROVED);
    }

    Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient);

    boolean existsBySenderAndRecipient(User sender, User recipient);
}
