# spring-boot-jpa-web-crud
Spring Boot JPA Web CRUD Article Management 
guide to how to build a simple web application for managing articles using Spring Boot, JPA (Java Persistence API), and a basic CRUD (Create, Read, Update, Delete) interface.

1. Project Setup:

    Create a Spring Boot Project:
        Use Spring Initializr (start.spring.io) to generate a new project.
        Select the following dependencies:
            Spring Web
            Spring Data JPA
            H2 Database (for development) or MySQL, PostgreSQL, etc.
            Thymeleaf (for templating) or another templating engine.
            Lombok(Optional but recommended for reducing boilerplate code)

    Configure Database:
        If using H2, Spring Boot will automatically configure it.
        For other databases, add the necessary driver dependency and configure application.properties or application.yml:
    Properties

    # application.properties (example for MySQL)
    spring.datasource.url=jdbc:mysql://localhost:3306/article_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=update # For development, use "create-drop" or "update"
    spring.jpa.show-sql=true # For debugging

2. Entity (Article):

    Create an Article entity class:
    Java

    import javax.persistence.Entity;
    import javax.persistence.GeneratedValue;
    import javax.persistence.GenerationType;
    import javax.persistence.Id;
    import java.time.LocalDateTime;

    @Entity
    public class Article {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String title;
        private String content;
        private LocalDateTime publicationDate;

        // Constructors, getters, setters...
    //Lombok annotations can be used to generate Constructors, getters, and setters.
    }

3. Repository (ArticleRepository):

    Create a JPA repository interface:
    Java

    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface ArticleRepository extends JpaRepository<Article, Long> {
    }

4. Service (ArticleService):

    Create a service class to handle business logic:
    Java

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;

    @Service
    public class ArticleService {

        @Autowired
        private ArticleRepository articleRepository;

        public List<Article> getAllArticles() {
            return articleRepository.findAll();
        }

        public Article getArticleById(Long id) {
            return articleRepository.findById(id).orElse(null);
        }

        public Article createArticle(Article article) {
            article.setPublicationDate(LocalDateTime.now());
            return articleRepository.save(article);
        }

        public Article updateArticle(Long id, Article updatedArticle) {
            Article existingArticle = articleRepository.findById(id).orElse(null);
            if (existingArticle != null) {
                existingArticle.setTitle(updatedArticle.getTitle());
                existingArticle.setContent(updatedArticle.getContent());
                return articleRepository.save(existingArticle);
            }
            return null;
        }

        public void deleteArticle(Long id) {
            articleRepository.deleteById(id);
        }
    }

5. Controller (ArticleController):

    Create a controller to handle HTTP requests:
    Java

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    @Controller
    @RequestMapping("/articles")
    public class ArticleController {

        @Autowired
        private ArticleService articleService;

        @GetMapping
        public String listArticles(Model model) {
            model.addAttribute("articles", articleService.getAllArticles());
            return "articles/list"; // Thymeleaf template
        }

        @GetMapping("/create")
        public String showCreateForm(Model model) {
            model.addAttribute("article", new Article());
            return "articles/create"; // Thymeleaf template
        }

        @PostMapping("/create")
        public String createArticle(@ModelAttribute Article article) {
            articleService.createArticle(article);
            return "redirect:/articles";
        }

        @GetMapping("/edit/{id}")
        public String showEditForm(@PathVariable Long id, Model model) {
            model.addAttribute("article", articleService.getArticleById(id));
            return "articles/edit"; // Thymeleaf template
        }

        @PostMapping("/edit/{id}")
        public String updateArticle(@PathVariable Long id, @ModelAttribute Article article) {
            articleService.updateArticle(id, article);
            return "redirect:/articles";
        }

        @GetMapping("/delete/{id}")
        public String deleteArticle(@PathVariable Long id) {
            articleService.deleteArticle(id);
            return "redirect:/articles";
        }
    }

6. Thymeleaf Templates:

    Create Thymeleaf templates in src/main/resources/templates/articles:
        list.html: Displays a list of articles.
        create.html: Form to create a new article.
        edit.html: Form to edit an existing article.

    Example (list.html):
    HTML

    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Articles</title>
    </head>
    <body>
        <h1>Articles</h1>
        <a href="/articles/create">Create New Article</a>
        <table>
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Publication Date</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="article : ${articles}">
                    <td th:text="${article.title}"></td>
                    <td th:text="${article.publicationDate}"></td>
                    <td>
                        <a th:href="@{/articles/edit/{id}(id=${article.id})}">Edit</a>
                        <a th:href="@{/articles/delete/{id}(id=${article.id})}">Delete</a>
                    </td>
                </tr>
            </tbody>
        </table>
    </body>
    </html>

7. Run the Application:

    Run the Spring Boot application.
    Access the application in your browser (e.g., http://localhost:8080/articles).

Enhancements:

    Validation: Add validation to the Article entity and controller.
    Pagination: Implement pagination for large lists of articles.
    Security: Add Spring Security for authentication and authorization.
    Styling: Improve the user interface with CSS and JavaScript.
    Exception Handling: Add global exception handling.
    Testing: Write unit and integration tests.
    Use a more robust database: H2 is great for development, but a production application would need a more robust database like Postgres or MySQL.
    Use a DTO (Data Transfer Object): Add DTOs to seperate the entity from the data sent to the client. This will allow you to control what data is exposed.
    Add a detail page: Add a page to view the full content of an individual article.
    Add categories or tags: Add the ability to categorize articles.
