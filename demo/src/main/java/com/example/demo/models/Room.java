import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms", schema = "public")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, length = 10)
    private String roomNumber;

    @Column(name = "area", nullable = false, precision = 5, scale = 2)
    private BigDecimal area;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    // Конструкторы
    public Room() {}

    public Room(String roomNumber, BigDecimal area, Integer maxGuests, String photoUrl) {
        this.roomNumber = roomNumber;
        this.area = area;
        this.maxGuests = maxGuests;
        this.photoUrl = photoUrl;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}