package axis.axis21.sanchita.axisapp.Objects;

public class Workshop {
    private String title;
    private String image;
    private String date;
    private String venue;
    private String description;

    public Workshop() {
    }

    public Workshop(String title, String image, String date, String venue, String description) {
        this.title = title;
        this.image = image;
        this.date = date;
        this.venue = venue;
        this.description = description;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
