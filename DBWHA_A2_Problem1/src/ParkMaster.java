public class ParkMaster {
    private String parkId;
    private String parkName;
    private String parkRegion;

    public ParkMaster(String parkId, String parkName, String parkRegion) {
        this.parkId = parkId;
        this.parkName = parkName;
        this.parkRegion = parkRegion;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getParkRegion() {
        return parkRegion;
    }

    public void setParkRegion(String parkRegion) {
        this.parkRegion = parkRegion;
    }

    @Override
    public String toString() {
        return "parkId='" + parkId + '\'' +
                ", parkName='" + parkName + '\'' +
                ", parkRegion='" + parkRegion + '\'';
    }
}
