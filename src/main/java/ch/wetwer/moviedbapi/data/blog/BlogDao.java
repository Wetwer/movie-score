package ch.wetwer.moviedbapi.data.blog;

import ch.wetwer.moviedbapi.data.DaoInterface;
import ch.wetwer.moviedbapi.data.user.User;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author Wetwer
 * @project movie-score
 */

@Service
public class BlogDao implements DaoInterface<Blog> {

    private BlogRepository blogRepository;

    public BlogDao(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public Blog getById(Long id) {
        return blogRepository.findBlogById(id);
    }

    @Override
    public List<Blog> getAll() {
        return blogRepository.findBlogsByOrderByTimestampDesc();
    }

    @Override
    public void save(Blog blog) {
        blogRepository.save(blog);
    }

    public void delete(Blog blog) {
        blogRepository.delete(blog);
    }

    public void createBlog(String title, String text, User user) {
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setText(text.replace("\r\n", "<br>"));
        blog.setUser(user);
        blog.setTimestamp(new Timestamp(new Date().getTime()));
        save(blog);
    }
}
