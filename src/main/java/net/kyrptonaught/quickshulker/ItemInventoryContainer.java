package net.kyrptonaught.quickshulker;

public interface ItemInventoryContainer {

    int getUsedSlotInPlayerInv();

    default boolean hasItem() {
        return getUsedSlotInPlayerInv() >= 0;
    }

    void setUsedSlot(int playerInvSlotID);

}
