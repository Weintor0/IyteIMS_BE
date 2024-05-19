package edu.iyte.ceng.internship.ims.repository;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.Notification;
import edu.iyte.ceng.internship.ims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findNotificationByDestinationUser(User user);

    List<Notification> findNotificationBySourceUser(User user);
}
