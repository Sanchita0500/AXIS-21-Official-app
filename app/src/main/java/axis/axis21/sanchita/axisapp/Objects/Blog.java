package axis.axis21.sanchita.axisapp.Objects;

public class Blog {
    private String title,author,date,content,image,email,insta;

    public Blog(){
    }

    public Blog(String title, String author, String date, String content, String image, String email, String insta) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.content = content;
        this.image = image;
        this.email = email;
        this.insta = insta;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInsta() {
        return insta;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }
}
