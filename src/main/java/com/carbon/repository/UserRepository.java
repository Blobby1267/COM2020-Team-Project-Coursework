package com.carbon.repository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
