package axis.axis21.sanchita.axisapp.Objects;

public class EventCategory {
    private String title;
    private String image;

    public EventCategory() {
    }

    public EventCategory(String title, String image) {
        this.title = title;
        this.image = image;
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
}
