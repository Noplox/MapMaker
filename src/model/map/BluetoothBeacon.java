package model.map;

import java.io.Serializable;
import java.util.UUID;

public class BluetoothBeacon implements Serializable {

    private Location location;
    private UUID id;
    private int txPower;

    public BluetoothBeacon(Location location, UUID id, int txPower) {
        this.location = location;
        this.id = id;
        this.txPower = txPower;
    }

    public BluetoothBeacon() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }
}
