package backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ItemModel {
    @Id
    @GeneratedValue
    private Long id;

    private String itemImage;
    private String itemDescription;
    private Double itemRating;

    public ItemModel() {}

    public ItemModel(String itemImage, String itemDescription, Double itemRating) {
        this.itemImage = itemImage;
        this.itemDescription = itemDescription;
        this.itemRating = itemRating;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemImage() { return itemImage; }
    public void setItemImage(String itemImage) { this.itemImage = itemImage; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public Double getItemRating() { return itemRating; }
    public void setItemRating(Double itemRating) { this.itemRating = itemRating; }
}
