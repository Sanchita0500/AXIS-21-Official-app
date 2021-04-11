package axis.axis21.sanchita.axisapp.Objects;

public class Feed {
    private String title;
    private String image;
    private String category;
    private String content;
    private String link;

    public Feed() {
    }

    public Feed(String title, String image, String category, String content, String link) {
        this.title = title;
        this.image = image;
        this.category = category;
        this.content = content;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
