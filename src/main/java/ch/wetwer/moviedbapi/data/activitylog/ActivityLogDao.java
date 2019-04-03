package ch.wetwer.moviedbapi.data.activitylog;

import ch.wetwer.moviedbapi.data.DaoInterface;
import ch.wetwer.moviedbapi.data.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Wetwer
 * @project movie-score
 */

@Service
public class ActivityLogDao implements DaoInterface<ActivityLog> {

    private ActivityLogRepository activityLogRepository;

    public ActivityLogDao(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public ActivityLog getById(Long id) {
        return activityLogRepository.getOne(id);
    }

    @Override
    public List<ActivityLog> getAll() {
        return activityLogRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    public void save(ActivityLog activityLog) {
        activityLogRepository.save(activityLog);
    }

    public List<ActivityLog> getAllByUser(User user) {
        return activityLogRepository.findActivityLogsByUserOrderByTimestampDesc(user);
    }

    public void delete() {
        activityLogRepository.deleteAll();
    }
}
