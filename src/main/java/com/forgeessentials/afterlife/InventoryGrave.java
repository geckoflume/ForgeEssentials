package com.forgeessentials.afterlife;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import com.forgeessentials.api.UserIdent;

public class InventoryGrave extends InventoryBasic
{

    private Grave grave;

    public InventoryGrave(Grave grave)
    {
        super("Tombe de " + UserIdent.get(grave.owner).getUsername() + ".", false, Math.min(36, ((grave.inventory.size() - 1) / 9 + 1) * 9));
        this.grave = grave;
    }

    @Override
    public void openInventory()
    {
        grave.setOpen(true);
        for (int i = 0; i < getSizeInventory(); i++)
            setInventorySlotContents(i, grave.inventory.size() > 0 ? grave.inventory.remove(0) : null);
        super.openInventory();
    }

    @Override
    public void closeInventory()
    {
        for (int i = 0; i < getSizeInventory(); i++)
        {
            ItemStack is = getStackInSlot(i);
            if (is != null)
                grave.inventory.add(is);
        }
        grave.setOpen(false);
        if (grave.inventory.isEmpty())
            grave.remove(false);
        super.closeInventory();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

}
