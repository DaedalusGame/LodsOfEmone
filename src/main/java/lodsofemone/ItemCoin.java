package lodsofemone;

import com.feed_the_beast.mods.money.FTBMoney;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public abstract class ItemCoin extends Item {
    private long getValue(ItemStack stack) {
        if (stack.getItem() != this)
            return 0;
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null)
            return 0;
        else
            return compound.getLong("value");
    }

    private void setValue(ItemStack stack, long value) {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null)
            compound = new NBTTagCompound();
        compound.setLong("value",value);
        stack.setTagCompound(compound);
    }

    private ItemStack ofValue(long value) {
        ItemStack stack = new ItemStack(this);
        setValue(stack,value);
        return stack;
    }

    public abstract int[] getDenominations();

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int denomination : getDenominations()) {
                items.add(ofValue(denomination));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);

        int useCount = 1;
        if (player.isSneaking())
            useCount = heldItem.getCount();

        long value = getValue(heldItem) * useCount;
        FTBMoney.setMoney(player, FTBMoney.getMoney(player) + value);
        player.sendStatusMessage(FTBMoney.moneyComponent(FTBMoney.getMoney(player)),true);

        heldItem.shrink(useCount);

        return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", FTBMoney.moneyString(getValue(stack))).trim();
    }
}
