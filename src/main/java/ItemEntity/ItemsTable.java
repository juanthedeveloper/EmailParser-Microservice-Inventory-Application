package ItemEntity;

import javax.persistence.*;

@Entity
@NamedQuery(name = "searchById", query = "SELECT I FROM ItemsTable I WHERE I.itemId=?1")
@NamedQuery(name= "allItemsName",query = "SELECT I FROM ItemsTable I" )
public class ItemsTable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "item_id")
    private long itemId;
    @Basic
    @Column(name = "item_name")
    private String itemName;
    @Basic
    @Column(name = "item_quanity")
    private int itemQuanity;

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemQuanity() {
        return itemQuanity;
    }

    public void setItemQuanity(int itemQuanity) {
        this.itemQuanity = itemQuanity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemsTable that = (ItemsTable) o;

        if (itemId != that.itemId) return false;
        if (itemQuanity != that.itemQuanity) return false;
        if (itemName != null ? !itemName.equals(that.itemName) : that.itemName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (itemId ^ (itemId >>> 32));
        result = 31 * result + (itemName != null ? itemName.hashCode() : 0);
        result = 31 * result + itemQuanity;
        return result;
    }
}
