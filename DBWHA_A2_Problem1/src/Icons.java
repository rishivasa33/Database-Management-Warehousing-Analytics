import java.sql.Blob;

public class Icons {
    private String iconId;
    private String iconName;
    private Blob icon_image;

    public Icons(String iconId, String iconName, Blob icon_image) {
        this.iconId = iconId;
        this.iconName = iconName;
        this.icon_image = icon_image;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Blob getIcon_image() {
        return icon_image;
    }

    public void setIcon_image(Blob icon_image) {
        this.icon_image = icon_image;
    }

    @Override
    public String toString() {
        return "iconId='" + iconId + '\'' +
                ", iconName='" + iconName + '\'';
    }
}
