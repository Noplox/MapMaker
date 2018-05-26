package view;

public class LevelComboItem {
    private String key;
    private String value;

    public LevelComboItem(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public LevelComboItem(model.map.Level level) {
        this.key = level.getName();
        this.value = "" + level.getFloorHeight();
    }

    @Override
    public String toString() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
